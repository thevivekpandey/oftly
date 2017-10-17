package com.oftly.oftly.asynctasks;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.oftly.oftly.Contact;
import com.oftly.oftly.ImageLib;
import com.oftly.oftly.MainAdapter;
import com.oftly.oftly.R;
import com.oftly.oftly.SharableContacts;
import com.oftly.oftly.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowSharableContactsTask extends AsyncTask<Contact, String, SharableContacts> {
	Context context;
	Typeface typeface, typefaceBold;
	MainAdapter adapter;
	String ph;
	HashMap<String, String> shareStatus = new HashMap<String, String>();
	
	public ShowSharableContactsTask(Context context, MainAdapter adapter) {
		this.context = context;
		this.adapter = adapter;
		typeface = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Light.ttf");
		typefaceBold = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Regular.ttf");
		ph = Util.getCanonicalPhoneNumber(Util.getStoredValue(context, Util.PHONE_NUMBER));
	}
	@Override
	protected SharableContacts doInBackground(Contact... contacts) {
		String url = "http://www.getoftly.com/get_sharable_contacts.php?ph=" + ph;
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		StringBuilder builder = new StringBuilder();

		HttpResponse response;
		try {
			response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
		        HttpEntity entity = response.getEntity();
		        InputStream content = entity.getContent();
		        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		        String line;
		        while ((line = reader.readLine()) != null) {
		          builder.append(line);
		        }
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SharableContacts sharableContacts = new SharableContacts();
		
		try {
			Util.Log(builder.toString());
			JSONObject json = new JSONObject(builder.toString());
			JSONArray details = json.getJSONArray("details");
			for (int i = 0; i < details.length(); i++) {
			    JSONObject entry = details.getJSONObject(i);
			    String dst = entry.getString("dst");
			    String target = entry.getString("tgt");
			    String src_ans_status = entry.getString("src_ans_status");
			    
			    String key = Util.getCanonicalPhoneNumber(dst) + "-" + Util.getCanonicalPhoneNumber(target);
			    shareStatus.put(key, src_ans_status);
			    Contact contact = adapter.contactDetails.get(target);
			    assert(contact != null);
			    if (adapter.contactDetails.containsKey(dst) &&
			    	contact.getPhotoURI() != null && 
			    	contact.getPhotoURI().length() > 0) {
			    	sharableContacts.addTarget(dst, contact);
			    	new UploadImage(context).execute(contact);
			    	//uploadImage(contact);
			    }
			}
		} catch (JSONException e) {
			Util.Log("JSON Exception");
			e.printStackTrace();
		}
		return sharableContacts;
	}
	@Override
	protected void onPostExecute(SharableContacts sharableContacts) {
		TextView textView = (TextView)((Activity)context).findViewById(R.id.share_screen_text);
		String message;
		if (sharableContacts.getDsts().size() == 0) {
			message = "Ask you friends to install Oftly to share contact photos with each other!";
		} else {
			message = "Share contact photos with your friends and improve their address book!";
		}
		textView.setTypeface(typeface);
		textView.setText(message);

		ListView listView = (ListView)((Activity)context).findViewById(R.id.share_screen_list);		
		listView.setAdapter(new ShareAdapter(context, R.id.share_screen_list, sharableContacts));
		
		/*Set dsts = sharableContacts.getDsts();
		Iterator dstIterator = dsts.iterator();
		while(dstIterator.hasNext()) {
			String dst = (String)dstIterator.next();
			for (Contact target : sharableContacts.getTargets(dst)) {
				uploadImage(target);
			}
		}*/
	}
	/*private void uploadImage(Contact contact) {
		try {
	        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(contact.getPhotoURI()));
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
	        nameValuePairs.add(new BasicNameValuePair("phone", Util.getCanonicalPhoneNumber(contact.getPhone())));

	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost("http://www.getoftly.com/upload.php");
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	private class ShareAdapter extends ArrayAdapter<LinearLayout> {
		int[] headerIndices;
		private LayoutInflater inflater;
		SharableContacts sharableContacts;

		public ShareAdapter(Context context, int resourceId, SharableContacts sharableContacts) {
			super(context, resourceId);
			this.sharableContacts = sharableContacts;
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			typeface = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Light.ttf");
			typefaceBold= Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Regular.ttf");

			Set dsts = sharableContacts.getDsts();
			int size = sharableContacts.getDstCount();
			
			if (size == 0) {
				return;
			}
			headerIndices = new int[size];
			headerIndices[0] = 0;
			Iterator<String> iter = dsts.iterator();
			int i = 1;
			while (iter.hasNext()) {
				if (i >= size) {
					break;
				}
				String dst = iter.next();
				int num = sharableContacts.getTargets(dst).size();
				headerIndices[i] = headerIndices[i - 1] + num + 1;
				i++;
			}
		}
		@Override
		public int getViewTypeCount() {
			return 2;
		}
		@Override
		public int getItemViewType(int position) {
			for (int i = 0; i < headerIndices.length; i++) {
				if (position == headerIndices[i]) {
					return 0;
				}
			}
			return 1;
		}
		@Override
		public int getCount() {
			int count = 0;
			Iterator<String> iter = sharableContacts.getDsts().iterator();
			while (iter.hasNext()) {
				count += sharableContacts.getTargets(iter.next()).size() + 1;
			}
			return count;
		}
		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			if (getItemViewType(pos) == 0) {
				return getHeaderView(pos, parent);
			} else {
				return getItemView(pos, parent);
			}
		}
		private LinearLayout getHeaderView(int pos, ViewGroup parent) {
			LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.share_photo_header, parent, false);
			
			Contact dst = getDstContact(pos);
			
			TextView textView = (TextView)linearLayout.findViewById(R.id.share_photo_header_pre_text);
			textView.setTypeface(typeface);
			textView.setText("Gift " + dst.getName() + " these photos:");
			return linearLayout;
		}
		private RelativeLayout getItemView(int pos, ViewGroup parent) {
			RelativeLayout relativeLayout = (RelativeLayout)inflater.inflate(R.layout.share_photo_item, parent, false);
			
			Contact targetContact = null;
			int count = 0;
			Iterator<String> iter = sharableContacts.getDsts().iterator();
			String dst = Util.INVALID;
			while (iter.hasNext()) {
				dst = iter.next();
				ArrayList<Contact> list = sharableContacts.getTargets(dst);
				count += list.size() + 1;
				if (count > pos) {
					targetContact = list.get(count - pos - 1);
					break;
				}
			}
			TextView photoView = (TextView)relativeLayout.findViewById(R.id.share_photo_item_name);
			String photoURI = targetContact.getPhotoURI();
			assert(photoURI != null);
			Bitmap b = ImageLib.getPhotoBitmap(context, photoURI);
			Drawable d = new BitmapDrawable(context.getResources(), b);
			photoView.setBackground(d);
			
			/* Name or else phone number */
			String nameOrPhoneNumber = targetContact.getName() != null ? targetContact.getName() : targetContact.getPhone();
			TextView nameOrPhoneNumberView = 
					(TextView)relativeLayout.findViewById(R.id.share_photo_item_name_or_phone_number);
			nameOrPhoneNumberView.setTypeface(typefaceBold);
			nameOrPhoneNumberView.setText(nameOrPhoneNumber);

			/* "Shared" button */
			TextView sharedView = (TextView)relativeLayout.findViewById(R.id.share_photo_item_shared);
			sharedView.setTypeface(typeface);

			/* On click events */
			ImageView shareImageView    = (ImageView)relativeLayout.findViewById(R.id.share_photo_item_tick);
			TextView shareImageViewText = (TextView)relativeLayout.findViewById(R.id.share_photo_item_tick_text);

			shareImageView.setTag(R.integer.tgt_phone, Util.getCanonicalPhoneNumber(targetContact.getPhone()));
			shareImageView.setTag(R.integer.dst_phone, Util.getCanonicalPhoneNumber(dst));
			shareImageView.setTag(R.integer.brother_view, shareImageViewText);
			shareImageView.setTag(R.integer.shared_button_view, sharedView);
			
			shareImageViewText.setTag(R.integer.tgt_phone, Util.getCanonicalPhoneNumber(targetContact.getPhone()));
			shareImageViewText.setTag(R.integer.dst_phone, Util.getCanonicalPhoneNumber(dst));
			shareImageViewText.setTag(R.integer.brother_view, shareImageView);
			shareImageViewText.setTag(R.integer.shared_button_view, sharedView);
			
			shareImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					updateSharingIntention(true, 
											(String)v.getTag(R.integer.dst_phone), 
											(String)v.getTag(R.integer.tgt_phone));
					v.setVisibility(View.GONE);
					((View)v.getTag(R.integer.brother_view)).setVisibility(View.GONE);
					((View)v.getTag(R.integer.shared_button_view)).setVisibility(View.VISIBLE);
				}
			});
			shareImageViewText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					updateSharingIntention(true, 
											(String)v.getTag(R.integer.dst_phone), 
											(String)v.getTag(R.integer.tgt_phone));
					v.setVisibility(View.GONE);
					((View)v.getTag(R.integer.brother_view)).setVisibility(View.GONE);
					((View)v.getTag(R.integer.shared_button_view)).setVisibility(View.VISIBLE);
				}
			});

			/* Show "Shared" button or tick button. */
			String key = Util.getCanonicalPhoneNumber(dst) + "-" + Util.getCanonicalPhoneNumber(targetContact.getPhone());
			String shareStatusForTarget = shareStatus.get(key);
			if (shareStatusForTarget.equals("0")) {
				shareImageView.setVisibility(View.VISIBLE);
				shareImageViewText.setVisibility(View.VISIBLE);
			} else {
				sharedView.setVisibility(View.VISIBLE);
			}
			return relativeLayout;			
		}
		private Contact getDstContact(int pos) {
			int index = -1;
			for (int i = 0; i < headerIndices.length; i++) {
				if (pos == headerIndices[i]) {
					index = i;					
				}
			}
			Set dsts = sharableContacts.getDsts();
			Iterator<String> iter = dsts.iterator();
			int i = 0;
			String dstPhone = Util.INVALID;
			while (iter.hasNext() && i <= index) {
				dstPhone = iter.next();
				i++;
			}
			return adapter.contactDetails.get(dstPhone);
		}
		private void updateSharingIntention(boolean share, String dst, String tgt) {
			String url = "http://www.getoftly.com/update_share_intent.php?" + 
							"src=" + ph + "&dst=" + dst + "&tgt=" + tgt + "&share=" + share;
			new HttpGetExecutor().execute(url);
		}
	}
}
