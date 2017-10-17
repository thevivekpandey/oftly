package com.oftly.oftly;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactDetailCallLogAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	Context context;
	Contact contact;
	ArrayList<Call>calls;
	Typeface typeface;

	public ContactDetailCallLogAdapter(Context context, Contact contact, String canonicalPhoneNumber) {
		this.context = context;
		this.contact = contact;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		calls = new ArrayList<Call>();

		Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
				new String[] {CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION},
				null,
				null,
				CallLog.Calls.DATE + " DESC");
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

		while(managedCursor.moveToNext()) {
			String phoneNumber = managedCursor.getString(number);
			int callType = managedCursor.getInt(type);
			long callDate = managedCursor.getLong(date);
			int callDuration = managedCursor.getInt(duration);
			if (canonicalPhoneNumber.equals(Util.getCanonicalPhoneNumber(phoneNumber))) {
				calls.add(new Call(phoneNumber, callDate, callDuration, callType));
			}
		}
		managedCursor.close();
		typeface = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Light.ttf");
	}
	
	@Override
	public int getCount() {
		return calls.size();
	}

	@Override
	public Object getItem(int position) {
		return calls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout relativeLayout = (RelativeLayout)inflater.inflate(R.layout.contact_detail_call, null);
		Call call = calls.get(position);
		
		/* Call type */
		ImageView imageView = (ImageView)relativeLayout.findViewById(R.id.contact_detail_call_type);
		imageView.setImageResource(Util.getCallTypeImageResourceId(call.type));
		
		/* Call time */
		TextView timeTextView = (TextView)relativeLayout.findViewById(R.id.contact_detail_call_time);
		timeTextView.setTypeface(typeface);
		timeTextView.setText(Util.getReadableCallTime(call.date));
		
		/* Call duration */
		TextView durationTextView = (TextView)relativeLayout.
												findViewById(R.id.contact_detail_duration);
		durationTextView.setTypeface(typeface);
		String callDuration = Util.convertSecToString(call.duration);
		durationTextView.setText(callDuration);
		return relativeLayout;
	}
}

