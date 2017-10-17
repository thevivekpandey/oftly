package com.oftly.oftly.asynctasks;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.oftly.oftly.Contact;
import com.oftly.oftly.DatabaseHelper;
import com.oftly.oftly.UploadedPhotoEntry;
import com.oftly.oftly.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

public class UploadImage extends AsyncTask<Contact, String, Void>{

	Context context;
	public UploadImage(Context context) {
		this.context = context;
	}
	@Override
	protected Void doInBackground(Contact... contacts) {
		try {
	        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(contacts[0].getPhotoURI()));
	        int width = bitmap.getWidth();
	        int height = bitmap.getHeight();
	        float lpwidth, lpheight;
	        if (width >= 120 && height >= 120) {
	        	float aspectRatio = (float)height / (float)width;
	        	if (aspectRatio > 1.0f) {
	        		lpheight = 120f;
	        		lpwidth = lpheight / aspectRatio;
	        	} else {
	        		lpwidth = 120f;
	        		lpheight = lpwidth * aspectRatio;
	        	}
	        	bitmap = Bitmap.createScaledBitmap(bitmap, (int)lpwidth, (int)lpheight, false);
	        }
	        ByteArrayOutputStream stream = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
	        byte [] byte_arr = stream.toByteArray();
	        String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
	        
	        ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
	        nameValuePairs.add(new BasicNameValuePair("my_phone", Util.getStoredValue(context, Util.PHONE_NUMBER)));	        
	        nameValuePairs.add(new BasicNameValuePair("image",image_str));
	        nameValuePairs.add(new BasicNameValuePair("phone", Util.getCanonicalPhoneNumber(contacts[0].getPhone())));

	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost("http://www.getoftly.com/upload.php");
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        /*if (response.getStatusLine().getStatusCode() == 200) {
	        	return contacts[0].getPhone();
	        } else {
	        	return Util.INVALID;
	        }*/
		} catch (Exception e) {
			//return Util.INVALID;
			e.printStackTrace();
		}
		return null;
	}
	
	/*@Override
	protected void onPostExecute(String phoneNumber) {
		if (!phoneNumber.equals(Util.INVALID)) {
			DatabaseHelper d = new DatabaseHelper(context);
			UploadedPhotoEntry e = new UploadedPhotoEntry(Util.getCanonicalPhoneNumber(phoneNumber));
			d.createUploadedPhotoEntry(e);
			Log.v("VIVEK", "adding " + Util.getCanonicalPhoneNumber(phoneNumber));
		} else {
			Log.v("VIVEK", "not adding " + Util.getCanonicalPhoneNumber(phoneNumber));
		}
	}*/
}
