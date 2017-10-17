package com.oftly.oftly.asynctasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.util.Log;

public class UploadTask extends AsyncTask<URL, Integer, Integer>{
	protected Integer doInBackground(URL... urls) {
		Log.v("VIVEK", "Start uploading");	
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://ec2-174-129-130-200.compute-1.amazonaws.com:9000/upload");

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("id", "h2345"));
	        nameValuePairs.add(new BasicNameValuePair("stringdata", "Hi"));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        Log.v("VIVEK", response.toString());
	    } catch (ClientProtocolException e) {
	    } catch (IOException e) {
	    }
		Log.v("VIVEK", "Stop uploading");
		return 0;
	}
}
