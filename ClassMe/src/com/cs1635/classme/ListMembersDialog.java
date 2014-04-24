package com.cs1635.classme;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListMembersDialog extends AlertDialog
{
	Context context;
	AlertDialog dialog;

	protected ListMembersDialog(Context con, ArrayList<String> members)
	{
		super(con);
		context = con;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		ArrayList<String> knownUsers = new ArrayList<String>();
		Gson gson = new Gson();
		Type listOfStrings = new TypeToken<ArrayList<String>>(){}.getType();
		if(prefs.contains("knownUsers"))
			knownUsers = gson.fromJson(prefs.getString("knownUsers",""),listOfStrings);

		for(String member : members)
		{
			if(!knownUsers.contains(member))
				knownUsers.add(member);
		}

		prefs.edit().putString("knownUsers",gson.toJson(knownUsers)).apply();

		Builder builder = new Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.member_list_dialog, null);
		builder.setTitle("Member List for " + BuckCourse.classId);
		builder.setView(view);

		ListView memberList = (ListView) view.findViewById(R.id.memberList);
		memberList.setAdapter(new MemberListAdapter(context,1,members));

		builder.setPositiveButton("OK", null);

		builder.setCancelable(true);
		dialog = builder.create();
		dialog.show();
	}
}