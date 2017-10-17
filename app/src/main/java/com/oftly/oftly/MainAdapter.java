package com.oftly.oftly;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class MainAdapter {
	private Context mContext;
	
	/* You need two data structures: contactDetails which is indexed by
	 * phone number is required so that you can do analysis of calls
	 * made to a particular phone number. contacts, an array is needed
	 * so that we can provided the functions expected from a class which
	 * is behaving like an adapter.
	 */

	/* We maintain an invariant that a contact is present in contactDetails
	 * iff it is present in contacts too.
	 */
	public HashMap<String, Contact>contactDetails = new HashMap<String, Contact>();
	public ArrayList<Contact>contacts = new ArrayList<Contact>();
	public ArrayList<Contact>all_contacts = new ArrayList<Contact>();
	private static MainAdapter mainAdapter;
	
	private ContentResolver contentResolver;
	/*public static HashMap<String, Contact> getContactDetails() {
		return contactDetails;
	}*/
	public static MainAdapter getMainAdapterInstance(Context context) {
		//mainAdapter = new MainAdapter(context);
		if (mainAdapter == null) {
			Util.Log("Yes, it is null");
			mainAdapter = new MainAdapter(context);
		} else {
			Util.Log("It is not null");
		}
		return mainAdapter;
	}
	public MainAdapter(Context c) {
		/* Crash reports say that c is sometimes null. I do not understand
		 * the code fully well. I am just returning in case c is null, and
		 * hoping that the problem vanishes.
		 */
		if (c == null) {
			return;
		}
		mContext = c;
		contentResolver = mContext.getContentResolver();

		String[] projection = {
								CallLog.Calls.NUMBER,
								CallLog.Calls.DATE,
								CallLog.Calls.DURATION,
								CallLog.Calls.CACHED_NAME
						};
		long time1 = new Date().getTime();
		/* First, going through call logs. */
		Cursor managedCursor = contentResolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, null);
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
		int cachedName = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

		while(managedCursor.moveToNext()) {
			Log.d("managedCursor", "cursor length is " + managedCursor.getCount());
			String phoneNumber = managedCursor.getString(number);
			String canonicalPhoneNumber = Util.getCanonicalPhoneNumber(phoneNumber);
			String callDate = managedCursor.getString(date);
			int callDuration = managedCursor.getInt(duration);
			String cachedNameStr = managedCursor.getString(cachedName);
			
			Contact contact;
			if (!contactDetails.containsKey(canonicalPhoneNumber)) {
				contact = new Contact(phoneNumber);
				contact.setName(cachedNameStr);
				contactDetails.put(canonicalPhoneNumber, contact);
				contacts.add(contact);
			} else {
				contact = contactDetails.get(canonicalPhoneNumber);
			}
			
			if (callDuration > 0) {
				contact.numCalls += 1;
				contact.lastActualCallTime = contact.lastActualCallTime > Long.parseLong(callDate) ? 
										 contact.lastActualCallTime : 
										 Long.parseLong(callDate);
				contact.firstActualCallTime = contact.firstActualCallTime < Long.parseLong(callDate) ?
										contact.firstActualCallTime :
										Long.parseLong(callDate);
			}
			contact.lastCallTime = contact.lastCallTime > Long.parseLong(callDate) ? 
					 contact.lastCallTime : 
					 Long.parseLong(callDate);
			contact.firstCallTime = contact.firstCallTime < Long.parseLong(callDate) ?
					contact.firstCallTime :
					Long.parseLong(callDate);

		}
		managedCursor.close();
		
		long time2 = new Date().getTime();
		Log.v("VIVEK", "T1 = " + (time2 - time1));
		/* First, sort by numCall */
		Collections.sort(contacts, new Comparator<Contact>(){
			@Override
			public int compare(Contact contact1, Contact contact2) {
				if (contact2.numCalls > contact1.numCalls) {
					return 1;
				} else if (contact2.numCalls < contact1.numCalls){
					return -1;
				} else {
					return 0;
				}
			}
		});

		/* Now assign the ranks */
		for (int i = 0; i < contacts.size(); i++) {
			contacts.get(i).numCallRank = i;
		}

		long time3 = new Date().getTime();
		Log.v("VIVEK", "T2 = " + (time3 - time2));
	
		/* Now, sort by lastCall - firstCall difference */
		Collections.sort(contacts, new Comparator<Contact>(){
			@Override
			public int compare(Contact contact1, Contact contact2) {
				long diff1 = contact1.lastCallTime - contact1.firstCallTime;
				long diff2 = contact2.lastCallTime - contact2.firstCallTime;
				if (diff2 > diff1) {
					return 1;
				} else if (diff2 < diff1){
					return -1;
				} else {
					return 0;
				}
			}
		});
		
		/* Now assign the ranks as per sustainability*/
		for (int i = 0; i < contacts.size(); i++) {
			contacts.get(i).sustainabilityRank = i;
		}
		
		/* Finally, the score is numCallRank + sustainabilityRank. */
		Collections.sort(contacts, new Comparator<Contact>(){
			@Override
			public int compare(Contact contact1, Contact contact2) {
				int rank1 = contact1.numCallRank + contact1.sustainabilityRank;
				int rank2 = contact2.numCallRank + contact2.sustainabilityRank;
				
				if (rank1 > rank2) {
					return 1;
				} else if (rank1 < rank2){
					return -1;
				} else {
					return 0;
				}
			}
		});

		long time4 = new Date().getTime();
		Log.v("VIVEK", "T3 = " + (time4 - time3));

		String[] projections = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_URI};

		String whereClause = Phone.DISPLAY_NAME + " = ? OR " + 
								Phone.DISPLAY_NAME + " = ? OR " +
								Phone.DISPLAY_NAME + " = ? OR " +
								Phone.DISPLAY_NAME + " = ? OR " +
								Phone.DISPLAY_NAME + " = ? OR " +
								Phone.DISPLAY_NAME + " = ? OR " +
								Phone.DISPLAY_NAME + " = ? ";

		/* CallLog.Calls.CACHED_NAME can be null too, so we need to take care
		 * of that. However, even if the name is null, it won't matter much
		 * because async db call will make data structures alright, and then
		 * a refresh of the list happens.
		 */
		String[] names = new String[7];
		for (int i = 0; i < 7; i++) {
			if (i < contacts.size() && contacts.get(i).getName() != "") {
				names[i] = contacts.get(i).getName();
			} else {
				names[i] = "-";
			}
		}
		String whereClauseArgs[] = new String[] {
			names[0] == null ? "-" : names[0],
			names[1] == null ? "-" : names[1],
			names[2] == null ? "-" : names[2],
			names[3] == null ? "-" : names[3],
			names[4] == null ? "-" : names[4],
			names[5] == null ? "-" : names[5],
			names[6] == null ? "-" : names[6]
		};
		Cursor phones = mContext.getContentResolver()
				.query(Phone.CONTENT_URI, 
						projections, 
						whereClause,
						whereClauseArgs,
						null);
		int nameIndex = phones.getColumnIndex(Phone.DISPLAY_NAME);
		int phoneIndex = phones.getColumnIndex(Phone.NUMBER);
		int photoIndex = phones.getColumnIndex(Phone.PHOTO_URI);

		long time5 = new Date().getTime();
		Log.v("VIVEK", "T4 = " + (time5 - time4));

		while (phones.moveToNext())	{
			String name = phones.getString(nameIndex);
			String phoneNumber = phones.getString(phoneIndex);
			String canonicalPhoneNumber = Util.getCanonicalPhoneNumber(phoneNumber);

			Contact contact;
			if (contactDetails.containsKey(canonicalPhoneNumber)) {
				contact = contactDetails.get(canonicalPhoneNumber);
				contact.setName(name);
			} else {
				contact = new Contact(name, phoneNumber);
				contacts.add(contact);
			}
			String photoURI = phones.getString(photoIndex);
			
			Contact contact1 = new Contact(name, phoneNumber);
			contact1.setPhotoURI(photoURI);

			contact.setPhotoURI(photoURI);
			contactDetails.put(canonicalPhoneNumber, contact);
		}
		phones.close();
		long time6 = new Date().getTime();
		Log.v("VIVEK", "T5 = " + (time6 - time5));

		/* Test putting data to server. */
		/*try {
			URL url = new URL("http://ec2-174-129-130-200.compute-1.amazonaws.com:9000/upload");
			new UploadTask().execute(url);
		} catch (Exception e) {
			Log.v("VIVEK", "Some exception happened dear!");
		}*/
	}
	public void setAllContactArray(ArrayList<Contact> all_contacts) {
		this.all_contacts = all_contacts;
	}
}
