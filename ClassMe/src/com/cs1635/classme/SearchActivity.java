package com.cs1635.classme;

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
	ArrayList<Object> results = new ArrayList<Object>();
	int numExpected = 0, numReturned = 0;

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
				if(searchFilterCourses.isChecked())
				{
					numExpected++;
					new SearchTask().execute("courses");
				}
				if(searchFilterUsers.isChecked())
				{
					numExpected++;
					new SearchTask().execute("users");
				}
				if(searchFilterPosts.isChecked())
				{
					numExpected++;
					new SearchTask().execute("posts");
				}
				if(!searchFilterCourses.isChecked() && !searchFilterUsers.isChecked() && !searchFilterPosts.isChecked())
				{
					numExpected = 3;
					new SearchTask().execute("courses");
					new SearchTask().execute("users");
					new SearchTask().execute("posts");
				}
			}
		});
	}

	private class SearchTask extends AsyncTask<String,Void,ArrayList<Object>>
	{
		Gson gson = new Gson();

		@Override
		protected ArrayList<Object> doInBackground(String... params)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("searchString", searchText.getText().toString()));

			ArrayList<Object> partialResults = null;
			try
			{
				numReturned++;
				HttpResponse urlResponse = AppEngineClient.makeRequest("/search/"+params[0], nameValuePairs);
				String response = EntityUtils.toString(urlResponse.getEntity());
				Type listOfResults = new TypeToken<ArrayList<Object>>(){}.getType();
				partialResults = gson.fromJson(response, listOfResults);
			}
			catch(Exception ex)
			{
				Log.e("ClassMe", "Error searching: " + ex.getMessage());
				return partialResults;
			}
			return partialResults;
		}

		@Override
		protected void onPostExecute(ArrayList<Object> partialResults)
		{
			results.addAll(partialResults);

			if(numReturned == numExpected)
			{
				Bundle bundle = new Bundle();
				bundle.putString("results",gson.toJson(results));
				Intent intent = new Intent(SearchActivity.this,ResultsActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	}
}