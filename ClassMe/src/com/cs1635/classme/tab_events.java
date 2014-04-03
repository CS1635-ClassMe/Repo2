package com.cs1635.classme;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
				startActivity(new Intent(getActivity(), CreateEventActivity.class));
			}
		});

		populateListView(rootView);

		ListView lv = (ListView) rootView.findViewById(R.id.cal_view_existing);

		lv.setAdapter(new UpcomingEventsAdapter(getActivity(), 42, new ArrayList<Event>()));

		return rootView;
	}

	private void populateListView(View rootView)
	{
		final ListView listView = (ListView) rootView.findViewById(R.id.cal_view_existing);


		new AsyncTask<Void, Void, ArrayList<Event>>()
		{

			@Override
			protected ArrayList<Event> doInBackground(Void... voidsss)
			{
				List<NameValuePair> params = new ArrayList<NameValuePair>(1);
				params.add(new BasicNameValuePair("classId", BuckCourse.classId));
				HttpResponse response;
				try
				{
					response = AppEngineClient.makeRequest("/listEvents", params);
					//parse
					Gson gson = new Gson();
					Type listOfEvents = new TypeToken<ArrayList<Event>>()
					{
					}.getType();
					String entityString = EntityUtils.toString(response.getEntity());

					Log.d("PRINT entity", entityString);

					return gson.fromJson(entityString, listOfEvents); //Return listy! :) "Youre doing great, buck. keep it up" --Robert //"You forgot an apostrophe" --Robert

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(ArrayList<Event> listy)
			{

				Log.d("PRINT LISTY", listy.toString());
				Log.d("size doesnt matter robert", Integer.toString(listy.size()));


				if(listy != null)
				{
					UpcomingEventsAdapter adapt = (UpcomingEventsAdapter) listView.getAdapter();
					adapt.addAll(listy);
					adapt.notifyDataSetChanged();
				}

			}

		}.execute();
	}
}

