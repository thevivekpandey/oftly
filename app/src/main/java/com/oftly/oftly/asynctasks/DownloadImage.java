package com.oftly.oftly.asynctasks;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.oftly.oftly.ContactBlockFragment;
import com.oftly.oftly.ImageLib;
import com.oftly.oftly.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.TextView;

public class DownloadImage extends AsyncTask<String, Integer, Bitmap>{
	Context context;
	TextView textView;
	public DownloadImage(Context context, TextView textView) {
		this.context = context;
		this.textView = textView;
	}
	@Override
	protected Bitmap doInBackground(String... params) {
		try {
			String srcPhone = params[0];
			String targetPhone = params[1];
			URL url = new URL("http://getoftly.com/get_photo.php?src=" + srcPhone + "&tgt=" + targetPhone);
			HttpURLConnection connection = (HttpURLConnection)(url.openConnection());
			connection.setDoInput(true);
			connection.connect();
			
			InputStream input = connection.getInputStream();
			Bitmap b = BitmapFactory.decodeStream(input);
			ImageLib.storeImage(context, b, targetPhone);
			return b;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	protected void onPostExecute(Bitmap b) {
		Drawable d = new BitmapDrawable(context.getResources(), b);
		textView.setBackground(d);
		
		/* Once the image is downloaded, we refresh the contact block. */
		FragmentManager fragmentManager = ((Activity)context).getFragmentManager();
		ContactBlockFragment contactBlockFragment = (ContactBlockFragment) fragmentManager.findFragmentById(R.id.fragment_contacts);
		contactBlockFragment.newPhotoAdded();
	}
}
