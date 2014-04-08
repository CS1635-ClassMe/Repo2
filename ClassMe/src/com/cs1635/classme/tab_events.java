package com.cs1635.classme;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.Event;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by BuckYoung on 3/29/14.
 */
public class tab_events extends Fragment
{
	View rootView;
	boolean doneTask = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.tab_events, container, false);

		Button createEvent = (Button) rootView.findViewById(R.id.tab_events_new);
		createEvent.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				BuckCourse.rememberPosition(BuckCourse.Position.EVENTS);

				startActivity(new Intent(getActivity(), CreateEventActivity.class));
			}
		});

		if(!doneTask)
			populateListView(rootView);

		ListView lv = (ListView) rootView.findViewById(R.id.list_of_events);
		lv.setAdapter(new UpcomingEventsAdapter(getActivity(), 42, new ArrayList<Event>()));

		return rootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser)
		{
			if(rootView != null)
			{
				populateListView(rootView);
				doneTask = true;
			}
		}
	}

	private void populateListView(View rootView)
	{
		final ListView listView = (ListView) rootView.findViewById(R.id.list_of_events);

		new AsyncTask<Void, Void, ArrayList<Event>>()
		{
			@Override
			protected ArrayList<Event> doInBackground(Void... args)
			{
				List<NameValuePair> params = new ArrayList<NameValuePair>(1);
				params.add(new BasicNameValuePair("classId", BuckCourse.classId));
				HttpResponse response;
				try
				{
					response = AppEngineClient.makeRequest("/listEvents", params);
					//parse
					Gson gson = new Gson();
					Type listOfEvents = new TypeToken<ArrayList<Event>>(){}.getType();
					String entityString = EntityUtils.toString(response.getEntity());

					return gson.fromJson(entityString, listOfEvents);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(ArrayList<Event> events)
			{
				if(events != null)
				{
					if(listView.getAdapter() != null)
					{
						UpcomingEventsAdapter adapter = (UpcomingEventsAdapter) listView.getAdapter();
						adapter.clear();
						adapter.addAll(events);
						adapter.notifyDataSetChanged();
					}
					else
						listView.setAdapter(new UpcomingEventsAdapter(getActivity(), 42, events));
				}
			}

		}.execute();
	}
}

