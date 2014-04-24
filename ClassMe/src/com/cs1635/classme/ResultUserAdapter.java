package com.cs1635.classme;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shared.Course;
import com.shared.User;

import java.util.ArrayList;

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

		return  v;
	}
}