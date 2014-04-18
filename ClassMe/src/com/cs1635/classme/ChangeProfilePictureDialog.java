package com.cs1635.classme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import java.io.File;

public class ChangeProfilePictureDialog extends AlertDialog
{
	Activity activity;
	AlertDialog dialog, d;
	String filePath;
	ImageView currentAvatar;

	public void setImage()
	{
		if(filePath == null)
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
			String url = "http://classmeapp.appspot.com/fileRequest?username=" + prefs.getString("loggedIn", "");
			Ion.with(currentAvatar).placeholder(R.drawable.user_icon).load(url);
		}
		else
		{
			Uri imgUri = Uri.fromFile(new File(filePath));
			currentAvatar.setImageURI(imgUri);
		}
	}

	public ChangeProfilePictureDialog(Activity a)
	{
		super(a);
		this.activity = a;

		Builder builder = new Builder(activity);
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.change_profile_picture_dialog, null);
		builder.setTitle("Profile Picture");
		builder.setView(view);

		currentAvatar = (ImageView) view.findViewById(R.id.currentAvatar);
		setImage();

		View avatarLayout = view.findViewById(R.id.avatarLayout);
		avatarLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new PhotoDialog(activity);
				dialog.dismiss();
			}
		});

		Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
		Button okButton = (Button) view.findViewById(R.id.okButton);

		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		okButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(filePath != null)
				{
					FileUploadTask task = new FileUploadTask(false,activity,null,null,null,null,null);
					task.profileUpload = true;
					task.execute(filePath);
					dialog.dismiss();
				}
				else
					dialog.dismiss();
			}
		});

		builder.setCancelable(true);
		dialog = builder.create();
		dialog.show();
	}
}