package com.cs1635.classme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUploadTask extends AsyncTask<String, Void, String>
{
	ProgressDialog progressDialog;
	//boolean isAttachment;
	Context context;
	boolean profileUpload = false;

	public FileUploadTask(Context c)
	{
		context = c;
	}

	@Override
	protected void onPreExecute()
	{
		progressDialog = ProgressDialog.show(context, "", "Uploading file to server, please wait...", true);
	}

	@Override
	protected String doInBackground(String... filePath)
	{
		if(filePath[0] == null)
			return null;

		String resp = null;
		String responseUrl = null;
		try
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			if(profileUpload)
				nameValuePairs.add(new BasicNameValuePair("username",prefs.getString("loggedIn","")));

			// execute request
			HttpResponse urlResponse = AppEngineClient.makeRequest("/fileUpload", nameValuePairs);
			HttpEntity entity = urlResponse.getEntity();
			if(entity != null)
			{
				responseUrl = EntityUtils.toString(entity);
				responseUrl = responseUrl.substring(0, responseUrl.lastIndexOf("/") + 1);
			}
		}
		catch(Exception ex)
		{
			Log.e("ClassMe", "Error getting upload url: " + ex.getMessage());
		}

		if(responseUrl == null) return null;

		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(responseUrl);
			MultipartEntity entity = new MultipartEntity();
			File image = new File(filePath[0]);
			String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(filePath[0]));
			entity.addPart("data", new FileBody(image, mimeType));
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			resp = EntityUtils.toString(response.getEntity());
		}
		catch(IOException e)
		{
			Log.e("ClassMe", "Error uploading file: " + e.getMessage());
		}
		return resp;
	}

	@Override
	public void onPostExecute(String response)
	{
		progressDialog.dismiss();
		if(response != null)
		{
			Toast.makeText(context, "Successfully uploaded file.", Toast.LENGTH_SHORT).show();
			//post.setText(post.getText() + "<img src=\"http://studentclassnet.appspot.com/addendum/getImage?key=" + response + "\">");
		}
		else
			Toast.makeText(context, "Upload error, please check your connection and try again.", Toast.LENGTH_SHORT).show();
	}
}