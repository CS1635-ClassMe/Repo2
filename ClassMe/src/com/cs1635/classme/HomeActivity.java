package com.cs1635.classme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.TextMessage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends ActionBarActivity
{
	HomeActivity activity = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

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
		Gson gson = new Gson();
		Map<String,?> keys = prefs.getAll();
		ArrayList<ArrayList<TextMessage>> recentChats = new ArrayList<ArrayList<TextMessage>>();
		if(keys != null)
		{
			for(Map.Entry<String,?> entry : keys.entrySet())
			{
				if(entry.getKey().contains("-history")) //should be a serialized chat history
				{
					Type type = new TypeToken<ArrayList<TextMessage>>(){}.getType();
					ArrayList<TextMessage> messages = gson.fromJson(entry.getValue().toString(), type);
					recentChats.add(messages);
				}
			}
		}
		ListView recentChatsList = (ListView) findViewById(R.id.recentChats);
		recentChatsList.setAdapter(new RecentChatsAdapter(activity,1,recentChats));
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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
			startActivity(new Intent(this,ChatListActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

}
