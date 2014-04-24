package com.cs1635.classme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.shared.Course;
import com.shared.TextMessage;

import java.util.ArrayList;

public class ResultCourseAdapter extends ArrayAdapter<Course>
{
	Context context;
	ArrayList<Course> courses;

	public ResultCourseAdapter(Context context, int textViewResourceId, ArrayList<Course> courses)
	{
		super(context, textViewResourceId, courses);
		this.context = context;
		this.courses = courses;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v;
		if(convertView != null)
			v = convertView;
		else
			v = vi.inflate(R.layout.result_course,null);

		TextView courseId = (TextView) v.findViewById(R.id.result_courseid);
		TextView department = (TextView) v.findViewById(R.id.result_coursedepartment);
		TextView instructor = (TextView) v.findViewById(R.id.result_courseinstructor);

		courseId.setText(courses.get(position).getCourseId());
		department.setText(courses.get(position).getDepartment());
		instructor.setText(courses.get(position).getInstructor());

		return  v;
	}
}