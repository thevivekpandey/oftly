package com.oftly.oftly.asynctasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ActivityLogger extends AsyncTask<String, Integer, Integer>{
	Context ctx;
	
	public ActivityLogger(Context ctx) {
		this.ctx = ctx;
	}
	/* 
	 * Append new entry to file (create file if it does not exist).
	 * Then, try to have a get request for each line. If get request
	 * fails at any stage, do not delete the file, else delete the file.
	 */
	protected Integer doInBackground(String... data) {
		Log.v("VIVEK", "Start logging");
		String fileName = "logcall.txt";
		
		String date = (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss")).format(new Date());
		String me = data[0];
		String you = data[1];
		Log.v("VIVEK", "Writing new entry");
		writeToInternalStorage(ctx, fileName, date + " " + me + " " + you + "\n");
		boolean b = uploadFileToServer(fileName);
		if (b) {
			Log.v("VIVEK", "Will delete file now");
			deleteFileFromInternalStorage(fileName);
		}
		
	    Log.v("VIVEK", "End logging");
		return 0;
	}
	
	public static void writeToInternalStorage(Context ctx, String fileName, String text) {
		BufferedWriter writer = null;
		try {
			FileOutputStream openFileOutput = ctx.openFileOutput(fileName, Context.MODE_PRIVATE | Context.MODE_APPEND);
			openFileOutput.write(text.getBytes());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public String readFileFromInternalStorage(String fileName) {
		String eol = System.getProperty("line.separator");
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(ctx.openFileInput(fileName)));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = input.readLine()) != null) {
				buffer.append(line + eol);
			}
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	void deleteFileFromInternalStorage(String fileName) {
		ctx.deleteFile(fileName);
	}
	boolean uploadFileToServer(String fileName) {
		
		String lines = readFileFromInternalStorage(fileName);
		String entries[] = lines.split("\n");
		for (String entry : entries) {
			String data[] = entry.split(" ");
			Log.v("VIVEK", "upload for time" + data[0]);
			boolean b = makeOneGetRequest(data[0], data[1], data[2]);
			if (!b) {
				return false;
			}
		}
		return true;
		/*Log.v("VIVEK", "Trying to upload the file");
		HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://untabbed.com/mydialer/upload-log.php");
        InputStreamEntity reqEntity;
        Log.v("VIVEK", "Step 1");
		try {
			//reqEntity = new InputStreamEntity(new FileInputStream(fileName), -1);
			reqEntity = new InputStreamEntity(ctx.openFileInput(fileName), -1);
	        reqEntity.setContentType("binary/octet-stream");    
	        reqEntity.setChunked(true);
	        httpPost.setEntity(reqEntity);
	        HttpResponse response = httpClient.execute(httpPost);
	        Log.v("VIVEK", response.getClass().getSimpleName());
	        httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
			Log.v("VIVEK", e.getClass().getSimpleName());
		}*/
	}
	boolean makeOneGetRequest(String time, String me, String you) {
		HttpClient httpClient = new DefaultHttpClient();
	    String url = "http://untabbed.com/mydialer/log_call.php?"
	    				+ "time=" + time
	    				+ "&me=" + me
	    				+ "&you=" + you;
	    HttpGet httpGet = new HttpGet(url);
	    try {
	    	HttpResponse response = httpClient.execute(httpGet);
	    	if (response.getEntity().getContentLength() != 4) {
	    		return false;
	    	}
	    } catch (Exception e) {
	    	Log.v("VIVEK", "Some exception");
	    	return false;
	    }		
	    return true;
	}
}