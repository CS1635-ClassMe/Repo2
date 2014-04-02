package com.cs1635.classme;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class CreateEventActivity extends ActionBarActivity
		implements OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
	CreateEventActivity activity = this;
	public static final String DATEPICKER_TAG = "datepicker";
	public static final String TIMEPICKER_TAG = "timepicker";
	Calendar calendar = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cal_create_event);

		final Calendar calendar = Calendar.getInstance();

		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
		final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);

		Button chooseDate = (Button) findViewById(R.id.chooseDate);
		Button chooseTime = (Button) findViewById(R.id.chooseTime);
		Button createEvent = (Button) findViewById(R.id.cal_create_submit);

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

				new AsyncTask<String,Void,Void>()
				{
					@Override
					protected Void doInBackground(String... params)
					{

						return null;
					}
				}.execute(title.getText().toString(),description.getText().toString());
			}
		});
	}

	@Override
	public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day)
	{
		calendar.set(year,month,day);
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute)
	{
		calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
		calendar.set(Calendar.MINUTE,minute);
	}
}
