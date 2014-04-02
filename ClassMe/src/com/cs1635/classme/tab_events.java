package com.cs1635.classme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Created by BuckYoung on 3/29/14.
 */
public class tab_events extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.tab_events, container, false);

		Button createEvent = (Button) rootView.findViewById(R.id.cal_view_submit);
		createEvent.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(getActivity(),CreateEventActivity.class));
			}
		});

		return rootView;
	}
}