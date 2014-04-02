package com.cs1635.classme;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.TextMessage;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatActivity extends ActionBarActivity
{
	ActionBar actionBar;
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	String SENDER_ID = "856052645219";
	static final String TAG = "ClassMe";

	GoogleCloudMessaging gcm;
	Context context;
	ChatActivity activity = this;

	String regid;
	EditText text;
	ArrayList<String> usernames;

	static SharedPreferences prefs;
	static ArrayList<TextMessage> messages = new ArrayList<TextMessage>();
	static ListView chatList;
	static String id = "";
	static boolean isResumed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		actionBar = getSupportActionBar();

		//get information about who we are chatting with
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
		{
			Gson gson = new Gson();

			Type listOfStrings = new TypeToken<ArrayList<String>>(){}.getType();
			usernames = gson.fromJson(bundle.getString("usernames"), listOfStrings);
			id = bundle.getString("id");
			//if an associated notification is showing, cancel it
			NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(Integer.parseInt(id));

			Type type = new TypeToken<ArrayList<TextMessage>>(){}.getType();
			if(prefs.contains(id+"-history")) //do we have any previous history for this conversation?
				messages = gson.fromJson(prefs.getString(id+"-history",null), type);

			String title = "";
			for(String username : usernames)
			{
				if(!username.equals(prefs.getString("loggedIn",null)))
					title += username+",";
			}
			if(title.endsWith(","))
				title = title.substring(0,title.length()-1);

			actionBar.setTitle(title);
		}
		else
		{
			Toast.makeText(activity,"Unable to get messages.",Toast.LENGTH_SHORT).show();
			finish();
		}

		chatList = (ListView) findViewById(R.id.chatList);
		chatList.setAdapter(new ChatListAdapter(activity,1,messages));

		ImageButton sendButton = (ImageButton) findViewById(R.id.sendButton);
		text = (EditText) findViewById(R.id.sendText);
		sendButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new SendTask().execute(text.getText().toString());
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
				addMessage(text.getText().toString(),prefs.getString("loggedIn",null),sdf.format(new Date()),usernames);
				text.setText("");
			}
		});

		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);

			if (regid.isEmpty()) {
				registerInBackground();
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}

	public static void addMessage(String message, String sender, String timeStamp, ArrayList<String> usernames)
	{
		messages.add(new TextMessage(message,sender,timeStamp,id,usernames));
		((ChatListAdapter)chatList.getAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		checkPlayServices();
		isResumed = true;
	}

	@Override
	public void onPause()
	{
		super.onPause();
		isResumed = false;
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p/>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 * registration ID.
	 */
	private String getRegistrationId(Context context)
	{
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if(registrationId.isEmpty())
		{
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = registeredVersion++;
		if(registeredVersion != currentVersion)
		{
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p/>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground()
	{
		new RegistrationTask().execute();
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context)
	{
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(ChatActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices()
	{
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(resultCode != ConnectionResult.SUCCESS)
		{
			if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
			{
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, 9000).show();
			}
			else
			{
				Log.i("ClassMe", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId   registration ID
	 */
	private void storeRegistrationId(Context context, String regId)
	{
		final SharedPreferences prefs = getGCMPreferences(context);
		Log.i(TAG, "Saving regId");
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.commit();
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. Not needed for this demo since the
	 * device sends upstream messages to a server that echoes back the message
	 * using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend()
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("username", prefs.getString("loggedIn","")));
		nameValuePairs.add(new BasicNameValuePair("id", regid));
		nameValuePairs.add(new BasicNameValuePair("addDrop", "add"));

		try
		{
			AppEngineClient.makeRequest("/gcmRegister", nameValuePairs);
		}
		catch(Exception ex)
		{
			Log.d("ClassMe","Couldn't register on server");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class RegistrationTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params)
		{
			String msg = "";
			try
			{
				if(gcm == null)
					gcm = GoogleCloudMessaging.getInstance(context);

				regid = gcm.register(SENDER_ID);
				msg = "Device registered, registration ID=" + regid;

				// You should send the registration ID to your server over HTTP,
				// so it can use GCM/HTTP or CCS to send messages to your app.
				// The request to your server should be authenticated if your app
				// is using accounts.
				sendRegistrationIdToBackend();

				// For this demo: we don't need to send it because the device
				// will send upstream messages to a server that echo back the
				// message using the 'from' address in the message.

				// Persist the regID - no need to register again.
				storeRegistrationId(context, regid);
			}
			catch(IOException ex)
			{
				msg = "Error :" + ex.getMessage();
				// If there is an error, don't just keep trying to register.
				// Require the user to click a button again, or perform
				// exponential back-off.
			}
			return msg;
		}

		@Override
		protected void onPostExecute(String msg)
		{
			//mDisplay.append(msg + "\n");
		}
	}

	private class SendTask extends AsyncTask<String,Void,Void>
	{
		@Override
		protected Void doInBackground(String... params)
		{
			Gson gson = new Gson();
			String time = gson.toJson(Calendar.getInstance());
			ArrayList<String> usernames = new ArrayList<String>();
			usernames.add(prefs.getString("loggedIn",""));
			for(String username : actionBar.getTitle().toString().split(","))
			{
				if(!usernames.contains(username))
					usernames.add(username);

			}
			String users = gson.toJson(usernames,new TypeToken<ArrayList<String>>(){}.getType());

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
			nameValuePairs.add(new BasicNameValuePair("text", params[0]));
			nameValuePairs.add(new BasicNameValuePair("sender", prefs.getString("loggedIn","")));
			nameValuePairs.add(new BasicNameValuePair("sentTime", time));
			nameValuePairs.add(new BasicNameValuePair("usernames", users));
			nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));

			try
			{
				AppEngineClient.makeRequest("/sendChat", nameValuePairs);
			}
			catch(Exception ex)
			{
				Log.d("ClassMe","Couldn't send chat message");
				Toast.makeText(activity,"Unable to send message.",Toast.LENGTH_SHORT);
				text.setText(params[0]);
			}
			return null;
		}
	}
}
