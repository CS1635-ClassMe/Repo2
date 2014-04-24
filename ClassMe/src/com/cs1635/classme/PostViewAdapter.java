package com.cs1635.classme;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.koushikdutta.ion.Ion;
import com.shared.Comment;
import com.shared.Post;

import java.util.ArrayList;
import java.util.Date;

public class PostViewAdapter extends ArrayAdapter<Post>
{
	Context context;
	ArrayList<Post> posts;

	public PostViewAdapter(Context context, int textViewResourceId, ArrayList<Post> posts)
	{
		super(context, textViewResourceId, posts);
		this.posts = posts;
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
			v = vi.inflate(R.layout.post, null);

		TextView title = (TextView) v.findViewById(R.id.title);
		TextView content = (TextView) v.findViewById(R.id.content);
		//WebView content = (WebView) v.findViewById(R.id.content);
		TextView username = (TextView) v.findViewById(R.id.from);
		ImageView profileImage = (ImageView) v.findViewById(R.id.profileImage);
		TextView time = (TextView) v.findViewById(R.id.time);
		TextView numComments = (TextView) v.findViewById(R.id.numComments);

		String streamLevel = posts.get(position).getClassId();
		title.setText(posts.get(position).getPostTitle());

		content.setText(Html.fromHtml(posts.get(position).getPostContent(), new Html.ImageGetter()
		{
			@Override
			public Drawable getDrawable(String source)
			{
				return context.getResources().getDrawable(R.drawable.ic_launcher);
			}
		},null));

		//content.loadDataWithBaseURL(null, posts.get(position).getPostContent(), "text/html", "utf-8", null);
		username.setText(posts.get(position).getUsername());
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

		Ion.with(profileImage).placeholder(R.drawable.user_icon).load("https://classmeapp.appspot.com/fileRequest?username=" + posts.get(position).getUsername());

		numComments.setText(String.valueOf(posts.get(position).getComments().size()));

		profileImage.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//new ProfileTask(activity).execute(posts.get(position).getUsername());
			}
		});

		if(posts.get(position).getComments().size() > 0)
		{
			ViewFlipper commentFlipper = (ViewFlipper) v.findViewById(R.id.commentFlipper);
			LinearLayout commentProfileLayout = (LinearLayout) v.findViewById(R.id.commenterProfileLayout);
			commentFlipper.removeAllViews();
			commentProfileLayout.removeAllViews();

			commentFlipper.setVisibility(View.VISIBLE);
			final ArrayList<View> profileImages = new ArrayList<View>(3);
			ArrayList<Comment> comments = posts.get(position).getComments();

			int start = comments.size() - 3;
			if(start < 0)
				start = 0;
			for(int i = start; i < comments.size(); i++)
			{
				View commentView = vi.inflate(R.layout.single_comment, null);
				TextView commentUsername = (TextView) commentView.findViewById(R.id.commentUsername);
				TextView commentContent = (TextView) commentView.findViewById(R.id.commentContent);
				commentUsername.setText(comments.get(i).getUsername());
				commentContent.setText(Html.fromHtml(comments.get(i).getContent(), null, null));
				commentFlipper.addView(commentView);

                if(posts.get(position).getType().equals("Discussion")){
                    commentView.setBackgroundColor(context.getResources().getColor(R.color.discuss_list_background));
                } else if(posts.get(position).getType().equals("Lecture")){
                    commentView.setBackgroundColor(context.getResources().getColor(R.color.lecture_list_background));
                } else if (posts.get(position).getType().equals("Note")){
                    commentView.setBackgroundColor(context.getResources().getColor(R.color.note_list_background));
                }

				View imageLayout = vi.inflate(R.layout.profile_image, null);
				ImageView image = (ImageView) imageLayout.findViewById(R.id.image);
				Ion.with(image).placeholder(R.drawable.user_icon).load("https://classmeapp.appspot.com/fileRequest?username=" + comments.get(i).getUsername());
				profileImages.add(imageLayout);
				commentProfileLayout.addView(imageLayout);


			}

			profileImages.get(0).findViewById(R.id.underline).setVisibility(View.VISIBLE);
			if(comments.size() > 1)
			{
				commentFlipper.setInAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
				commentFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
				commentFlipper.setFlipInterval(4000);
				commentFlipper.getInAnimation().setAnimationListener(new Animation.AnimationListener()
				{
					int count = 1;

					public void onAnimationStart(Animation animation)
					{
						if(count == 0)
						{
							profileImages.get(count).findViewById(R.id.underline).setVisibility(View.VISIBLE);
							profileImages.get(profileImages.size() - 1).findViewById(R.id.underline).setVisibility(View.GONE);
						}
						else
						{
							profileImages.get(count).findViewById(R.id.underline).setVisibility(View.VISIBLE);
							profileImages.get((count - 1)).findViewById(R.id.underline).setVisibility(View.GONE);
						}
						count++;
						if(count == profileImages.size())
							count = 0;
					}

					public void onAnimationRepeat(Animation animation)
					{
					}

					public void onAnimationEnd(Animation animation)
					{
					}
				});
				commentFlipper.startFlipping();
			}
		}
		return v;
	}
}