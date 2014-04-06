package com.cs1635.classme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;
import java.util.Date;

public class PhotoDialog extends AlertDialog
{
	AlertDialog dialog = this;
	Activity activity;

	public PhotoDialog(final Activity activity)
	{
		super(activity);

		this.activity = activity;

		Builder builder = new Builder(activity);
		View view = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.photo_dialog, null);
		builder.setView(view);
		LinearLayout choosePhoto = (LinearLayout) view.findViewById(R.id.choosePhoto);
		choosePhoto.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				activity.startActivityForResult(pickPhoto, 1);
				dialog.dismiss();
			}
		});
		LinearLayout takePhoto = (LinearLayout) view.findViewById(R.id.takePhoto);
		takePhoto.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "temp.png");
				Uri imgUri = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
				activity.startActivityForResult(intent, 0);
				dialog.dismiss();
			}
		});

		dialog = builder.create();
		dialog.show();
	}
}
