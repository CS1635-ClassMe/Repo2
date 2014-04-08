package com.cs1635.classme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apmem.tools.layouts.FlowLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

public class ContactChooserDialog extends AlertDialog
{
	Context context;
	AlertDialog dialog = this;
	ArrayList<String> chosenContacts = new ArrayList<String>();
	ArrayList<String> knownUsers = new ArrayList<String>();
	FlowLayout contactsLayout;
	Gson gson = new Gson();

	protected ContactChooserDialog(Context con)
	{
		super(con);
		context = con;

		Builder builder = new Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.contact_chooser_dialog, null);
		builder.setView(view);
		builder.setTitle("Choose Contacts");

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Type listOfStrings = new TypeToken<ArrayList<String>>(){}.getType();
		if(prefs.contains("knownUsers"))
			knownUsers = gson.fromJson(prefs.getString("knownUsers",""), listOfStrings);

		contactsLayout = (FlowLayout) view.findViewById(R.id.contactsLayout);
		final AutoCompleteTextView customName = (AutoCompleteTextView) view.findViewById(R.id.customName);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,knownUsers.toArray(new String[knownUsers.size()]));
		customName.setAdapter(adapter);
		customName.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				View contactLayout = addContact(parent.getItemAtPosition(position).toString());
				contactLayout.setBackgroundColor(context.getResources().getColor(R.color.background));
				chosenContacts.add(contactLayout.getTag().toString());
			}
		});

		chosenContacts = new ArrayList<String>();
		for(String contact : knownUsers)
		{
			addContact(contact);
		}

		ImageView addButton = (ImageView) view.findViewById(R.id.addButton);
		addButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				View contactLayout = addContact(customName.getText().toString());
				contactLayout.setBackgroundColor(context.getResources().getColor(R.color.background));
				chosenContacts.add(contactLayout.getTag().toString());
				SharedPreferences.Editor edit = prefs.edit();
				knownUsers.add(customName.getText().toString());
				Type listOfStrings = new TypeToken<ArrayList<String>>(){}.getType();
				edit.putString("knownUsers",gson.toJson(knownUsers, listOfStrings));
				edit.apply();
				customName.setText("");
			}
		});

		builder.setPositiveButton("OK",new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Gson gson = new Gson();
				Random rand = new Random();
				Intent intent = new Intent(context,ChatActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("usernames",gson.toJson(chosenContacts));
				bundle.putString("id",String.valueOf(rand.nextInt(10000000)));
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		}).setNegativeButton("Cancel",new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});

		dialog = builder.create();
		dialog.show();
	}

	private View addContact(String contact)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View contactLayout = inflater.inflate(R.layout.contact_card,null);
		TextView name = (TextView) contactLayout.findViewById(R.id.contactName);
		name.setText(contact);
		contactLayout.setTag(contact);
		contactLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(chosenContacts.contains(contactLayout.getTag().toString()))
				{
					contactLayout.setBackgroundColor(0);
					chosenContacts.remove(contactLayout.getTag().toString());
				}
				else
				{
					contactLayout.setBackgroundColor(context.getResources().getColor(R.color.background));
					chosenContacts.add(contactLayout.getTag().toString());
				}
			}
		});
		contactsLayout.addView(contactLayout);
		return contactLayout;
	}
}
