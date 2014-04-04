package com.cs1635.classme;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * Created by BuckYoung on 3/29/14.
 */
public class tab_notes extends Fragment
{
	ImageView preview;
	tab_notes fragment = this;
	Uri captureUri, fileUri;

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

	/*preview = (ImageView) rootView.findViewById(R.id.preview);

		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "temp.jpg");
		captureUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		View attachLayout = rootView.findViewById(R.id.attachLayout);
		attachLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new PhotoDialog(fragment, captureUri);
			}
		});

		View shareLayout = rootView.findViewById(R.id.shareLayout);
		shareLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new FileUploadTask(getActivity()).execute(getRealPathFromURI(fileUri));
			}
		});
*/
		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
	{
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		if(resultCode != Activity.RESULT_OK)
		{
			Toast.makeText(getActivity(), "Unable to get image", Toast.LENGTH_SHORT).show();
			return;
		}
		if(requestCode == 0) //if camera
			fileUri = captureUri;
		else if(resultCode == Activity.RESULT_OK) //if photo chooser
			fileUri = imageReturnedIntent.getData();

		preview.setImageURI(fileUri);
	}

	public String getRealPathFromURI(Uri uri)
	{
		Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}
}