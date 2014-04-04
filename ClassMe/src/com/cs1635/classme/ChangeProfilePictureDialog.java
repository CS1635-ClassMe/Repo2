package com.cs1635.classme;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ChangeProfilePictureDialog extends AlertDialog
{
	Context context;
	AlertDialog dialog;

	protected ChangeProfilePictureDialog(Context con)
	{
		super(con);
		context = con;

		Builder builder = new Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.change_profile_picture_dialog, null);
		builder.setTitle("Profile Picture");
		builder.setView(view);

		View avatarLayout = view.findViewById(R.id.avatarLayout);
		avatarLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.TITLE, "temp.jpg");
				Uri captureUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

				showPhotoDialog(captureUri);
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

			}
		});

		builder.setCancelable(true);
		dialog = builder.create();
		dialog.show();
	}

	private void showPhotoDialog(final Uri captureUri)
	{
		Builder builder = new Builder(context);
		View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.photo_dialog, null);
		builder.setView(view);
		LinearLayout choosePhoto = (LinearLayout) view.findViewById(R.id.choosePhoto);
		choosePhoto.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				getOwnerActivity().startActivityForResult(pickPhoto, 1);
				dialog.dismiss();
			}
		});
		LinearLayout takePhoto = (LinearLayout) view.findViewById(R.id.takePhoto);
		takePhoto.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
				getOwnerActivity().startActivityForResult(intent, 0);
				dialog.dismiss();
			}
		});

		dialog = builder.create();
		dialog.show();
	}
}