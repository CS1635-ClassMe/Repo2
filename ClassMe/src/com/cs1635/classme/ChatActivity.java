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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.TextMessage;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatActivity extends ActionBarActivity
{
	ActionBar actionBar;
	ChatActivity activity = this;
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
			else
				messages = new ArrayList<TextMessage>();

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
