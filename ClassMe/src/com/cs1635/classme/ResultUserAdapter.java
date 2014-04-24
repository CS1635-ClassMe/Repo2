package com.cs1635.classme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shared.Course;
import com.shared.User;

import java.util.ArrayList;
import java.util.Random;

public class ResultUserAdapter extends ArrayAdapter<User>
{
	Context context;
	ArrayList<User> users;

	public ResultUserAdapter(Context context, int textViewResourceId, ArrayList<User> users)
	{
		super(context, textViewResourceId, users);
		this.users = users;
		this.context = context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v;
		if(convertView != null)
			v = convertView;
		else
			v = vi.inflate(R.layout.result_user,null);

		TextView username = (TextView) v.findViewById(R.id.result_username);
		TextView realname = (TextView) v.findViewById(R.id.result_realname);

		username.setText(users.get(position).getUsername());
		realname.setText(users.get(position).getFirstName() + " " + users.get(position).getLastName());

		v.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Start a chat with " + users.get(position).getUsername())
						.setPositiveButton("Yes", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								Gson gson = new Gson();
								Random rand = new Random();
								Intent intent = new Intent(context,ChatActivity.class);
								Bundle bundle = new Bundle();
								ArrayList<String> chosenContacts = new ArrayList<String>();
								chosenContacts.add(users.get(position).getUsername());
								bundle.putString("usernames",gson.toJson(chosenContacts));
								bundle.putString("id",String.valueOf(rand.nextInt(10000000)));
								intent.putExtras(bundle);
								context.startActivity(intent);
							}
						})
						.setNegativeButton("No", null)
						.show();
			}
		});

		return  v;
	}
}