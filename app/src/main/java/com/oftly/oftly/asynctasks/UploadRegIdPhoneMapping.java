package com.oftly.oftly.asynctasks;

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
import android.os.AsyncTask;
import android.util.Log;

public class UploadRegIdPhoneMapping extends AsyncTask<String, String, Void>{

	public static final String INVALID = "invalid";
	Context context;
	public UploadRegIdPhoneMapping(Context context) {
		this.context = context;
	}
	@Override
	protected Void doInBackground(String... str) {
		try {
			String regid = str[0];
			String phoneNumber = str[1];
	        ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();        
	        nameValuePairs.add(new BasicNameValuePair(Util.REG_ID,regid));
	        nameValuePairs.add(new BasicNameValuePair(Util.PHONE_NUMBER, phoneNumber));

	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost("http://www.getoftly.com/register.php");
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        if (response.getStatusLine().getStatusCode() == 200) {
	        	Util.setStoredValue(context, Util.MAPPING_STORED, Util.TRUE);
	        }
		} catch (Exception e) {
			Log.v("VIVEK", "Exception in UploadRegIdPhoneMapping");
		}
		return null;
	}
}
