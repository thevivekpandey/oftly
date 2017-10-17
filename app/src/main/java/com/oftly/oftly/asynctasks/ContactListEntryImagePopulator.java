package com.oftly.oftly.asynctasks;

import com.oftly.oftly.CallViewHolder;
import com.oftly.oftly.ImageLib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class ContactListEntryImagePopulator extends AsyncTask<Void, Void, Drawable> {
	Context context;
	String photoURI;
	CallViewHolder holder;
	int position;
	String canonicalPhoneNumber;
	String text;
	
	public ContactListEntryImagePopulator(Context context, CallViewHolder holder, String photoURI, 
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
		/* Should quickly check if photo exists. If it 
		 * does, color with gray rather than random color.
		 */
		int color = ImageLib.getRandomColor(canonicalPhoneNumber);
		holder.photoView.setBackgroundColor(color);
	}
	@Override
	protected Drawable doInBackground(Void...voids) {
		Bitmap b = ImageLib.getPhotoBitmap(context, photoURI);
		if (b == null) {
			return null;
		}
		return new BitmapDrawable(context.getResources(), b);
	}

	protected void onPostExecute(Drawable drawable) {
		/*if (holder.position != position) {
			Log.v("VIVEK", "Ignorning: " + holder.position + ", " + position);
			return;
		}*/
		if (drawable == null) {
			int color = ImageLib.getRandomColor(canonicalPhoneNumber);
			holder.photoView.setBackgroundColor(color);
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
