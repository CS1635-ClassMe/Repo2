package com.cs1635.classme;

import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;


/**
 * Created by BuckYoung on 3/29/14.
 */
public class tab_calendar extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab_calendar, container, false);

		CalendarView calendarView = (CalendarView) rootView.findViewById(R.id.calendarView);

        return rootView;
    }
}