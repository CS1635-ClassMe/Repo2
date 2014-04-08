package com.cs1635.classme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shared.Post;

import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Robert McDermot on 4/4/14.
 */
public class CreateLectureActivity extends ActionBarActivity
{
	Activity activity = this;
	EditText postText, postTitle;
	Uri captureUri;
	ArrayList<String> attachmentNames = new ArrayList<String>(), attachmentKeys = new ArrayList<String>(), deleteKeys = new ArrayList<String>();
	FlowLayout attachmentsPanel;
	Post editPost;
	AlertDialog d;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_lecture);

		postTitle = (EditText) findViewById(R.id.post_title);
		postText = (EditText) findViewById(R.id.post_text);

		Bundle bundle = getIntent().getBundleExtra("bundle");
		if(bundle != null)
			editPost = (Post) bundle.getSerializable("post");

		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "temp.jpg");
		captureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		final AlphaAnimation alphaUp = new AlphaAnimation(.2f, 1f); //hack for view.setAlpha in api < 11
		alphaUp.setDuration(0);
		alphaUp.setFillAfter(true);

		final AlphaAnimation alphaDown = new AlphaAnimation(1f, .2f);
		alphaDown.setDuration(0);
		alphaDown.setFillAfter(true);

		final LinearLayout shareLayout = (LinearLayout) findViewById(R.id.shareLayout);
		shareLayout.startAnimation(alphaDown);

		TextWatcher textWatcher = new TextWatcher()
		{
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				if(postText.getText().length() > 0 && postTitle.getText().length() > 0)
					shareLayout.startAnimation(alphaUp);
				else
					shareLayout.startAnimation(alphaDown);
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){}
			@Override
			public void afterTextChanged(Editable s){}
		};
		postText.addTextChangedListener(textWatcher);
		postTitle.addTextChangedListener(textWatcher);
		if(editPost != null)
		{
			postText.setText(editPost.getPostContent());
			postTitle.setText(editPost.getPostTitle());

			if(postText.getText().length() > 0 && postTitle.getText().length() > 0)
				shareLayout.startAnimation(alphaUp);
			else
				shareLayout.startAnimation(alphaDown);
		}

		LinearLayout fileLayout = (LinearLayout) findViewById(R.id.fileLayout);
		fileLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent chooseFile;
				Intent intent;
				chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
				chooseFile.setType("*/*");
				intent = Intent.createChooser(chooseFile, "Choose a file");
				startActivityForResult(intent, 3);
			}
		});

		LinearLayout cancelLayout = (LinearLayout) findViewById(R.id.cancelLayout);
		cancelLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(editPost != null && editPost.getPostContent().equals(postText.getText().toString()))
					finish();
				else if(postText.getText().toString().length() != 0)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(activity)
							.setTitle("Addendum Mobile")
							.setMessage("Do you want to discard this post?")
							.setNegativeButton("No", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									dialog.dismiss();
								}
							})
							.setPositiveButton("Yes", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									activity.finish();
								}
							});
					builder.create().show();
				}
				else
					finish();
			}
		});
		shareLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(editPost != null)
				{
					postText.setText(postText.getText().toString().replaceAll("\n", "<br>"));
					editPost.setPostContent(postText.getText().toString());
					editPost.setPostTitle(postTitle.getText().toString());
					editPost.setAttachmentKeys(attachmentKeys);
					editPost.setAttachmentNames(attachmentNames);
					new EditPostTask(activity, attachmentKeys, attachmentNames, deleteKeys, editPost).execute();
				}
				else
				{
					if(postText.getText().length() > 0 && postTitle.getText().length() > 0)
					{
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
						new PostUploadTask(activity, attachmentKeys, attachmentNames, deleteKeys).execute(postTitle.getText().toString(), postText.getText().toString().replaceAll("\n", "<br>"), prefs.getString("loggedIn", ""), BuckCourse.classId, "Lecture");
					}
				}
			}
		});

		attachmentsPanel = (FlowLayout) findViewById(R.id.attachmentsPanel);
		if(editPost != null)
		{
			attachmentNames = editPost.getAttachmentNames();
			attachmentKeys = editPost.getAttachmentKeys();

			for(int i = 0; i < attachmentKeys.size(); i++)
			{
				LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.attachment, null);
				final TextView name = (TextView) layout.findViewById(R.id.fileName);
				name.setText(attachmentNames.get(i));
				ImageView delete = (ImageView) layout.findViewById(R.id.delete);
				delete.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						for(int i = 0; i < attachmentNames.size(); i++)
						{
							if(attachmentNames.get(i).equals(name.getText()))
							{
								attachmentNames.remove(i);
								String key = attachmentKeys.remove(i);
								attachmentsPanel.removeView(layout);
								new DeleteAttachmentsTask().execute(key);
							}
						}
					}
				});
				attachmentsPanel.addView(layout);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		String filePath;

		if(resultCode != Activity.RESULT_OK)
		{
			Toast.makeText(this, "Unable to get file", Toast.LENGTH_SHORT).show();
			return;
		}

		if(requestCode == 0)
			filePath = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "temp.png").getPath();
		else
		{
			filePath = getFilePathFromContentUri(data.getData());
		}

		new FileUploadTask(requestCode == 3, activity, attachmentKeys, attachmentNames, deleteKeys, attachmentsPanel, postText).execute(filePath);
	}

	private String getFilePathFromContentUri(Uri uri)
	{
		String filePath;
		try
		{
			String[] filePathColumn = {MediaStore.Files.FileColumns.DATA};

			Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
			filePath = cursor.getString(columnIndex);
			cursor.close();
		}
		catch(Exception e)
		{
			filePath = uri.getPath();
		}
		return filePath;
	}
}
