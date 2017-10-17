package com.oftly.oftly.asynctasks;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.oftly.oftly.Util;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class ContactListUploaderTask {
	Context context;
	public ContactListUploaderTask(Context context) {
		this.context = context;
	}
	/*@Override
	protected Void doInBackground(String... arg) {
		String[] projections = new String[] {Phone.NUMBER, Phone.PHOTO_URI};
		Cursor phones = context.getContentResolver()
				.query(Phone.CONTENT_URI, 
						projections,
						null,
						null,
						null);
		int phoneIndex = phones.getColumnIndex(Phone.NUMBER);
		int photoURIIndex = phones.getColumnIndex(Phone.PHOTO_URI);

		String contactList = "";
		int i = 0;
		while (phones.moveToNext())	{
			String phoneNumber = phones.getString(phoneIndex);
			String canonicalPhoneNumber = Util.getCanonicalPhoneNumber(phoneNumber);
			String photoURI = phones.getString(photoURIIndex);
			String photoURIAvailable = "0";
			if (photoURI != null && photoURI.length() > 10) {
				photoURIAvailable = "1";
			}
			contactList += canonicalPhoneNumber + "\t" + photoURIAvailable + "\n";
			i++;
		}
		Log.v("VIVEK", "Found " + i + " contacts");
		phones.close();
		
		ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(Util.CONTACT_LIST, contactList));     
		nameValuePairs.add(new BasicNameValuePair(Util.PHONE_NUMBER, Util.getStoredValue(context, Util.PHONE_NUMBER)));     
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.getoftly.com/upload_contact_list.php");
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			Log.v("VIVEK", "Status code is " + response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			Log.v("VIVEK", "Exception in uploadContactList");
		}
		return null;
	}*/
}
