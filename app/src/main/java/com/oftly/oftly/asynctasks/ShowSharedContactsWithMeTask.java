package com.oftly.oftly.asynctasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oftly.oftly.Contact;
import com.oftly.oftly.ContactBlockFragment;
import com.oftly.oftly.ImageLib;
import com.oftly.oftly.MainAdapter;
import com.oftly.oftly.R;
import com.oftly.oftly.SharedContactsWithMe;
import com.oftly.oftly.Util;

public class ShowSharedContactsWithMeTask extends AsyncTask<Contact, String, SharedContactsWithMe> {
	Context context;
	MainAdapter adapter;
	Typeface typeface, typefaceBold;
	String ph;

	public ShowSharedContactsWithMeTask(Context context, MainAdapter adapter) {
		this.context = context;
		this.adapter = adapter;
		typeface = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Light.ttf");
		typefaceBold = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Regular.ttf");
		ph = Util.getCanonicalPhoneNumber(Util.getStoredValue(context, Util.PHONE_NUMBER));
	}
	@Override
	protected SharedContactsWithMe doInBackground(Contact... contacts) {
		String url = "http://www.getoftly.com/get_shared_with_me_contacts.php?dst=" + ph;
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
		SharedContactsWithMe sharedContacts = new SharedContactsWithMe();
		
		try {
			Util.Log(builder.toString());
			JSONObject json = new JSONObject(builder.toString());
			JSONArray details = json.getJSONArray("details");
			for (int i = 0; i < details.length(); i++) {
			    JSONObject entry = details.getJSONObject(i);
			    String src = entry.getString("src");
			    String target = entry.getString("tgt");
			    
			    Contact contact = adapter.contactDetails.get(target);
			    if (contact != null) {
				    if (adapter.contactDetails.containsKey(src)) {
				    	sharedContacts.addTarget(src, contact);
				    }
			    }
			}
		} catch (JSONException e) {
			Util.Log("JSON Exception");
			e.printStackTrace();
		}
		return sharedContacts;
	}

	@Override
	protected void onPostExecute(SharedContactsWithMe sharedContacts) {
		TextView textView = (TextView)((Activity)context).findViewById(R.id.show_shared_text);
		String message;
		if (sharedContacts.getSrcs().size() == 0) {
			message = "No one has yet shared photos with you!";
		} else {
			message = "Your friends have shared these photos with you";
		}
		textView.setTypeface(typeface);
		textView.setText(message);

		ListView listView = (ListView)((Activity)context).findViewById(R.id.show_shared_list);		
		listView.setAdapter(new ShareAdapter(context, R.id.show_shared_list, sharedContacts));		
	}
	private class ShareAdapter extends ArrayAdapter<LinearLayout> {
		int[] headerIndices;
		private LayoutInflater inflater;
		SharedContactsWithMe sharedContacts;

		public ShareAdapter(Context context, int resourceId, SharedContactsWithMe sharedContacts) {
			super(context, resourceId);
			this.sharedContacts = sharedContacts;
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			typeface = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Light.ttf");
			typefaceBold= Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Regular.ttf");

			Set srcs = sharedContacts.getSrcs();
			int size = sharedContacts.getSrcCount();
			
			if (size == 0) {
				return;
			}
			headerIndices = new int[size];
			headerIndices[0] = 0;
			Iterator<String> iter = srcs.iterator();
			int i = 1;
			while (iter.hasNext()) {
				if (i >= size) {
					break;
				}
				String src = iter.next();
				int num = sharedContacts.getTargets(src).size();
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
			Iterator<String> iter = sharedContacts.getSrcs().iterator();
			while (iter.hasNext()) {
				count += sharedContacts.getTargets(iter.next()).size() + 1;
			}
			return count;
		}
		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			Util.Log("pos = " + pos);
			if (getItemViewType(pos) == 0) {
				return getHeaderView(pos, parent);
			} else {
				return getItemView(pos, parent);
			}
		}
		private LinearLayout getHeaderView(int pos, ViewGroup parent) {
			LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.shared_photo_with_me_header, parent, false);
			
			Contact src = getSrcContact(pos);
			
			TextView textView = (TextView)linearLayout.findViewById(R.id.shared_photo_with_me_header_pre_text);
			textView.setTypeface(typeface);
			textView.setText(src.getName() + " gifted you these photos:");
			return linearLayout;
		}
		private RelativeLayout getItemView(int pos, ViewGroup parent) {
			RelativeLayout relativeLayout = (RelativeLayout)inflater.inflate(R.layout.shared_photo_with_me_item, parent, false);
			
			Contact targetContact = null;
			int count = 0;
			Iterator<String> iter = sharedContacts.getSrcs().iterator();
			String src = Util.INVALID;
			while (iter.hasNext()) {
				src = iter.next();
				ArrayList<Contact> list = sharedContacts.getTargets(src);
				count += list.size() + 1;
				if (count > pos) {
					targetContact = list.get(count - pos - 1);
					break;
				}
			}
			TextView photoView = (TextView)relativeLayout.findViewById(R.id.shared_photo_with_me_item_name);
			photoView.setBackgroundResource(R.drawable.empty_photo);

			String srcPhone = Util.getCanonicalPhoneNumber(src);
			String targetPhone = Util.getCanonicalPhoneNumber(targetContact.getPhone());
			Bitmap b = ImageLib.retrieveImage(context, targetPhone);
			if (b == null) {
				new DownloadImage(context, photoView).execute(srcPhone, targetPhone);
			} else {
				Util.Log("I got image locally");
				Drawable d = new BitmapDrawable(context.getResources(), b);
				photoView.setBackground(d);
			}
			/* Name or else phone number */
			String nameOrPhoneNumber = targetContact.getName() != null ? targetContact.getName() : targetContact.getPhone();
			TextView nameOrPhoneNumberView = 
					(TextView)relativeLayout.findViewById(R.id.shared_photo_with_me_item_name_or_phone_number);
			nameOrPhoneNumberView.setTypeface(typefaceBold);
			nameOrPhoneNumberView.setText(nameOrPhoneNumber);

			return relativeLayout;
		}
		private Contact getSrcContact(int pos) {
			int index = -1;
			for (int i = 0; i < headerIndices.length; i++) {
				if (pos == headerIndices[i]) {
					index = i;					
				}
			}
			Set srcs = sharedContacts.getSrcs();
			Iterator<String> iter = srcs.iterator();
			int i = 0;
			String srcPhone = Util.INVALID;
			while (iter.hasNext() && i <= index) {
				srcPhone = iter.next();
				i++;
			}
			return adapter.contactDetails.get(srcPhone);
		}
		/*private void updateSharingIntention(boolean share, String dst, String tgt) {
			String url = "http://www.getoftly.com/update_share_intent.php?" + 
							"src=" + ph + "&dst=" + dst + "&tgt=" + tgt + "&share=" + share;
			new HttpGetExecutor().execute(url);
		}*/
	}

}
