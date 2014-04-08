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
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.shared.TextMessage;

import java.util.ArrayList;
import java.util.Random;

public class MemberListAdapter extends ArrayAdapter<String>
{
	Context context;
	ArrayList<String> members;
	SharedPreferences prefs;

	public MemberListAdapter(Context context, int textViewResourceId, ArrayList<String> members)
	{
		super(context, textViewResourceId, members);
		this.members = members;
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
			v = vi.inflate(R.layout.member_row,null);

		TextView name = (TextView) v.findViewById(R.id.name);
		TextView text = (TextView) v.findViewById(R.id.text);
		ImageView userImage = (ImageView) v.findViewById(R.id.userImage);

		String url = "http://classmeapp.appspot.com/fileRequest?username="+members.get(position);
		UrlImageViewHelper.setUrlDrawable(userImage,url,R.drawable.user_icon,10000);

		name.setText(members.get(position));
		text.setText(""); //TODO make this something useful

		userImage.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Gson gson = new Gson();
				Random rand = new Random();
				Intent intent = new Intent(context,ChatActivity.class);
				Bundle bundle = new Bundle();
				ArrayList<String> chosenContacts = new ArrayList<String>();
				chosenContacts.add(members.get(position));
				bundle.putString("usernames",gson.toJson(chosenContacts));
				bundle.putString("id",String.valueOf(rand.nextInt(10000000)));
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		return  v;
	}
}