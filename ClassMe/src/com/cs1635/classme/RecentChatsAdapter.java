package com.cs1635.classme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.shared.TextMessage;

import java.util.ArrayList;

public class RecentChatsAdapter extends ArrayAdapter<ArrayList<TextMessage>>
{
	Context context;
	ArrayList<ArrayList<TextMessage>> recentChats;
	SharedPreferences prefs;

	public RecentChatsAdapter(Context context, int textViewResourceId, ArrayList<ArrayList<TextMessage>> recentChats)
	{
		super(context, textViewResourceId, recentChats);
		this.recentChats = recentChats;
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v;
		if(convertView != null)
			v = convertView;
		else
			v = vi.inflate(R.layout.recent_chat,null);

		TextView name = (TextView) v.findViewById(R.id.name);
		TextView text = (TextView) v.findViewById(R.id.text);
		TextView time = (TextView) v.findViewById(R.id.time);
		ImageView userImage = (ImageView) v.findViewById(R.id.userImage);

		String url = "http://classmeapp.appspot.com/fileRequest?username="+recentChats.get(position).get(recentChats.get(position).size()-1).getFrom();
		Ion.with(userImage).placeholder(R.drawable.user_icon).load(url);

		String nameString = "";
		for(String username : recentChats.get(position).get(0).getUsernames())
		{
			if(!username.equals(prefs.getString("loggedIn","")) && !nameString.contains(username))
				nameString += username + ", ";
		}
		if(nameString.endsWith(", "))
			nameString = nameString.substring(0,nameString.length()-2);

		name.setText(nameString);
		text.setText(recentChats.get(position).get(recentChats.get(position).size()-1).getFrom()+ ": " +recentChats.get(position).get(recentChats.get(position).size()-1).getText());
		time.setText(recentChats.get(position).get(recentChats.get(position).size()-1).getTimestamp());

		v.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Gson gson = new Gson();
				Intent intent = new Intent(context,ChatActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("id",recentChats.get(position).get(0).getConversationId());
				bundle.putString("usernames",gson.toJson(recentChats.get(position).get(0).getUsernames()));
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});

		return  v;
	}
}