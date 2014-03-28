package com.cs1635.classme;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shared.TextMessage;

import java.lang.reflect.Type;
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

		messages.add(new TextMessage(bundle.getString("text"),bundle.getString("sender"),bundle.getString("sentTime")));
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(bundle.getString("id")+"-history",gson.toJson(messages,type));
		edit.apply();

		//if message coming in from someone else, add it to the chat activity if the id matches
		if(bundle.getString("id").equals(ChatActivity.id) && !bundle.getString("sender").equals(prefs.getString("loggedIn",null)))
			ChatActivity.addMessage(bundle.getString("text"),bundle.getString("sender"),bundle.getString("sentTime"));

		//if the chat activity is not currently showing, then make a notification
		if(!ChatActivity.isResumed && !ChatActivity.id.equals(bundle.getString("id")))
		{
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(context, ChatActivity.class);
			Bundle bundle1 = new Bundle();
			bundle1.putString("id",bundle.getString("id"));
			resultIntent.putExtras(bundle1);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setSmallIcon(R.drawable.ic_launcher);
			builder.setContentTitle("New Message");
			builder.setContentText(bundle.getString("text"));

			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(ChatActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =	stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(resultPendingIntent);
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			// id allows you to update the notification later on.
			notificationManager.notify(Integer.parseInt(bundle.getString("id")), builder.build());
		}
	}
}
