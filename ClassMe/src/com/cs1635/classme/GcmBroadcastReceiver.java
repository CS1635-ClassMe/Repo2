package com.cs1635.classme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

/**
 * Created by Robert McDermot on 3/27/14.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Bundle bundle = intent.getExtras();
		Toast.makeText(context,"Got message: " + bundle.getString("text"),Toast.LENGTH_SHORT);
	}
}
