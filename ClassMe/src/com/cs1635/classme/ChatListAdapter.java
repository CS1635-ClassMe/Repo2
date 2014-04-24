package com.cs1635.classme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.shared.TextMessage;

import java.util.ArrayList;

public class ChatListAdapter extends ArrayAdapter<TextMessage>
{
	Context context;
	ArrayList<TextMessage> messages;
	SharedPreferences prefs;

	public ChatListAdapter(Context context, int textViewResourceId, ArrayList<TextMessage> messages)
	{
		super(context, textViewResourceId, messages);
		this.messages = messages;
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v;
		if(convertView != null)
		{
			if(!messages.get(position).getFrom().equals(prefs.getString("loggedIn",null)))
			{
				if(convertView.findViewById(R.id.you) != null)
					v = convertView;
				else
					v = vi.inflate(R.layout.chat_bubble_you,null);
			}
			else
			{
				if(convertView.findViewById(R.id.me) != null)
					v = convertView;
				else
					v = vi.inflate(R.layout.chat_bubble_me,null);
			}
		}
		else
		{
			if(!messages.get(position).getFrom().equals(prefs.getString("loggedIn",null)))
				v = vi.inflate(R.layout.chat_bubble_you, null);
			else
				v = vi.inflate(R.layout.chat_bubble_me,null);
		}

		TextView messageText = (TextView) v.findViewById(R.id.messageText);
		TextView timeStamp = (TextView) v.findViewById(R.id.timeStamp);
		ImageView userImage = (ImageView) v.findViewById(R.id.userImage);
		TextView username = (TextView) v.findViewById(R.id.username);

		messageText.setText(messages.get(position).getText());
		timeStamp.setText(messages.get(position).getTimestamp());
		userImage.setImageResource(R.drawable.user_icon);
		username.setText(messages.get(position).getFrom());

		String url = "http://classmeapp.appspot.com/fileRequest?username="+messages.get(position).getFrom();
		Ion.with(userImage).placeholder(R.drawable.user_icon).load("https://classmeapp.appspot.com/fileRequest?username=" + messages.get(position).getFrom());

        //CHeck not null

        Resources res = context.getResources();
        ImageView you_triangle = (ImageView) v.findViewById(R.id.you);
        ImageView me_triangle = (ImageView) v.findViewById(R.id.me);
        int youColor = res.getColor(R.color.lecture_list_background);
        int meColor = res.getColor(R.color.note_list_background);

        if(you_triangle != null) {
            you_triangle.setColorFilter(youColor, PorterDuff.Mode.SRC_ATOP);
        }

        if(me_triangle != null){
            me_triangle.setColorFilter(meColor, PorterDuff.Mode.SRC_ATOP);
        }




		return  v;
	}
}