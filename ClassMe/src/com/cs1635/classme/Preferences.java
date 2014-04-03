package com.cs1635.classme;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class Preferences extends PreferenceActivity
{
	Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
	}

	public static class MyPreferenceFragment extends PreferenceFragment
	{
		SharedPreferences prefs;

		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);

			prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			if(prefs.contains("gcmFalse"))
			{
				CheckBoxPreference enableNotifications = (CheckBoxPreference) findPreference("notificationCheckbox");
				if(enableNotifications != null)
				{
					enableNotifications.setChecked(false);
					enableNotifications.setEnabled(false);
					enableNotifications.setSummary("Your device does not support Google Cloud Messaging");
				}
			}

			Preference changePassword = findPreference("changePassword");
			if(changePassword != null)
			{
				changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
				{
					public boolean onPreferenceClick(Preference preference)
					{
						new ChangePasswordDialog(getActivity());
						return true;
					}
				});
			}

			Preference profilePicture = findPreference("profilePicture");
			if(profilePicture != null)
			{
				profilePicture.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
				{
					public boolean onPreferenceClick(Preference preference)
					{
						//new ProfilePictureDialog(context);
						return true;
					}
				});
			}

			Preference chatNotificationTest = findPreference("chatNotificationTest");
			if(chatNotificationTest != null)
			{
				chatNotificationTest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
				{
					public boolean onPreferenceClick(Preference preference)
					{
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
						boolean vibrate = prefs.getBoolean("chatNotificationVibrateCheckBox", false);
						String sound = prefs.getString("chatNotificationSound", null);
						notification(vibrate, sound, "New Message");
						return true;
					}
				});
			}
		}

		private void notification(boolean vibrate, String sound, String type)
		{
			String username = "testUser";
			Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());

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
	}
}