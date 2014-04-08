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
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.TextMessage;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Robert McDermot on 3/27/14.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Bundle bundle = intent.getExtras();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<TextMessage>>(){}.getType();
		ArrayList<TextMessage> messages = new ArrayList<TextMessage>();
		if(prefs.contains(bundle.getString("id")+"-history")) //do we have any previous history for this conversation?
			messages = gson.fromJson(prefs.getString(bundle.getString("id")+"-history",null), type);

		Type listOfStrings = new TypeToken<ArrayList<String>>(){}.getType();
		ArrayList<String> usernames = gson.fromJson(bundle.getString("usernames"), listOfStrings);

		messages.add(new TextMessage(bundle.getString("text"),bundle.getString("sender"),bundle.getString("sentTime"),bundle.getString("id"),usernames));
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(bundle.getString("id")+"-history",gson.toJson(messages,type));
		edit.apply();

		//if message coming in from someone else, add it to the chat activity if the id matches
		if(bundle.getString("id").equals(ChatActivity.id) && !bundle.getString("sender").equals(prefs.getString("loggedIn",null)))
			ChatActivity.addMessage(bundle.getString("text"),bundle.getString("sender"),bundle.getString("sentTime"),usernames);

		//if the chat activity is not currently showing, then make a notification
		if(!ChatActivity.isResumed || !bundle.getString("id").equals(ChatActivity.id) && prefs.getBoolean("chatNotificationCheckbox",false))
			new GetUserIconTask(context,bundle,"Message",Integer.parseInt(bundle.getString("id"))).execute(bundle.getString("sender"));
	}

	private void notification(Context context, Bundle bundle, String type, int id, Bitmap userIcon)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean vibrate = prefs.getBoolean("chatNotificationVibrateCheckBox", false);
		String sound = prefs.getString("chatNotificationSound", null);

		Intent resultIntent = new Intent(context, ChatActivity.class);
		Bundle bundle1 = new Bundle();
		bundle1.putString("id",bundle.getString("id"));
		bundle1.putString("usernames",bundle.getString("usernames"));
		resultIntent.putExtras(bundle1);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(ChatActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =	stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

		builder.setContentIntent(resultPendingIntent)
				.setContentTitle("ClassMe")
				.setContentText("New message from " + bundle.getString("sender"))
				.setContentInfo(type)
				.setWhen(System.currentTimeMillis())
				.setTicker(bundle.getString("text"))
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_LIGHTS)
				.setLargeIcon(userIcon)
				.setSmallIcon(R.drawable.ic_launcher);

		if(sound == null)
			builder.setDefaults(Notification.DEFAULT_SOUND);
		else
			builder.setSound(Uri.parse(sound));

		if(vibrate)
			builder.setDefaults(Notification.DEFAULT_VIBRATE);

		if(type.equals("Message"))
		{
			builder.setStyle(new NotificationCompat.BigTextStyle()
					.bigText(bundle.getString("sender") + ": " + bundle.getString("text"))
					.setSummaryText("New message from " + bundle.getString("sender")));
		}

		notificationManager.notify(id, builder.build());
	}

	private class GetUserIconTask extends AsyncTask<String,Void,Bitmap>
	{
		Context context;
		Bundle bundle;
		String type;
		int id;

		public GetUserIconTask(Context c, Bundle bundle, String type, int id)
		{
			context = c;
			this.bundle = bundle;
			this.type = type;
			this.id = id;
		}

		@Override
		protected Bitmap doInBackground(String... args)
		{
			try
			{
				URL url = new URL("http://classmeapp.appspot.com/fileRequest?username="+args[0]);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(10000);
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();

				Bitmap userIcon = BitmapFactory.decodeStream(is);

				int height = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_height);
				int width = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_width);
				userIcon = Bitmap.createScaledBitmap(userIcon, width, height, false);

				return  userIcon;
			}
			catch(Exception ex){ex.printStackTrace();}

			return null;
		}

		@Override
		protected void onPostExecute(Bitmap userIcon)
		{
			notification(context, bundle, type, id, userIcon);
		}
	}
}
