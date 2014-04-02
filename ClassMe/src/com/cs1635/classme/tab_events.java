package com.cs1635.classme;

import android.content.Intent;
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

        populateListView(rootView);

        ListView lv = (ListView) rootView.findViewById(R.id.cal_view_existing);

        lv.setAdapter(new ExistingEventsAdapter(getActivity(), 42, new ArrayList<Event>() ) );

		return rootView;
	}

    private void populateListView(View rootView){
        final ListView listView = (ListView) rootView.findViewById(R.id.cal_view_existing);

        new Thread(new Runnable() {
            public void run() {
                List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                params.add(new BasicNameValuePair("classID", BuckCourse.classId));
                HttpResponse response;
                try {
                    response = AppEngineClient.makeRequest("/listEvents", params);
                    //parse
                    Gson gson = new Gson();
                    Type listOfEvents = new TypeToken<ArrayList<Event>>(){}.getType();
                    String entityString = EntityUtils.toString(response.getEntity());
                    ArrayList<Event> listy = gson.fromJson(entityString, listOfEvents);

                    ExistingEventsAdapter adapt = (ExistingEventsAdapter) listView.getAdapter();

                    
                    adapt.addAll(listy);
                    adapt.notifyDataSetChanged();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

