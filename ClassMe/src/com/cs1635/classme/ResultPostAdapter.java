package com.cs1635.classme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shared.Course;
import com.shared.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class ResultPostAdapter extends ArrayAdapter<Post>
{
	Context context;
	ArrayList<Post> posts;

	public ResultPostAdapter(Context context, int textViewResourceId, ArrayList<Post> posts)
	{
		super(context, textViewResourceId, posts);
		this.context = context;
		this.posts = posts;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v;
		if(convertView != null)
			v = convertView;
		else
			v = vi.inflate(R.layout.result_post,null);

		TextView title = (TextView) v.findViewById(R.id.result_title);
		TextView body = (TextView) v.findViewById(R.id.result_body);
		TextView from = (TextView) v.findViewById(R.id.result_from);
		TextView time = (TextView) v.findViewById(R.id.result_time);

		title.setText(posts.get(position).getPostTitle());
		body.setText(posts.get(position).getPostContent());
		from.setText(posts.get(position).getUsername());
		String timeFormatString = "h:mm a";
		String editFormatString = "h:mm a";
		Date now = new Date(System.currentTimeMillis());
		if(posts.get(position).getPostTime().getDate() != now.getDate())
			timeFormatString = "MMM d, yyyy";
		if(posts.get(position).getLastEdit() != null && posts.get(position).getLastEdit().getDate() != now.getDate())
			editFormatString = "MMM d, yyyy";
		String timeString = String.valueOf(android.text.format.DateFormat.format(timeFormatString, posts.get(position).getPostTime()));
		if(posts.get(position).getLastEdit() != null)
			timeString += "(last edit - " + String.valueOf(android.text.format.DateFormat.format(editFormatString, posts.get(position).getLastEdit())) + ")";
		time.setText(timeString);

		v.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(context, SinglePostActivity.class);
				Bundle bundle = new Bundle();
				Gson gson = new Gson();
				bundle.putString("post", gson.toJson(posts.get(position)));
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});

		return  v;
	}
}