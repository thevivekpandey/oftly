package com.oftly.oftly;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

public class Call {
	String phoneNumber;
	long date;
	int duration;
	int type;

	public Call(String phoneNumber, long date, int duration, int type) {
		this.phoneNumber = phoneNumber;
		this.date = date;
		this.duration = duration;
		this.type = type;
	}
	public static ArrayList<Call> getCalls(ContentResolver contentResolver) {
		return getCalls(contentResolver, "", null);
	}
	public static ArrayList<Call> getCalls(ContentResolver contentResolver, 
											String matchText, 
											MainAdapter mainAdapter) {
		ArrayList<Call>calls = new ArrayList<Call>();
		Cursor managedCursor = contentResolver.query(CallLog.Calls.CONTENT_URI,
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
			
			String canonicalPhoneNumber = Util.getCanonicalPhoneNumber(phoneNumber);
			if (matchText.length() == 0) {
				calls.add(new Call(phoneNumber, callDate, callDuration, callType));							
			} else if (!mainAdapter.contactDetails.containsKey(canonicalPhoneNumber)) {
			} else if (mainAdapter.contactDetails.get(canonicalPhoneNumber).getName() == null) {
			} else {
    			String canonicalContactName = mainAdapter.contactDetails
    										.get(canonicalPhoneNumber)
    										.getName()
    										.toLowerCase()
    										.replace(" ", "");
    			String canonicalSearchText = matchText.toLowerCase().replace(" ", "");
    			if (canonicalContactName.contains(canonicalSearchText)) {
    				calls.add(new Call(phoneNumber, callDate, callDuration, callType));											
    			}
			}
		}
		return calls;
	}
}