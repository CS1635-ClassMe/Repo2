package com.cs1635.classme;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robert McDermot on 4/6/14.
 */
public class DeleteAttachmentsTask extends AsyncTask<String, Void, Void>
{
	@Override
	protected Void doInBackground(String... params)
	{
		try
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("key", params[0]));
			AppEngineClient.makeRequest("/deleteAttachment", nameValuePairs);
		}
		catch(Exception ex)
		{
			Log.e("ClassMe", "Error deleting attachment: " + ex.getMessage());
		}
		return null;
	}
}