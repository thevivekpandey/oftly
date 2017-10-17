package com.oftly.oftly;

import java.io.IOException;

import com.oftly.oftly.asynctasks.UploadImage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class ContactPhotoUploader extends BroadcastReceiver {

	public static final int BATCH_SIZE = 5;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("VIVEK", "onReceive() in ContactPhotoUploader is called");

		DatabaseHelper d = new DatabaseHelper(context);
		
		String[] projections = new String[] {Phone.NUMBER, Phone.PHOTO_URI};
		Cursor phones = context.getContentResolver()
				.query(Phone.CONTENT_URI, 
						projections, 
						Phone.PHOTO_URI + "!= ?",
						new String[] {""},
						"RANDOM()");
		int phoneIndex = phones.getColumnIndex(Phone.NUMBER);
		int photoIndex = phones.getColumnIndex(Phone.PHOTO_URI);

		int i = 0;
		while (phones.moveToNext())	{
			i++;
			if (i > BATCH_SIZE) {
				break;
			}
			String phoneNumber = phones.getString(phoneIndex);
			String canonicalPhoneNumber = Util.getCanonicalPhoneNumber(phoneNumber);
			String photoURI = phones.getString(photoIndex);
			Contact contact = new Contact(phoneNumber);
			contact.setPhotoURI(photoURI);
			boolean exists = d.phoneNumberExists(phoneNumber);
			if (!exists) {
				try {
					/* Try to upload photo. If you succeed, then you update db. */
					uploadPhoto(context, contact);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				//Log.v("VIVEK", phoneNumber + "Exists in db");
			}
		}
		phones.close();
	}
	private void uploadPhoto(Context context, Contact contact) throws IOException {
        new UploadImage(context).execute(contact);
	}
}
