package com.cs1635.classme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Robert McDermot on 4/6/14.
 */
public class PostUploadTask extends AsyncTask<String, Void, Boolean>
{
	ProgressDialog progressDialog;
	ArrayList<String> attachmentKeys, attachmentNames, deleteKeys;
	Activity activity;

	public PostUploadTask(Activity activity, ArrayList<String> attachmentKeys, ArrayList<String> attachmentNames, ArrayList<String> deleteKeys)
	{
		this.attachmentKeys = attachmentKeys;
		this.attachmentNames = attachmentNames;
		this.deleteKeys = deleteKeys;
		this.activity = activity;
	}

	@Override
	protected void onPreExecute()
	{
		progressDialog = ProgressDialog.show(activity, "", "Sending...", true);
	}

	@Override
	protected Boolean doInBackground(String... params)
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
		nameValuePairs.add(new BasicNameValuePair("title", params[0]));
		nameValuePairs.add(new BasicNameValuePair("text", params[1]));
		nameValuePairs.add(new BasicNameValuePair("username", params[2]));
		nameValuePairs.add(new BasicNameValuePair("classId", params[3]));
		nameValuePairs.add(new BasicNameValuePair("type", params[4]));
		Gson gson = new Gson();
		nameValuePairs.add(new BasicNameValuePair("time", gson.toJson(new Date())));
		nameValuePairs.add(new BasicNameValuePair("attachmentKeys", gson.toJson(attachmentKeys)));
		nameValuePairs.add(new BasicNameValuePair("attachmentNames", gson.toJson(attachmentNames)));

		try
		{
			HttpResponse urlResponse = AppEngineClient.makeRequest("/uploadPost", nameValuePairs);
			String response = EntityUtils.toString(urlResponse.getEntity());
			if(!response.equals("done"))
				return false;
		}
		catch(Exception ex)
		{
			Log.e("ClassMe", "Error uploading post: " + ex.getMessage());
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean success)
	{
		progressDialog.dismiss();
		if(success)
		{
			for(String key : deleteKeys)
				new DeleteAttachmentsTask().execute(key);
			activity.finish();
		}
		else
		{
			Toast.makeText(activity, "There was a problem uploading your post.  Please try again.", Toast.LENGTH_SHORT).show();
		}
	}
}
