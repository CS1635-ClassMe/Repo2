package com.cs1635.classme;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.Course;
import com.shared.Post;
import com.shared.User;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ResultsActivity extends ActionBarActivity
{
	ResultsActivity activity = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);

		ListView postsList = (ListView) findViewById(R.id.postsList);
		ListView usersList = (ListView) findViewById(R.id.usersList);
		ListView coursesList = (ListView) findViewById(R.id.coursesList);

		ArrayList<Post> posts = new ArrayList<Post>();
		ArrayList<User> users = new ArrayList<User>();
		ArrayList<Course> courses = new ArrayList<Course>();

		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
		{
			Gson gson = new Gson();
			Type listOfResults = new TypeToken<ArrayList<Object>>(){}.getType();
			ArrayList<Object> results = gson.fromJson(bundle.getString("results"), listOfResults);
			if(results != null)
			{
				for(Object result : results)
				{
					if(result instanceof Post)
						posts.add((Post)result);
					if(result instanceof User)
						users.add((User)result);
					if(result instanceof Course)
						courses.add((Course)result);
				}

				//add adapters to lists
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.single_post, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}
}
