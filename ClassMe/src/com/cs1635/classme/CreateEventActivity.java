package com.cs1635.classme;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.gson.Gson;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateEventActivity extends ActionBarActivity
		implements OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
	CreateEventActivity activity = this;
	public static final String DATEPICKER_TAG = "datepicker";
	public static final String TIMEPICKER_TAG = "timepicker";
	Calendar calendar = Calendar.getInstance();
	TextView date, time;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cal_create_event);

		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
		final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);

		Button chooseDate = (Button) findViewById(R.id.chooseDate);
		Button chooseTime = (Button) findViewById(R.id.chooseTime);
		Button createEvent = (Button) findViewById(R.id.cal_create_submit);

		date = (TextView) findViewById(R.id.cal_create_date);
		time = (TextView) findViewById(R.id.cal_create_time);

		final EditText title = (EditText) findViewById(R.id.cal_create_title);
		final EditText description = (EditText) findViewById(R.id.cal_create_description);

		chooseDate.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				datePickerDialog.setYearRange(1985, 2028);
				datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
			}
		});

		chooseTime.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
			}
		});

		createEvent.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(title.getText().toString().trim().equals(""))
				{
					title.setError("Title cannot be blank");
					return;
				}

				new AsyncTask<String,Void,Boolean>()
				{
					ProgressDialog dialog;

					@Override
					protected void onPreExecute()
					{
						dialog = ProgressDialog.show(activity,"Creating Event","",true);
					}

					@Override
					protected Boolean doInBackground(String... params)
					{
						Gson gson = new Gson();
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
						nameValuePairs.add(new BasicNameValuePair("creator", prefs.getString("loggedIn","")));
						nameValuePairs.add(new BasicNameValuePair("title", params[0]));
						nameValuePairs.add(new BasicNameValuePair("description", params[1]));
						nameValuePairs.add(new BasicNameValuePair("classId", BuckCourse.classId));
						nameValuePairs.add(new BasicNameValuePair("date", gson.toJson(calendar.getTime())));

						try
						{
							AppEngineClient.makeRequest("/createEvent", nameValuePairs);
						}
						catch(Exception ex)
						{
							return false;
						}
						return true;
					}

					@Override
					protected void onPostExecute(Boolean success)
					{
						if(success)
						{
							dialog.dismiss();
							Toast.makeText(activity,"Success",Toast.LENGTH_SHORT);
							finish();
						}
						else
						{
							Log.d("ClassMe", "Couldn't create event");
							Toast.makeText(activity, "Unable to create event.", Toast.LENGTH_SHORT);
						}
					}

				}.execute(title.getText().toString(),description.getText().toString());
			}
		});
	}

	@Override
	public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day)
	{
		calendar.set(year,month,day);
		SimpleDateFormat format = new SimpleDateFormat("M/d/yyyy");
		date.setText("(" + format.format(calendar.getTime()) + ")");
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute)
	{
		calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		SimpleDateFormat format = new SimpleDateFormat("h:mm a");
		time.setText("("+format.format(calendar.getTime())+")");
	}
}
