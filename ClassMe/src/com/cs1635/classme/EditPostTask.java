package com.cs1635.classme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shared.Post;

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
public class EditPostTask extends AsyncTask<Void, Void, Boolean>
{
	ProgressDialog progressDialog;
	Post editPost;
	Activity activity;
	ArrayList<String> attachmentKeys, attachmentNames, deleteKeys;

	public EditPostTask(Activity activity, ArrayList<String> attachmentKeys, ArrayList<String> attachmentNames, ArrayList<String> deleteKeys, Post editPost)
	{
		this.editPost = editPost;
		this.activity = activity;
		this.attachmentKeys = attachmentKeys;
		this.attachmentNames = attachmentNames;
		this.deleteKeys = deleteKeys;
	}

	@Override
	protected void onPreExecute()
	{
		progressDialog = ProgressDialog.show(activity, "", "Sending...", true);
	}

	@Override
	protected Boolean doInBackground(Void... v)
	{
		Gson gson = new Gson();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("html", editPost.getPostContent()));
		nameValuePairs.add(new BasicNameValuePair("title", editPost.getPostTitle()));
		nameValuePairs.add(new BasicNameValuePair("postKey", editPost.getPostKey()));
		nameValuePairs.add(new BasicNameValuePair("time", gson.toJson(new Date())));
		nameValuePairs.add(new BasicNameValuePair("attachmentKeys", gson.toJson(attachmentKeys)));
		nameValuePairs.add(new BasicNameValuePair("attachmentNames", gson.toJson(attachmentNames)));

		try
		{
			HttpResponse urlResponse = AppEngineClient.makeRequest("/editPost", nameValuePairs);
			String response = EntityUtils.toString(urlResponse.getEntity());
			if(!response.equals("done"))
				return false;
		}
		catch(Exception ex)
		{
			Log.e("ClassMe", "Error editing post: " + ex.getMessage());
		}

		return true;
	}

	@Override
	protected void onPostExecute(Boolean success)
	{
		progressDialog.dismiss();
		if(!success)
		{
			Toast.makeText(activity, "Error while uploading post", Toast.LENGTH_SHORT).show();
			return;
		}
		else
		{
			for(String key : deleteKeys)
				new DeleteAttachmentsTask().execute(key);

			Bundle bundle = new Bundle();
			bundle.putSerializable("post", editPost);
			Intent result = new Intent();
			result.putExtras(bundle);
			activity.setResult(Activity.RESULT_OK, result);
			activity.finish();
		}
	}
}