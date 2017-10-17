package com.oftly.oftly;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;
import android.provider.ContactsContract.PhoneLookup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ContactDetailFragment extends Fragment {
	Contact contact;

	/*public ContactDetailFragment(Contact contact) {
		this.contact = contact;
	}*/

	public ContactDetailFragment() {
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

		Contact contact = new Contact("111");
		ContactDetailsLayout contactDetailsLayout = 
				(ContactDetailsLayout)inflater.inflate(R.layout.fragment_contact_detail, container, false);
        ContactDetailCallLogAdapter adapter = new ContactDetailCallLogAdapter(getActivity(), 
        														contact, 
        														Util.getCanonicalPhoneNumber(contact.phone));
        ListView listView = (ListView)contactDetailsLayout.findViewById(R.id.contact_detail_call_log);
        listView.setAdapter(adapter);

		setUI(contactDetailsLayout, contact);
        return contactDetailsLayout;
	}
	public void refresh(Contact contact) {
		this.contact = contact;
		ContactDetailsLayout contactDetailsLayout = 
				(ContactDetailsLayout)getActivity().findViewById(R.id.fragment_contact_detail);
		
        ContactDetailCallLogAdapter adapter = new ContactDetailCallLogAdapter(getActivity(), 
				contact, 
				Util.getCanonicalPhoneNumber(contact.phone)); //This line is throwing NullPointerException
        ListView listView = (ListView)contactDetailsLayout.findViewById(R.id.contact_detail_call_log);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        setUI(contactDetailsLayout, contact);
	}
	public void setUI(ContactDetailsLayout contactDetailsLayout, Contact contact) {
		
        LinearLayout relativeLayout = (LinearLayout)contactDetailsLayout.findViewById(R.id.contact_detail_layout);
		relativeLayout.setTag(R.integer.contact, contact);

		Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "RobotoCondensed-Light.ttf");
		Typeface boldTypeface = Typeface.createFromAsset(getActivity().getAssets(), "RobotoCondensed-Regular.ttf");

		TextView photoView = (TextView)relativeLayout.findViewById(R.id.contact_detail_photo);
		/*photoView.setBackgroundDrawable(ImageLib.getContactDrawable(getActivity(), contact.photoURI, contact.getPhone()));
		if (contact.getPhotoURI() == null && contact.getName() != null && 
				contact.getName().length() > 0) {
			photoView.setTypeface(typeface);
			photoView.setText(contact.getName().subSequence(0, 1));
		} else {
			photoView.setText("");
		}*/
		
		Bitmap b = ImageLib.getPhotoBitmap(getActivity(), contact.photoURI, Util.getCanonicalPhoneNumber(contact.getPhone()));
		Drawable d;
		if (b == null) {
			photoView.setBackgroundColor(ImageLib.getRandomColor(contact.getPhone()));
			if (contact.getName() != null &&
				contact.getName().length() > 0) {
				photoView.setText(contact.getName().subSequence(0, 1));
			}
		} else {
			d = new BitmapDrawable(getActivity().getResources(), b);
			photoView.setBackgroundDrawable(d);
			photoView.setText("");
		}		
		
		photoView.setTag(R.integer.call, contact);
		photoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = ((Contact)v.getTag(R.integer.call)).getPhone();
				Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
				Cursor cursor = getActivity().getContentResolver().query(
							lookupUri, new String[]{PhoneLookup._ID}, null, null, null);
				long id = 0;
				try {
					if (cursor != null) {
						if (cursor.moveToFirst()) {
							id = Long.valueOf(cursor.getString(cursor.getColumnIndex(PhoneLookup._ID)));
						}
					}
				} finally {
					cursor.close();
				}
				Intent intent;
				if (id > 0) {
					intent = new Intent(Intent.ACTION_EDIT);
					intent.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id));
				} else {
			    	intent = new Intent(Intents.Insert.ACTION);
			    	intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
			    	intent.putExtra(Intents.Insert.PHONE, phone);
				}
				startActivity(intent);
			}
		});
		/* Name */
		TextView nameTextView = (TextView)relativeLayout.findViewById(R.id.contact_detail_name);
		nameTextView.setTypeface(boldTypeface);
		nameTextView.setText(contact.getName());

		/* Phone number */
		TextView phoneTextView = (TextView)relativeLayout.findViewById(R.id.contact_detail_phone);
		if (contact.getName() == null || contact.getName().length() == 0) {
			phoneTextView.setTypeface(boldTypeface);
		} else {
			phoneTextView.setTypeface(typeface);
		}
		phoneTextView.setText(contact.getPhone());

		/* Dial button */
		ImageView callButtonImageView = (ImageView)relativeLayout.findViewById(R.id.contact_detail_call_button);
		callButtonImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Contact contact = (Contact)((View)view.getParent().getParent()).getTag(R.integer.contact);
				String tel = contact.phone;
        		Intent intent = new Intent(Intent.ACTION_CALL);
        		intent.setData(Uri.parse("tel:" + Uri.encode(tel)));
        		getActivity().startActivity(intent);
			}
		});

		/* SMS button */
		ImageView smsButtonImageView = (ImageView)relativeLayout.findViewById(R.id.contact_detail_sms_button);
		smsButtonImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Contact contact = (Contact)((View)view.getParent().getParent()).getTag(R.integer.contact);
				String tel = contact.phone;
				Uri smsUri = Uri.parse("sms:" + tel);
        		//Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
        		//Intent intent = new Intent(Intent.ACTION_VIEW);
        		//intent.setType("vnd.android-dir/mms-sms");
        		//intent.putExtra("address", tel);
				String uri = "smsto:" + tel;
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
				intent.putExtra("compose_mode", true);
        		getActivity().startActivity(intent);
			}
		});
	}
    @Override
    public void onResume() {
        super.onResume();
        if (contact == null) {
        	return;
        }
		String phone = contact.getPhone();
		Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
		Cursor cursor = getActivity().getContentResolver().query(
					lookupUri, new String[]{PhoneLookup._ID, PhoneLookup.PHOTO_URI, PhoneLookup.DISPLAY_NAME}, null, null, null);
		long id = 0;
		try {
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					id  = Long.valueOf(cursor.getString(cursor.getColumnIndex(PhoneLookup._ID)));
					String photoURI = String.valueOf(cursor.getString(cursor.getColumnIndex(PhoneLookup.PHOTO_URI)));
					String displayName = String.valueOf(cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME)));
					if (photoURI != null) {
						contact.setPhotoURI(photoURI);
					}
					contact.setName(displayName);
				}
			}
		} finally {
			cursor.close();
		}

        refresh(contact);
    }

}