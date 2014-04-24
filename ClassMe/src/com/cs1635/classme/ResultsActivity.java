package com.cs1635.classme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
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

		Button createNew = (Button) findViewById(R.id.result_createnew);
		createNew.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(ResultsActivity.this,NewCourse.class));
			}
		});

		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
		{
			Gson gson = new Gson();
			Type listOfResults = new TypeToken<ArrayList<User>>(){}.getType();
			ArrayList<User> users = gson.fromJson(bundle.getString("users"), listOfResults);
			listOfResults = new TypeToken<ArrayList<Post>>(){}.getType();
			ArrayList<Post> posts = gson.fromJson(bundle.getString("posts"), listOfResults);
			listOfResults = new TypeToken<ArrayList<Course>>(){}.getType();
			ArrayList<Course> courses = gson.fromJson(bundle.getString("courses"), listOfResults);

			//add adapters to lists
			usersList.setAdapter(new ResultUserAdapter(this, 1, users));
			postsList.setAdapter(new ResultPostAdapter(this,1,posts));
			coursesList.setAdapter(new ResultCourseAdapter(this, 1, courses));
		}
	}
}
