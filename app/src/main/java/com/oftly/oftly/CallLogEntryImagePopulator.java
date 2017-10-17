package com.oftly.oftly;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

public class CallLogEntryImagePopulator extends AsyncTask <Void, Void, Drawable> {
	Context context;
	String photoURI;
	CallViewHolder holder;
	int position;
	String canonicalPhoneNumber;
	String text;
	
	public CallLogEntryImagePopulator(Context context, CallViewHolder holder, String photoURI, 
							int position, String canonicalPhoneNumber, String text) {
		this.context = context;
		this.photoURI = photoURI;
		this.holder = holder;
		this.position = position;
		this.canonicalPhoneNumber = canonicalPhoneNumber;
		this.text = text;
	}
	@Override
	protected void onPreExecute() {
		int color = ImageLib.getRandomColor(canonicalPhoneNumber);
		holder.photoView.setBackgroundDrawable(null);
		holder.photoView.setBackgroundColor(color);
		holder.photoView.setText("");
		/*if (photoURI == null) {
			if (!text.equals("INVALID")) {
				holder.photoView.setText(text);
			} else {
				holder.photoView.setText("");
			}
		}*/
	}

	@Override
	protected Drawable doInBackground(Void...voids) {
		Bitmap b = ImageLib.getPhotoBitmap(context, photoURI);
		if (b == null) {
			return null;
		}
		
		/* Scale the image here. This will result in UI thread
		 * in onPostExecute() having to do smaller amount of
		 * work, though overall work done by the system is
		 * increased.
		 */
		return new BitmapDrawable(context.getResources(), b);

	    /*float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;

		//Log.v("VIVEK", "scaled Density is " + scaledDensity);
        int width = b.getWidth();
        int height = b.getHeight();
        float lpwidth, lpheight;
        
        int target_width_px = (int)((float)width * scaledDensity);
        int target_height_px = (int)((float)height * scaledDensity);

        if (width < target_width_px || height < target_height_px ) {
    		return new BitmapDrawable(context.getResources(), b);
        } else {
        	float aspectRatio = (float)height / (float)width;
        	if (aspectRatio > 1.0f) {
        		lpheight = target_height_px;
        		lpwidth = lpheight / aspectRatio;
        	} else {
        		lpwidth = target_width_px;
        		lpheight = lpwidth * aspectRatio;
        	}
        	b = Bitmap.createScaledBitmap(b, (int)lpwidth, (int)lpheight, false);
        	return new BitmapDrawable(context.getResources(), b);
        }*/
	}

	protected void onPostExecute(Drawable drawable) {
		if (holder.position != position) {
			Log.v("VIVEK", "Ignorning: " + holder.position + ", " + position);
			return;
		}
		if (drawable == null) {
			if (!text.equals("INVALID")) {
				holder.photoView.setText(text);
			} else {
				holder.photoView.setText("");
			}
		} else {
			holder.photoView.setBackgroundDrawable(drawable);
			holder.photoView.setText("");
		}
	}
}