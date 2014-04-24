package com.cs1635.classme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.Course;
import com.shared.Post;
import com.shared.User;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends ActionBarActivity
{
	SearchActivity activity = this;
	CheckBox searchFilterUsers, searchFilterCourses, searchFilterPosts;
	EditText searchText;
	ArrayList<User> users = new ArrayList<User>();
	ArrayList<Post> posts = new ArrayList<Post>();
	ArrayList<Course> courses = new ArrayList<Course>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		Button submitSearch = (Button) findViewById(R.id.search_submit);
		searchFilterUsers = (CheckBox) findViewById(R.id.search_filter_Users);
		searchFilterCourses = (CheckBox) findViewById(R.id.search_filter_Courses);
		searchFilterPosts = (CheckBox) findViewById(R.id.search_filter_Posts);
		searchText = (EditText) findViewById(R.id.search_text);

		submitSearch.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if(searchText.getText().toString().length() < 1)
				{
					searchText.setError("Cannot be blank");
					return;
				}

				if(searchFilterCourses.isChecked())
				{
					courses = null;
					new SearchTask().execute("courses");
				}
				if(searchFilterUsers.isChecked())
				{
					users = null;
					new SearchTask().execute("users");
				}
				if(searchFilterPosts.isChecked())
				{
					posts = null;
					new SearchTask().execute("posts");
				}
				if(!searchFilterCourses.isChecked() && !searchFilterUsers.isChecked() && !searchFilterPosts.isChecked())
				{
					courses = null;
					users = null;
					posts = null;
					new SearchTask().execute("courses");
					new SearchTask().execute("users");
					new SearchTask().execute("posts");
				}
			}
		});
	}

	private class SearchTask extends AsyncTask<String,Void,Boolean>
	{
		Gson gson = new Gson();
		ProgressDialog dialog;

		@Override
		protected void onPreExecute()
		{
			dialog = ProgressDialog.show(SearchActivity.this,"","Getting Results...",true);
		}

		@Override
		protected Boolean doInBackground(String... params)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("searchString", searchText.getText().toString()));

			try
			{
				HttpResponse urlResponse = AppEngineClient.makeRequest("/search/"+params[0], nameValuePairs);
				String response = EntityUtils.toString(urlResponse.getEntity());
				if(params[0].equals("users"))
				{
					Type listOfResults = new TypeToken<ArrayList<User>>(){}.getType();
					users = gson.fromJson(response, listOfResults);
				}
				if(params[0].equals("posts"))
				{
					Type listOfResults = new TypeToken<ArrayList<Post>>(){}.getType();
					posts = gson.fromJson(response, listOfResults);
				}
				if(params[0].equals("courses"))
				{
					Type listOfResults = new TypeToken<ArrayList<Course>>(){}.getType();
					courses = gson.fromJson(response, listOfResults);
				}
			}
			catch(Exception ex)
			{
				Log.e("ClassMe", "Error searching: " + ex.getMessage());
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean success)
		{
			dialog.dismiss();

			if(posts != null && users != null && courses != null)
			{
				Bundle bundle = new Bundle();
				bundle.putString("users",gson.toJson(users));
				bundle.putString("posts",gson.toJson(posts));
				bundle.putString("courses",gson.toJson(courses));
				Intent intent = new Intent(SearchActivity.this,ResultsActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	}
}