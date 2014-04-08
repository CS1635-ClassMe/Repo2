package com.cs1635.classme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.internal.view.SupportMenuItem;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.Post;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GetPostsTask extends AsyncTask<String, Void, ArrayList<Post>>
{
	Context context;
	SupportMenuItem refresh;
	SharedPreferences prefs;
	ListView postList;

	public GetPostsTask(Context c, SupportMenuItem refresh, ListView postList)
	{
		context = c;
		this.refresh = refresh;
		this.postList = postList;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	protected void onPreExecute()
	{
		if(refresh != null)
			refresh.setActionView(R.layout.actionbar_indeterminate_progress);
	}

	@Override
	protected ArrayList<Post> doInBackground(String... params)
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("username", prefs.getString("loggedIn", "")));
		nameValuePairs.add(new BasicNameValuePair("classId", params[0]));
		nameValuePairs.add(new BasicNameValuePair("type", params[1]));
		nameValuePairs.add(new BasicNameValuePair("sort", params[2]));

		try
		{
			HttpResponse urlResponse = AppEngineClient.makeRequest("/getPosts", nameValuePairs);
			String response = EntityUtils.toString(urlResponse.getEntity());

			Gson gson = new Gson();
			Type collectionType = new TypeToken<Collection<Post>>(){}.getType();
			return gson.fromJson(response, collectionType);
		}
		catch(Exception e)
		{
			Log.e("ClassMe", "Error getting posts: " + e.getMessage());
		}

		return null;
	}

	@Override
	protected void onPostExecute(final ArrayList<Post> posts)
	{
		if(refresh != null)
			refresh.setActionView(null);

		//RelativeLayout layout = (RelativeLayout) postList.getParent();
		//TextView empty = (TextView) layout.findViewById(R.id.empty);

		if(posts != null && posts.size() > 0)
		{
			//empty.setVisibility(View.GONE);
			postList.setVisibility(View.VISIBLE);
			Collections.sort(posts, Post.PostScoreComparator);
			if(postList.getAdapter() != null)
			{
				PostViewAdapter adapter = (PostViewAdapter) postList.getAdapter();
				adapter.clear();
				for(Post post : posts) //addAll() requires API level 11
					adapter.add(post);
				adapter.notifyDataSetChanged();
			}
			else
				postList.setAdapter(new PostViewAdapter(context, R.layout.post, posts));

			postList.setClickable(true);
			postList.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					Intent intent = new Intent(context, SinglePostActivity.class);
					Bundle bundle = new Bundle();
					Gson gson = new Gson();
					bundle.putString("post", gson.toJson(posts.get(position)));
					intent.putExtras(bundle);
					context.startActivity(intent);

                    //BUCK(posts.get(position)).get;
				}
			});
		}
		else
		{
			//empty.setVisibility(View.VISIBLE);
			postList.setVisibility(View.GONE);
		}
	}
}
