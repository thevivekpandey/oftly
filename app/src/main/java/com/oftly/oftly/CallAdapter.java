package com.oftly.oftly;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CallAdapter extends ArrayAdapter<Call> {
	ArrayList<Call>calls;
	MainAdapter mainAdapter;
	Context context;
	private LayoutInflater inflater;
	ContentResolver contentResolver;
    Typeface typeface, boldTypeface;
    ImageFetcher imageFetcher;

	public CallAdapter(Context context, int resourceId, ArrayList<Call>calls, 
			MainAdapter mainAdapter, ContentResolver contentResolver, ImageFetcher imageFetcher) {
		super(context, resourceId, calls);
		this.context = context;
		this.calls = calls;
		this.mainAdapter = mainAdapter;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.contentResolver = contentResolver;
		typeface = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Light.ttf");
		boldTypeface = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Regular.ttf");
		this.imageFetcher = imageFetcher;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CallViewHolder nHolder;
		if (convertView == null) {
			nHolder = new CallViewHolder();

			convertView = inflater.inflate(R.layout.call, parent, false);
			nHolder.photoView = (TextView)convertView.findViewById(R.id.photo);
			
			nHolder.textView = (TextView)convertView.findViewById(R.id.phone_number_or_name);
			nHolder.textView.setTypeface(boldTypeface);

			nHolder.timeView = (TextView)convertView.findViewById(R.id.time);
			nHolder.timeView.setTypeface(typeface);

			nHolder.callTypeView = (ImageView)convertView.findViewById(R.id.call_type_icon);

			nHolder.durationView = (TextView)convertView.findViewById(R.id.duration);
			nHolder.durationView.setTypeface(typeface);

			nHolder.callView = (ImageView)convertView.findViewById(R.id.call_call_button);
			nHolder.position = position;
			convertView.setTag(nHolder);
		} else {
			nHolder = (CallViewHolder)convertView.getTag();
		}

		Call call = calls.get(position);
		String phoneNumber = call.phoneNumber;
		String canonicalPhoneNumber = Util.getCanonicalPhoneNumber(phoneNumber);
		HashMap<String, Contact> map = mainAdapter.contactDetails;
		
		/* Image */
		String photoURI;
		if (map.containsKey(canonicalPhoneNumber)) {
			photoURI = map.get(canonicalPhoneNumber).photoURI;
		} else {
			photoURI = null;
		}

		/* Name or phone number */
		String nameOrPhoneNumber;
		if (map.containsKey(canonicalPhoneNumber) && map.get(canonicalPhoneNumber).getName() != null) {
			nameOrPhoneNumber = map.get(Util.getCanonicalPhoneNumber(phoneNumber)).getName();
		} else {
			nameOrPhoneNumber = phoneNumber;
		}

		nHolder.textView.setText(nameOrPhoneNumber);
		nHolder.timeView.setText(Util.getReadableCallTime(call.date));
		nHolder.durationView.setText(Util.convertSecToString(call.duration));

		nHolder.callView.setTag(R.integer.call, call);
		Contact contact = mainAdapter.contactDetails.get(Util.getCanonicalPhoneNumber(call.phoneNumber));

		nHolder.callView.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Call call = (Call)view.getTag(R.integer.call);
				String tel = call.phoneNumber;
        		Intent intent = new Intent(Intent.ACTION_CALL);
        		intent.setData(Uri.parse("tel:" + Uri.encode(tel)));
        		context.startActivity(intent);
			}
		});
				
		String text = "INVALID";
		if (map.containsKey(canonicalPhoneNumber) &&
			map.get(canonicalPhoneNumber).getName() != null &&
			map.get(canonicalPhoneNumber).getName().length() > 0) {
				text = map.get(canonicalPhoneNumber).getName().subSequence(0, 1).toString();
		}
		nHolder.photoView.setTypeface(typeface);

		/*CallLogEntryImagePopulator c = new CallLogEntryImagePopulator(context, 
												nHolder, 
												photoURI, 
												position, 
												canonicalPhoneNumber, 
												text);
		c.execute();*/
		if (photoURI == null && !ImageLib.giftedImageExists(canonicalPhoneNumber)) {
			int color = ImageLib.getRandomColor(canonicalPhoneNumber);
			nHolder.photoView.setBackgroundDrawable(null);
			nHolder.photoView.setBackgroundColor(color);
			if (!text.equals("INVALID")) {
				nHolder.photoView.setTypeface(typeface);
				nHolder.photoView.setText(text);
			} else {
				nHolder.photoView.setText("");
			}
		} else {
			nHolder.photoView.setText("");
			imageFetcher.loadImage(photoURI + "-" + canonicalPhoneNumber, nHolder.photoView);
		}

		/* Stop gap arrangement till you figure out how to fix library in
		 * android 4.2
		 */
		
		/*Bitmap b = ImageLib.getPhotoBitmap(context, photoURI);
		if (b == null) {
			nHolder.photoView.setBackgroundColor(ImageLib.getRandomColor(contact.getPhone()));
			if (contact.getName() != null && contact.getName().length() > 0) {
				nHolder.photoView.setText(contact.getName().subSequence(0, 1));
			}
		} else {
			Drawable d = new BitmapDrawable(context.getResources(), b);
			nHolder.photoView.setBackgroundDrawable(d);
			nHolder.photoView.setText("");
		}*/

		nHolder.callTypeView.setImageResource(Util.getCallTypeImageResourceId(call.type));
		convertView.setTag(R.integer.contact, contact);
		convertView.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Contact contact = (Contact)(view.getTag(R.integer.contact));
				FragmentManager fragmentManager = ((Activity)context).getFragmentManager();
				
				FragmentTransaction transaction = fragmentManager.beginTransaction();
	    		ContactDetailFragment contactDetailFragment = (ContactDetailFragment)
	    							fragmentManager.findFragmentById(R.id.fragment_contact_detail);
	    		
	    		contactDetailFragment.refresh(contact);
				transaction.setCustomAnimations(R.animator.slide_left, R.animator.slide_right, 
											R.animator.slide_left, R.animator.slide_right);
				transaction.show(contactDetailFragment);
				transaction.commit();
				
				BackButton backButton = BackButton.getBackButtonInstance();
				backButton.addToStack("contactdetail");
			}
		});
		return convertView;
	}
	public void setAdapter(ArrayList<Call>calls, MainAdapter mainAdapter) {
		this.calls = calls;
		this.mainAdapter = mainAdapter;
	}
	@Override
	public int getCount() {
		return calls.size();
	}
}