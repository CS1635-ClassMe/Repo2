package com.cs1635.classme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * Created by BuckYoung on 3/29/14.
 */
public class tab_discuss extends Fragment
{
	ListView postList;
	boolean doneTask = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View rootView = inflater.inflate(R.layout.tab_discuss, container, false);

		(rootView.findViewById(R.id.tab_discuss_new)).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{

						Intent intent = new Intent(getActivity(), CreateDiscussionActivity.class);
						startActivity(intent);
					}
				}
		);

		postList = (ListView) rootView.findViewById(R.id.list_of_discussions);
		if(!doneTask)
			new GetPostsTask(getActivity(),null,postList).execute(BuckCourse.classId,"Discussion","Popular");

		return rootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser)
		{
			if(postList != null)
			{
				new GetPostsTask(getActivity(), null, postList).execute(BuckCourse.classId, "Discussion", "Popular");
				doneTask = true;
			}
		}
	}
}