package com.cs1635.classme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.Event;

import org.apmem.tools.layouts.FlowLayout;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventDetailsDialog extends AlertDialog
{
	Context context;
	AlertDialog dialog = this;

	protected EventDetailsDialog(Context con, Event event)
	{
		super(con);
		context = con;

		Builder builder = new Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.cal_view_details, null);
		builder.setView(view);
		builder.setTitle("Event Details");

		SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

		TextView title = (TextView) view.findViewById(R.id.cal_view_title);
		TextView descripion = (TextView) view.findViewById(R.id.cal_view_description);
		TextView time = (TextView) view.findViewById(R.id.cal_view_time);
		TextView date = (TextView) view.findViewById(R.id.cal_view_date);
		TextView creator = (TextView) view.findViewById(R.id.cal_view_creator);
		TextView classId = (TextView) view.findViewById(R.id.cal_view_class);

		title.setText(event.getTitle());
		descripion.setText(event.getDescription());
		time.setText("Time: " + timeFormat.format(event.getDate()));
		date.setText("Date: " + dateFormat.format(event.getDate()));
		creator.setText("Creator: " + event.getCreator());
		classId.setText(BuckCourse.classId);

		builder.setPositiveButton("OK", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		});

		dialog = builder.create();
		dialog.show();
	}
}
