package com.cs1635.classme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robert McDermot on 4/6/14.
 */
public class FileUploadTask extends AsyncTask<String, Void, String>
{
	ProgressDialog progressDialog;
	boolean isAttachment, profileUpload = false;
	Activity activity;
	ArrayList<String> attachmentKeys, attachmentNames, deleteKeys;
	FlowLayout attachmentsPanel;
	EditText postText;

	public FileUploadTask(boolean isAttachment, Activity activity, ArrayList<String> attachmentKeys, ArrayList<String> attachmentNames, ArrayList<String> deleteKeys, FlowLayout attachmentsPanel, EditText postText)
	{
		this.isAttachment = isAttachment;
		this.activity = activity;
		this.attachmentKeys = attachmentKeys;
		this.attachmentNames = attachmentNames;
		this.deleteKeys = deleteKeys;
		this.attachmentsPanel = attachmentsPanel;
		this.postText = postText;
	}

	@Override
	protected void onPreExecute()
	{
		progressDialog = ProgressDialog.show(activity, "", "Uploading file to server, please wait...", true);
	}

	@Override
	protected String doInBackground(String... arg0)
	{
		if(arg0[0] == null)
			return null;

		String response = null;
		String responseUrl = null;
		try
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			// execute request
			String url = "/fileUpload";
			if(profileUpload)
			{
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
				url += "?username=" + prefs.getString("loggedIn", "");
			}
			HttpResponse urlResponse = AppEngineClient.makeRequest(url, nameValuePairs);
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
			File image = new File(arg0[0]);
			String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(arg0[0]));
			entity.addPart("data", new FileBody(image, mimeType));
			httppost.setEntity(entity);
			response = EntityUtils.toString(httpclient.execute(httppost).getEntity());
			if(isAttachment && !isCancelled())
			{
				attachmentNames.add(image.getName());
				attachmentKeys.add(response);
			}
		}
		catch(IOException e)
		{
			Log.e("ClassMe", "Error uploading file: " + e.getMessage());
		}
		return response;
	}

	@Override
	public void onPostExecute(String response)
	{
		progressDialog.dismiss();
		if(response != null)
		{
			if(isAttachment)
			{
				LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.attachment, null);
				final TextView name = (TextView) layout.findViewById(R.id.fileName);
				name.setText(attachmentNames.get(attachmentNames.size() - 1));
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
								deleteKeys.add(key);
							}
						}
					}
				});
				attachmentsPanel.addView(layout);
			}
			else if(!profileUpload)
			{
				String src = "<img src=\"http://classmeapp.appspot.com/fileRequest?key=" + response + "\">";
				postText.setText(postText.getText().toString() + src);
			}
		}
		else
			Toast.makeText(activity, "Upload error, please check your connection and try again", Toast.LENGTH_SHORT).show();
	}
}