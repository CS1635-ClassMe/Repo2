package com.cs1635.classme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shared.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UpcomingEventsAdapter extends ArrayAdapter<Event>
{
    Context context;
    ArrayList<Event> events;
    SharedPreferences prefs;

    public UpcomingEventsAdapter(Context context, int textViewResourceId, ArrayList<Event> events)
    {
        super(context, textViewResourceId, events);
        this.events = events;
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        if(convertView != null)
        {
           v = convertView;
        }
        else
        {
            v = vi.inflate(R.layout.cal_single_event, null);
        }

        TextView title = (TextView) v.findViewById(R.id.cal_single_title);
        TextView description = (TextView) v.findViewById(R.id.cal_single_description);
        TextView date = (TextView) v.findViewById(R.id.cal_single_date);

        title.setText(events.get(position).getTitle());
        description.setText(events.get(position).getDescription());
        Date dateObj = events.get(position).getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy  |  hh:mm a");
        date.setText(sdf.format(dateObj));

		v.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new EventDetailsDialog(context, events.get(position));
			}
		});

        return  v;
    }
}