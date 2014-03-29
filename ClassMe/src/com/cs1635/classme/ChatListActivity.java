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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.TextMessage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class ChatListActivity extends ActionBarActivity
{
	ChatListActivity activity = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_list);

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

		TextView newChat = (TextView) findViewById(R.id.newChat);
		newChat.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new ContactChooserDialog(activity);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_list, menu);
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

}
