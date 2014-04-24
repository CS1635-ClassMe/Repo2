package com.cs1635.classme;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.User;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BuckCourse extends ActionBarActivity implements ActionBar.TabListener
{
	private ViewPager viewPager;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = {"Discussions", "Recorded Lectures", "Class Notes", "Events"};
	public static String classId = "CS1635";
	private static int remembered_tab;
	SharedPreferences prefs;

	MenuItem joinClass, leaveClass;

	public static enum Position
	{
		DISCUSS, LECTURE, NOTES, EVENTS
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buck_course);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		classId = getIntent().getStringExtra("classId");

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getSupportActionBar();
		TabsPagerAdapter mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setTitle(classId);

		// Adding Tabs
		for(String tab_name : tabs)
		{
			actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int position)
			{
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{
			}
		});
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{

	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{

	}

	@Override
	public void onResume()
	{
		super.onResume();

		viewPager.setCurrentItem(remembered_tab);
	}

	public static void resetPosition()
	{
		remembered_tab = 0;
	}

	public static void rememberPosition(Position position)
	{
		int pos;

		switch(position)
		{
			case DISCUSS:
				pos = 0;
				break;
			case LECTURE:
				pos = 1;
				break;
			case NOTES:
				pos = 2;
				break;
			case EVENTS:
				pos = 3;
				break;
			default:
				pos = 0;
		}

		remembered_tab = pos;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.buckcourse, menu);
		Gson gson = new Gson();
		User user = gson.fromJson(prefs.getString("userObject",""), User.class);
		joinClass = menu.findItem(R.id.joinClass);
		leaveClass = menu.findItem(R.id.leaveClass);
		if(user.getCourseList() != null && user.getCourseList().contains(classId)) //we're already a member
		{
			joinClass.setVisible(false);
			leaveClass.setVisible(true);
		}
		else
		{
			joinClass.setVisible(true);
			leaveClass.setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == R.id.listMembers)
		{
			new GetMembersTask().execute();
			return true;
		}
		if(id == R.id.joinClass || id == R.id.leaveClass)
		{
			new ToggleClassTask().execute();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class ToggleClassTask extends AsyncTask<Void,Void,String>
	{
		@Override
		protected String doInBackground(Void... params)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username", prefs.getString("loggedIn","")));
			nameValuePairs.add(new BasicNameValuePair("id", classId));

			try
			{
				HttpResponse urlResponse = AppEngineClient.makeRequest("/toggleClass", nameValuePairs);
				String response = EntityUtils.toString(urlResponse.getEntity());
				return response;
			}
			catch(Exception ex)
			{
				return null;
			}
		}

		@Override
		protected void onPostExecute(String response)
		{
			if(response.equals("success"))
			{
				Gson gson = new Gson();
				User user = gson.fromJson(prefs.getString("userObject",""),User.class);

				if(user.getCourseList().contains(classId))
				{
					user.getCourseList().remove(classId);
					joinClass.setVisible(true);
					leaveClass.setVisible(false);
				}
				else
				{
					user.getCourseList().add(classId);
					joinClass.setVisible(false);
					leaveClass.setVisible(true);
				}

				SharedPreferences.Editor edit = prefs.edit();
				edit.putString("userObject",gson.toJson(user));
				edit.apply();
			}
			else
			{
				Toast.makeText(BuckCourse.this,"Unable to contact server, please try again later",Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class GetMembersTask extends AsyncTask<Void,Void,ArrayList<String>>
	{
		ProgressDialog dialog;

		@Override
		protected void onPreExecute()
		{
			dialog = ProgressDialog.show(BuckCourse.this,"","Getting Member List...");
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params)
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("id", classId));

			try
			{
				HttpResponse urlResponse = AppEngineClient.makeRequest("/listMembers", nameValuePairs);
				String response = EntityUtils.toString(urlResponse.getEntity());
				Gson gson = new Gson();
				Type listOfStrings = new TypeToken<ArrayList<String>>(){}.getType();
				ArrayList<String> members = gson.fromJson(response,listOfStrings);
				return members;
			}
			catch(Exception ex)
			{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<String> members)
		{
			dialog.dismiss();

			if(members != null)
			{
				if(members.size() > 0)
					new ListMembersDialog(BuckCourse.this, members);
				else
					Toast.makeText(BuckCourse.this,"No current members of this class.", Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(BuckCourse.this,"Could not get list of members from server.", Toast.LENGTH_SHORT).show();
		}
	}
}