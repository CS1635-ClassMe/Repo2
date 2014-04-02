package com.cs1635.classme;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class BuckCourse extends FragmentActivity implements ActionBar.TabListener
{
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private android.app.ActionBar actionBar;
	// Tab titles
	private String[] tabs = {"Discuss", "Lecture", "Notes", "Events", "Members"};
	public static String classId = "CS1635";

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buck_course);

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

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
                Log.d("butt", "BLAH");
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
	public void onTabSelected(Tab tab, android.app.FragmentTransaction fragmentTransaction)
	{
		viewPager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction fragmentTransaction)
	{

	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction fragmentTransaction)
	{

	}
}