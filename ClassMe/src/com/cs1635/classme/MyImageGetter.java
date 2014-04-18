package com.cs1635.classme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.text.Html;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MyImageGetter implements Html.ImageGetter
{
	Context context;
	TextView textView;
	static LruCache<String,Drawable> cache = new LruCache<String, Drawable>(5);

	public MyImageGetter(TextView t, Context c)
	{
		context = c;
		textView = t;
	}

	@Override
	public Drawable getDrawable(String source)
	{
		final Drawable[] d = {context.getResources().getDrawable(R.drawable.ic_launcher)};
		Ion.with(context).load(source).asBitmap().setCallback(new FutureCallback<Bitmap>()
		{
			@Override
			public void onCompleted(Exception e, Bitmap result)
			{
				d[0] = new BitmapDrawable(context.getResources(),result);
				d[0].setBounds(0, 0, result.getWidth(), result.getHeight());
				// i don't know yet a better way to refresh TextView
				// mTv.invalidate() doesn't work as expected
				CharSequence t = textView.getText();
				textView.setText(t);
			}
		});

		return d[0];
	}
}