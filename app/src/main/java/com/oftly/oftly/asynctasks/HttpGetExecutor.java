package com.oftly.oftly.asynctasks;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.oftly.oftly.Util;

import android.os.AsyncTask;

public class HttpGetExecutor extends AsyncTask<String, Integer, Void>{
	public HttpGetExecutor() {
	}

	@Override
	protected Void doInBackground(String... params) {
		HttpClient httpClient = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet(params[0]);
	    try {
	    	HttpResponse response = httpClient.execute(httpGet);
	    	Util.Log("Return code is " + response.getStatusLine().getStatusCode());
	    } catch (Exception e) {
	    	Util.Log("VIVEK", "Some exception");
	    }
	    return null;
	}
}
