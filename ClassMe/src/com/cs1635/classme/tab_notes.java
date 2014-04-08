package com.cs1635.classme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * Created by BuckYoung on 3/29/14.
 */
public class tab_notes extends Fragment
{
	ListView postList;
	boolean doneTask = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.tab_notes, container, false);

        (rootView.findViewById(R.id.tab_notes_new)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
                        startActivity(intent);
                    }
                }
        );

		postList = (ListView) rootView.findViewById(R.id.list_of_notes);
		if(!doneTask)
			new GetPostsTask(getActivity(),null,postList).execute(BuckCourse.classId,"Note","Popular");

		return rootView;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.d("ClassMe", "notes onResume()");
		new GetPostsTask(getActivity(),null,postList).execute(BuckCourse.classId,"Note","Popular");
	}

	@Override
	public void setMenuVisibility(boolean menuVisibility)
	{
		super.setMenuVisibility(menuVisibility);
		if(menuVisibility)
		{
			if(postList != null)
			{
				new GetPostsTask(getActivity(), null, postList).execute(BuckCourse.classId, "Note", "Popular");
				doneTask = true;
			}
		}
	}
}