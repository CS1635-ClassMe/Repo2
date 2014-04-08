package com.cs1635.classme;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * Created by BuckYoung on 3/29/14.
 */
public class tab_lecture extends Fragment
{
	ListView postList;
	boolean doneTask = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab_lecture, container, false);

        (rootView.findViewById(R.id.tab_lecture_new)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getActivity(), CreateLectureActivity.class);
                        startActivity(intent);
                    }
                }
        );

		postList = (ListView) rootView.findViewById(R.id.list_of_lectures);
		if(!doneTask)
			new GetPostsTask(getActivity(),null,postList).execute(BuckCourse.classId,"Lecture","Popular");

		return rootView;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.d("ClassMe", "lecture onResume()");
		new GetPostsTask(getActivity(),null,postList).execute(BuckCourse.classId,"Lecture","Popular");
	}

	@Override
	public void setMenuVisibility(boolean menuVisibility)
	{
		super.setMenuVisibility(menuVisibility);
		if(menuVisibility)
		{
			if(postList != null)
			{
				new GetPostsTask(getActivity(), null, postList).execute(BuckCourse.classId, "Lecture", "Popular");
				doneTask = true;
			}
		}
	}
}