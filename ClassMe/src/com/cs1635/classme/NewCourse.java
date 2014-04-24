package com.cs1635.classme;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class NewCourse extends ActionBarActivity
{
	EditText courseId, instructor, department;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_course);

		Button createSubmit = (Button) findViewById(R.id.create_submit);
		courseId = (EditText) findViewById(R.id.courseid);
		instructor = (EditText) findViewById(R.id.instructor);
		department = (EditText) findViewById(R.id.department);

		createSubmit.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(courseId.getText().toString().length() < 1)
				{
					courseId.setError("Cannot be blank");
					return;
				}
				if(instructor.getText().toString().length() < 1)
				{
					instructor.setError("Cannot be blank");
					return;
				}
				if(department.getText().toString().length() < 1)
				{
					department.setError("Cannot be blank");
					return;
				}
				new NewCourseTask().execute();
			}
		});
	}

	private class NewCourseTask extends AsyncTask<Void,Void,Boolean>
	{
		ProgressDialog dialog;

		@Override
		protected void onPreExecute()
		{
			dialog = ProgressDialog.show(NewCourse.this,"","Creating Course...",true);
		}

		@Override
		protected Boolean doInBackground(Void... params)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("department", department.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("instructor", instructor.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("courseId", courseId.getText().toString()));

			try
			{
				AppEngineClient.makeRequest("/newCourse", nameValuePairs);
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
			if(success)
				finish();
			else
				Toast.makeText(NewCourse.this,"Error creating course",Toast.LENGTH_SHORT);
		}
	}
}
