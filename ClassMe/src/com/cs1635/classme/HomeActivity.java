package com.cs1635.classme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.TextMessage;
import com.shared.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends ActionBarActivity
{
	HomeActivity activity = this;
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	String SENDER_ID = "856052645219";
	static final String TAG = "ClassMe";
	String regid;
	SharedPreferences prefs;
	GoogleCloudMessaging gcm;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		prefs = PreferenceManager.getDefaultSharedPreferences(activity);

		Gson gson = new Gson();
		User user = gson.fromJson(prefs.getString("userObject",""),User.class);
		ListView myClasses = (ListView) findViewById(R.id.myClasses);
		myClasses.setAdapter(new ArrayAdapter<String>(this,R.layout.class_row,R.id.courseName,user.getCourseList()));

		BuckCourse.resetPosition();

		ViewGroup classRow = (ViewGroup) findViewById(R.id.classRow);
		classRow.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(activity, BuckCourse.class);
				startActivity(intent);
			}
		});

		//find all chat histories
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Map<String, ?> keys = prefs.getAll();
		ArrayList<ArrayList<TextMessage>> recentChats = new ArrayList<ArrayList<TextMessage>>();
		if(keys != null)
		{
			for(Map.Entry<String, ?> entry : keys.entrySet())
			{
				if(entry.getKey().contains("-history")) //should be a serialized chat history
				{
					Type type = new TypeToken<ArrayList<TextMessage>>()
					{
					}.getType();
					ArrayList<TextMessage> messages = gson.fromJson(entry.getValue().toString(), type);
					recentChats.add(messages);
				}
			}
		}
		ListView recentChatsList = (ListView) findViewById(R.id.recentChats);
		recentChatsList.setAdapter(new RecentChatsAdapter(activity, 1, recentChats));

		gcm = GoogleCloudMessaging.getInstance(this);
		regid = getRegistrationId();
		if(regid.isEmpty())
			new RegistrationTask().execute();
	}

	private void storeRegistrationId(String regId)
	{
		Log.i(TAG, "Saving regId");
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.commit();
	}

	private String getRegistrationId()
	{
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if(registrationId.isEmpty())
		{
			Log.i(TAG, "Registration not found.");
			return "";
		}
		int currentVersion = 0;
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		try
		{
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			currentVersion = pInfo.versionCode;
		}
		catch(Exception ex)
		{
			Log.i(TAG,"Could not get current app version");
		}
		if(registeredVersion != currentVersion)
		{
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if(id == R.id.logout)
		{
			SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
			edit.remove("loggedIn");
			edit.commit();
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			return true;
		}
		if(id == R.id.action_settings)
		{
			Intent intent = new Intent(this, Preferences.class);
			startActivity(intent);
			return true;
		}
		if(id == R.id.search)
		{
			Intent intent = new Intent(this, SearchActivity.class);
			startActivity(intent);
		}
		if(id == R.id.chat)
		{
			startActivity(new Intent(this, ChatListActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	private class RegistrationTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params)
		{
			try
			{
				if(gcm == null)
					gcm = GoogleCloudMessaging.getInstance(activity);

				regid = gcm.register(SENDER_ID);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
				nameValuePairs.add(new BasicNameValuePair("username", prefs.getString("loggedIn", "")));
				nameValuePairs.add(new BasicNameValuePair("id", regid));
				nameValuePairs.add(new BasicNameValuePair("addDrop", "add"));

				AppEngineClient.makeRequest("/gcmRegister", nameValuePairs);
				storeRegistrationId(regid);
			}
			catch(Exception ex)
			{
				Log.d("ClassMe", "Couldn't register on server");
			}
			return null;
		}
	}
}
