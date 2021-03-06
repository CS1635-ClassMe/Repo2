package com.cs1635.classme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.shared.Comment;
import com.shared.Post;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentViewAdapter extends ArrayAdapter<Comment>
{
	Context context;
	ArrayList<Comment> comments;
	Post post;
	CommentViewAdapter adapter = this;
	SharedPreferences prefs;
	ListView commentList;

	public CommentViewAdapter(Context context, int textViewResourceId, ArrayList<Comment> comments, Post post, ListView commentList)
	{
		super(context, textViewResourceId, comments);
		this.comments = comments;
		this.context = context;
		this.post = post;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.commentList = commentList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v;
		if(convertView != null)
			v = convertView;
		else
			v = vi.inflate(R.layout.comment, null);

		TextView content = (TextView) v.findViewById(R.id.content);
		TextView username = (TextView) v.findViewById(R.id.from);
		ImageView profileImage = (ImageView) v.findViewById(R.id.profileImage);
		TextView time = (TextView) v.findViewById(R.id.time);
		TextView plusOne = (TextView) v.findViewById(R.id.plusOne);
		ImageView accepted = (ImageView) v.findViewById(R.id.accepted);

		if(comments.get(position).isAccepted())
		{
			accepted.setVisibility(View.VISIBLE);
			accepted.setBackgroundResource(R.drawable.accepted);
		}
		else
			accepted.setVisibility(View.GONE);

		if(post.getUsername().equals(prefs.getString("loggedIn", "")))
		{
			accepted.setVisibility(View.VISIBLE);
			if(comments.get(position).isAccepted())
				accepted.setBackgroundResource(R.drawable.accepted);
			else
				accepted.setBackgroundResource(R.drawable.not_accepted);

			accepted.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if(comments.get(position).isAccepted())
						comments.get(position).setAccepted(false);
					else
					{
						for(Comment comment : comments)
							comment.setAccepted(false);
						comments.get(position).setAccepted(true);
					}

					new AcceptTask(comments.get(position).isAccepted()).execute(comments.get(position));
					adapter.notifyDataSetChanged();
				}
			});
		}
		else if(!comments.get(position).isAccepted())
			accepted.setVisibility(View.GONE);
		else if(comments.get(position).isAccepted())
		{
			accepted.setVisibility(View.VISIBLE);
			accepted.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Toast.makeText(context, "This comment was accepted by the postText author as a good answer", Toast.LENGTH_SHORT).show();
				}
			});
		}

		plusOne.setText("+" + comments.get(position).getPlusOnes());
		if(comments.get(position).getPlusOnes() == 0)
			plusOne.setVisibility(View.GONE);
		else
			plusOne.setVisibility(View.VISIBLE);
		if(comments.get(position).isPlusOned())
			plusOne.setTextColor(Color.RED);
		else
			plusOne.setTextColor(Color.parseColor("#ff979797"));

		content.setText(Html.fromHtml(comments.get(position).getContent() + "", new MyImageGetter(content, context), null));
		username.setText(comments.get(position).getUsername());
		String timeFormatString = "h:mm a";
		String editFormatString = "h:mm a";
		Date now = new Date(System.currentTimeMillis());
		if(comments.get(position).getCommentTime().getDate() != now.getDate())
			timeFormatString = "MMM d, yyyy";
		if(comments.get(position).getLastEdit() != null && comments.get(position).getLastEdit().getDate() != now.getDate())
			editFormatString = "MMM d, yyyy";
		String timeString = String.valueOf(android.text.format.DateFormat.format(timeFormatString, comments.get(position).getCommentTime()));
		if(comments.get(position).getLastEdit() != null)
			timeString += "(last edit - " + String.valueOf(android.text.format.DateFormat.format(editFormatString, comments.get(position).getLastEdit())) + ")";
		time.setText(timeString);
		Ion.with(profileImage).placeholder(R.drawable.user_icon).load("https://classmeapp.appspot.com/fileRequest?username=" + comments.get(position).getUsername());

		if(comments.get(position).getAttachmentKeys().size() > 0)
		{
			LinearLayout attachmentsLayout = (LinearLayout) v.findViewById(R.id.attachmentsLayout);
			attachmentsLayout.setVisibility(View.VISIBLE);

			for(int i=0; i<comments.get(position).getAttachmentNames().size(); i++)
			{
				String key = comments.get(position).getAttachmentKeys().get(i);
				String name = comments.get(position).getAttachmentNames().get(i);
				TextView attachment = new TextView(context);
				attachment.setText(Html.fromHtml("<a href=\"https://classmeapp.appspot.com/fileRequest?key=" + key + "\">" + name + "</a>"));
				attachment.setMovementMethod(LinkMovementMethod.getInstance());
				attachmentsLayout.addView(attachment);
			}
		}

		v.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new CommentOptionsDialog(context, comments.get(position), comments, commentList);
			}
		});

		return v;
	}

	private class AcceptTask extends AsyncTask<Comment, Void, Void>
	{
		boolean newValue;

		public AcceptTask(boolean newValue)
		{
			this.newValue = newValue;
		}

		@Override
		protected Void doInBackground(Comment... params)
		{
			Gson gson = new Gson();
			String comment = gson.toJson(params[0]);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("comment", comment));
			nameValuePairs.add(new BasicNameValuePair("postKey", post.getPostKey()));
			nameValuePairs.add(new BasicNameValuePair("newValue", String.valueOf(newValue)));

			try
			{
				AppEngineClient.makeRequest("/acceptComment", nameValuePairs);
			}
			catch(Exception ex)
			{
			}

			return null;
		}
	}
}