package com.cs1635.classme;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class ChangePasswordDialog extends AlertDialog
{
	Context context;
	AlertDialog dialog;

	protected ChangePasswordDialog(Context con)
	{
		super(con);
		context = con;

		Builder builder = new Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.change_password_dialog, null);
		builder.setTitle("New Password");
		builder.setView(view);

		final EditText currentPasswordText = (EditText)view.findViewById(R.id.currentPassword);
		final EditText newPasswordText = (EditText)view.findViewById(R.id.newPassword);
		final EditText confirmPasswordText = (EditText)view.findViewById(R.id.confirmPassword);

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
				String currentPassword = currentPasswordText.getText().toString();
				String newPassword = newPasswordText.getText().toString();
				String confirmPassword = confirmPasswordText.getText().toString();

				if(newPassword.trim().length() == 0)
				{
					newPasswordText.setError("Password cannot be blank");
					return;
				}

				if(confirmPassword.equals(newPassword))
					new ChangePasswordTask(dialog, currentPasswordText).execute(currentPassword, newPassword);
				else
					confirmPasswordText.setError("Passwords do not match");
			}
		});

		builder.setCancelable(true);
		dialog = builder.create();
		dialog.show();
	}

	private class ChangePasswordTask extends AsyncTask<String,Void,Integer>
	{
		ProgressDialog progressDialog;
		AlertDialog dialog;
		EditText currentPasswordText;

		public ChangePasswordTask(AlertDialog dialog, EditText currentPasswordText)
		{
			this.dialog = dialog;
			this.currentPasswordText = currentPasswordText;
		}

		@Override
		protected void onPreExecute()
		{
			progressDialog = ProgressDialog.show(context, "", "Changing password...", true);
		}

		@Override
		protected Integer doInBackground(String... args)
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("username", prefs.getString("loggedIn","")));
			nameValuePairs.add(new BasicNameValuePair("currentPassword", args[0]));
			nameValuePairs.add(new BasicNameValuePair("newPassword", args[1]));

			try
			{
				HttpResponse httpResponse = AppEngineClient.makeRequest("/changePassword", nameValuePairs);
				String response = EntityUtils.toString(httpResponse.getEntity());
				if(response.equals("success"))
					return 0;
				else if(response.equals("wrong password"))
					return 1;
				else
					return 2;
			}
			catch(Exception ex)
			{
				return 2;
			}
		}

		@Override
		protected void onPostExecute(Integer result)
		{
			progressDialog.dismiss();
			Log.d("deScribe", "result:" + result);
			if(result == 0)
			{
				Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
			else if(result == 1)
			{
				currentPasswordText.setError("Wrong password");
			}
			else
				Toast.makeText(context, "Server error, try again later",Toast.LENGTH_LONG).show();
		}
	}
}