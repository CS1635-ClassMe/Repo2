package com.cs1635.classme;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class BuckCourse extends ActionBarActivity implements ActionBar.TabListener
{
	private ViewPager viewPager;
	private ActionBar actionBar;
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
}