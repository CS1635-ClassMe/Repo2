package com.cs1635.classme;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	Context context = this;
	ArrayList<String> users = null;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.contains("gcmFalse"))
		{
			CheckBoxPreference enableNotifications = (CheckBoxPreference) findPreference("notificationCheckbox");
			enableNotifications.setChecked(false);
			enableNotifications.setEnabled(false);
			enableNotifications.setSummary("Your device does not support Google Cloud Messaging");
		}

		Preference changePassword = (Preference) findPreference("changePassword");
		changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(Preference preference)
			{
				//new ChangePasswordDialog(context);
				return true;
			}
		});

		Preference profilePicture = (Preference) findPreference("profilePicture");
		profilePicture.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(Preference preference)
			{
				//new ProfilePictureDialog(context);
				return true;
			}
		});

		Preference chatNotificationTest = (Preference) findPreference("chatNotificationTest");
		chatNotificationTest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(Preference preference)
			{
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				boolean vibrate = prefs.getBoolean("chatNotificationVibrateCheckBox", false);
				String sound = prefs.getString("chatNotificationSound", null);
				notification(vibrate, sound, "New Message");
				return true;
			}
		});
	}

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode == -1)
			return;

		Editor editor = prefs.edit();
		editor.putString("avatarFile", "true");
		if(prefs.contains("avatar"))
			editor.remove("avatar");
		editor.commit();
		new ProfilePictureDialog.ProfileUploadTask().execute("profilePic.png","false");
	}*/

	private void notification(boolean vibrate, String sound, String type)
	{
		String username = "testUser";
		Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

		builder.setSmallIcon(R.drawable.ic_chat)
				.setContentIntent(contentIntent)
				.setContentTitle("ClassMe")
				.setContentText(type+" test")
				.setContentInfo(type)
				.setWhen(System.currentTimeMillis())
				.setTicker(type+" test notification")
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_LIGHTS)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.user_icon));

		if(sound == null)
			builder.setDefaults(Notification.DEFAULT_SOUND);
		else
			builder.setSound(Uri.parse(sound));

		if(vibrate)
			builder.setDefaults(Notification.DEFAULT_VIBRATE);

		if(type.equals("New Message"))
		{
			builder.setStyle(new NotificationCompat.BigTextStyle()
					.bigText(username + ": " + "This is a test message")
					.setSummaryText("(Tap to dismiss test)"));
		}

		mNotificationManager.notify(100, builder.build());
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
	{
		/*if(!prefs.contains("gcmFalse") && key.equals("notificationCheckbox"))
		{
			if(prefs.getBoolean(key, false))
			{
				try
				{
					GCMRegistrar.checkDevice(this);
					GCMRegistrar.checkManifest(this);
					final String regId = GCMRegistrar.getRegistrationId(this);
					if(regId.equals(""))
					{
						GCMRegistrar.register(this, "932974086510");
					}
				}
				catch(UnsupportedOperationException ex){Editor editor = prefs.edit();editor.putBoolean("gcmFalse", false);editor.commit();}
			}
			else
			{
				try
				{
					GCMRegistrar.unregister(this);
				}
				catch(UnsupportedOperationException ex){Editor editor = prefs.edit();editor.putBoolean("gcmFalse", false);editor.commit();}
			}
		}*/
	}
}