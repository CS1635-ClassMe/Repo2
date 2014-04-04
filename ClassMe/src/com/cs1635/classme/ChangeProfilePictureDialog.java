package com.cs1635.classme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ChangeProfilePictureDialog extends AlertDialog
{
	Activity activity;
	AlertDialog dialog, d;
	String filePath;
	ImageView currentAvatar;
	Uri fileUri;

	public void setImage()
	{
		if(fileUri == null)
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
			String url = "http://classmeapp.appspot.com/fileRequest?username=" + prefs.getString("loggedIn", "");
			ImageLoader loader = ImageLoader.getInstance();
			loader.init(new ImageLoaderConfiguration.Builder(activity).build());
			loader.displayImage(url,currentAvatar);
		}
		else
			currentAvatar.setImageURI(fileUri);
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

		View avatarLayout = view.findViewById(R.id.avatarLayout);
		avatarLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.TITLE, "temp.jpg");
				Uri captureUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

				showPhotoDialog(captureUri,activity);
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
					FileUploadTask task = new FileUploadTask(activity);
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

	private void showPhotoDialog(final Uri captureUri, final Activity activity)
	{
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
				d.dismiss();
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
				activity.startActivityForResult(intent, 0);
				d.dismiss();
			}
		});

		d = builder.create();
		d.show();
	}
}