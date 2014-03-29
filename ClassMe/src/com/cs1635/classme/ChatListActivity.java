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
		if(keys != null)
		{
			for(Map.Entry<String,?> entry : keys.entrySet())
			{
				if(entry.getKey().contains("-history")) //should be a serialized chat history
				{
					Type type = new TypeToken<ArrayList<TextMessage>>(){}.getType();
					ArrayList<TextMessage> messages = gson.fromJson(entry.getValue().toString(), type);
					String title = "";
					String lastMessage = messages.get(messages.size()-1).getText();
					for(TextMessage message : messages)
					{
						if(!title.contains(message.getFrom()))
							title += message.getFrom() + ",";
					}
				}
			}
		}
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
