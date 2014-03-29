package com.cs1635.classme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Date;

public class ContactChooserDialog extends AlertDialog
{
	Context context;
	AlertDialog dialog = this;

	protected ContactChooserDialog(Context con)
	{
		super(con);
		context = con;

		Builder builder = new Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.contact_chooser_dialog, null);
		builder.setView(view);
		builder.setTitle("Choose Contacts");

		FlowLayout contactsLayout = (FlowLayout) view.findViewById(R.id.contactsLayout);

		String[] contacts = {"Robert","Aamir","Buck"};
		final ArrayList<String> chosenContacts = new ArrayList<String>();
		for(String contact : contacts)
		{
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
		}

		builder.setPositiveButton("OK",new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String users = "";
				for(String user : chosenContacts)
					users += user + ",";
				if(users.endsWith(","))
					users = users.substring(0,users.length()-1);

				Intent intent = new Intent(context,ChatActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("userString",users);
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
}
