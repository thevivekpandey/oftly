package com.oftly.oftly;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ContactBlockAdapter extends ArrayAdapter<RelativeLayout> implements SectionIndexer {
	Context context;
	SearchAdapter adapter;
	int screenWidth;
	int[] blockNum2BeginIdx;
	Typeface typeface, typefaceBold;
	LayoutInflater inflater;
	FragmentManager fragmentManager;
	int numBlocks;
	MainActivity mainActivity;
	ImageFetcher imageFetcher;
	
	int numItems;
	/* When full population of 7 is present */
	int numParts_7 = 7;
	int Xs_7[] = {3, 3, 3, 2, 2, 2, 2, 3, 3, 3, 2, 2, 2, 2};
	int Ys_7[] = {2, 2, 2, 3, 3, 3, 3, 2, 2, 2, 3, 3, 3, 3};
	int marginXs_7[] = {2, 2, 2, 0, 0, 5, 5, 2, 2, 2, 0, 0, 5, 5};
	int marginYs_7[] = {    0,     2,     4,     0,     3,     0,     3,
			        6 + 0, 6 + 2, 6 + 4, 6 + 0, 6 + 3, 6 + 0, 6 + 3};
	
	/* When population size is 1 */
	int numParts_1 = 7;
	int Xs_1[] = {3};
	int Ys_1[] = {2};
	int marginXs_1[] = {2};
	int marginYs_1[] = {0};

	/* When population size is 2 */
	int numParts_2 = 8;
	int Xs_2[] = {4, 4};
	int Ys_2[] = {3, 3};
	int marginXs_2[] = {0, 4};
	int marginYs_2[] = {0, 0};

	/* When population size is 3 */
	int numParts_3 = 6;
	int Xs_3[] = {2, 2, 2};
	int Ys_3[] = {3, 3, 3};
	int marginXs_3[] = {0, 2, 4};
	int marginYs_3[] = {0, 0, 0};
	
	/* When population is size is 4 */
	int numParts_4 = 8;
	int Xs_4[] = {4, 4, 4, 4};
	int Ys_4[] = {4, 4, 4, 4};
	int marginXs_4[] = {0, 4, 0, 4};
	int marginYs_4[] = {0, 0, 4, 4};

	/* When population is size is 5 */
	int numParts_5 = 6;
	int Xs_5[] = {3, 3, 2, 2, 2};
	int Ys_5[] = {3, 3, 3, 3, 3};
	int marginXs_5[] = {0, 3, 0, 2, 4};
	int marginYs_5[] = {0, 0, 3, 3, 3};
	
	/* When population is size is 6 */
	int numParts_6 = 7;
	int Xs_6[] = {2, 3, 2, 2, 3, 2};
	int Ys_6[] = {3, 3, 3, 3, 3, 3};
	int marginXs_6[] = {0, 2, 5, 0, 2, 5};
	int marginYs_6[] = {0, 0, 0, 3, 3, 3};

	private String sections ;
	public ContactBlockAdapter(Context context, int resourceId, 
					int screenWidth, int numBlocks,
					SearchAdapter adapter, FragmentManager fragmentManager,
					int[] blockNum2BeginIdx, ImageFetcher imageFetcher) {
		
		super(context, resourceId);
		this.context = context;
		this.adapter = adapter;
		this.screenWidth = screenWidth;
		this.numBlocks = numBlocks;
		this.fragmentManager = fragmentManager;
		this.blockNum2BeginIdx = blockNum2BeginIdx;
		this.imageFetcher = imageFetcher;
		typeface = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Light.ttf");
		typefaceBold = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Regular.ttf");
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int ot = context.getResources().getConfiguration().orientation;
		if (ot == Configuration.ORIENTATION_LANDSCAPE) {
			this.sections = "ACEGIKMOQSUWY.";
		} else {
			this.sections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ.";
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return 0;
		}
		return 1;
	}
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		Util.Log("Called with pos = " + pos);
		int ot = context.getResources().getConfiguration().orientation;
		if (ot == Configuration.ORIENTATION_LANDSCAPE) {
			pos++;
		}
		if (pos == 0) {
			return getTiledView(pos, convertView, parent);
		}
		return getListItem(pos, convertView, parent);
	}
	
	public View getListItem(int position, View convertView, ViewGroup parent) {
		RelativeLayout relativeLayout = (RelativeLayout)inflater.inflate(R.layout.simple_contact, null);
		TextView photoView = (TextView)relativeLayout.findViewById(R.id.photo);

		Contact contact = adapter.all_contacts.get(position - 1);
		
		/* Image */
		/*String photoURI = contact.getPhotoURI();
		Bitmap b = ImageLib.getPhotoBitmap(context, photoURI);
		Drawable d;
		photoView.setTypeface(typeface);
		if (b == null) {
			photoView.setBackgroundColor(ImageLib.getRandomColor(contact.getPhone()));
			if (contact.getName() != null && contact.getName().length() > 0) {
				photoView.setText(contact.getName().subSequence(0, 1));
			}
		} else {
			d = new BitmapDrawable(context.getResources(), b);
			photoView.setBackgroundDrawable(d);
			photoView.setText("");
		}*/
		
		String photoURI = contact.getPhotoURI();
		String text = "INVALID";
		if (contact.getName() != null && contact.getName().length() > 0) {
			text = contact.getName().subSequence(0, 1).toString();
		}
		String canonicalPhoneNumber = Util.getCanonicalPhoneNumber(contact.getPhone());
		if (photoURI == null && !ImageLib.giftedImageExists(canonicalPhoneNumber)) {
			int color = ImageLib.getRandomColor(contact.getPhone());
			photoView.setBackgroundDrawable(null);
			photoView.setBackgroundColor(color);
			if (!text.equals("INVALID")) {
				photoView.setTypeface(typeface);
				photoView.setText(text);
			} else {
				photoView.setText("");
			}

		} else {
			photoView.setText("");
			imageFetcher.loadImage(photoURI + "-" + canonicalPhoneNumber, photoView);
		}

		/* Name or else phone number */
		String nameOrPhoneNumber = contact.getName() != null ? contact.getName() : contact.getPhone();
		TextView nameOrPhoneNumberView = (TextView)relativeLayout.findViewById(R.id.name_or_phone_number);
		nameOrPhoneNumberView.setTypeface(typefaceBold);
		nameOrPhoneNumberView.setText(nameOrPhoneNumber);
		
		/* phone number if there was name too */
		String phoneNumberIfNamePresent = contact.getName() != null ? contact.getPhone() : "";
		TextView phoneNumberIfNamePresentView = (TextView)relativeLayout.findViewById(R.id.phone_number_if_name_present);
		phoneNumberIfNamePresentView.setTypeface(typeface);
		phoneNumberIfNamePresentView.setText(phoneNumberIfNamePresent);
		
		/* Make a call */
		ImageView callButtonView = (ImageView)relativeLayout.findViewById(R.id.call_button);
		callButtonView.setTag(R.integer.call, contact);
		callButtonView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Contact contact = (Contact)view.getTag(R.integer.call);
				String tel = contact.phone;
        		Intent intent = new Intent(Intent.ACTION_CALL);
        		intent.setData(Uri.parse("tel:" + Uri.encode(tel)));
        		context.startActivity(intent);
			}
		});

		relativeLayout.setTag(R.integer.contact, contact);
		relativeLayout.setOnClickListener(new OnClickListener() {
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
				BackButton.getBackButtonInstance(fragmentManager).addToStack("contactdetail");
			}
		});
		return relativeLayout;
	}
	
	public View getTiledView(int pos, View convertView, ViewGroup parent) {
		int ot = context.getResources().getConfiguration().orientation;
		if (ot == Configuration.ORIENTATION_LANDSCAPE) {
			numItems = adapter.contacts.size() > 4 ? 4 : adapter.contacts.size();
		}else {
			numItems = adapter.contacts.size() > 7 ? 7 : adapter.contacts.size();
		}
		RelativeLayout myView = new RelativeLayout(context);
		for (int i = 0; i < numItems; i++) {
			myView.addView(getTile(i), getPosition(i));
		}
		return myView;
	}
	
	private RelativeLayout getTile(int i) {
		RelativeLayout relativeLayout = (RelativeLayout)inflater.inflate(R.layout.contact_new, null);
		relativeLayout.setGravity(Gravity.CENTER_HORIZONTAL);		

		TextView photoView = (TextView)relativeLayout.findViewById(R.id.contact_photo);
		photoView.setTypeface(typeface);
		TextView textView = (TextView)relativeLayout.findViewById(R.id.contact_name);
		textView.setTypeface(typefaceBold);
		relativeLayout.setBackgroundResource(R.drawable.border);

		Contact contact = adapter.contacts.get(blockNum2BeginIdx[0] + i);
		Bitmap b = ImageLib.getPhotoBitmap(context, contact.photoURI, Util.getCanonicalPhoneNumber(contact.getPhone()));
		Drawable d;
		if (b == null) {
			photoView.setBackgroundColor(ImageLib.getRandomColor(contact.getPhone()));
			if (contact.getName() != null &&
				contact.getName().length() > 0) {
				photoView.setText(contact.getName().subSequence(0, 1));
			}
		} else {
			d = new BitmapDrawable(context.getResources(), b);
			photoView.setBackgroundDrawable(d);
			photoView.setText("");
		}
		if (contact.getName() != null) {
			textView.setText(contact.getName());
		} else {
			textView.setText(contact.getPhone());
		}

		relativeLayout.setTag(R.integer.call, contact);
		relativeLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				/* Make a phone call when photo is clicked */
				Contact contact = (Contact)view.getTag(R.integer.call);
				String tel = contact.phone;
        		Intent intent = new Intent(Intent.ACTION_CALL);
        		intent.setData(Uri.parse("tel:" + Uri.encode(tel)));
        		context.startActivity(intent); /* Encountered ActivityNotFoundException here */
			}
		});
        relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
            	Contact contact = (Contact)view.getTag(R.integer.call);
            	
				FragmentManager fragmentManager = ((Activity)context).getFragmentManager();
				
				FragmentTransaction transaction = fragmentManager.beginTransaction();
	    		ContactDetailFragment contactDetailFragment = (ContactDetailFragment)
	    							fragmentManager.findFragmentById(R.id.fragment_contact_detail);
	    		
	    		contactDetailFragment.refresh(contact);
				transaction.setCustomAnimations(R.animator.slide_left, R.animator.slide_right, 
											R.animator.slide_left, R.animator.slide_right);
				transaction.show(contactDetailFragment);
				transaction.commit();
				
				BackButton.getBackButtonInstance(fragmentManager).addToStack("contactdetail");
            	return true;
            }
        });

        relativeLayout.setPadding(5, 5, 5, 5);
		return relativeLayout;
	}
	private RelativeLayout.LayoutParams getPosition(int i) {
		RelativeLayout.LayoutParams rlp = null;
		int offsetY;
		if (i >= 7) {
			offsetY = screenWidth * 6 /7 ;
		} else {
			offsetY = 0;
		}
		if ((numItems == 7)) {
			rlp = new RelativeLayout.LayoutParams(getIntDim(i, Xs_7[i]), getIntDim(i, Ys_7[i]));
			rlp.setMargins(getIntDim(i, marginXs_7[i]), getIntDim(i, marginYs_7[i]), 0, 0);
		}
		if (numItems == 1) {
			rlp = new RelativeLayout.LayoutParams(getIntDim(i, Xs_1[i % 7]), getIntDim(i, Ys_1[i % 7]));
			rlp.setMargins(getIntDim(i, marginXs_1[i % 7]), getIntDim(i, marginYs_1[i % 7]) + offsetY, 0, 0);			
		}
		if (numItems == 2) {
			rlp = new RelativeLayout.LayoutParams(getIntDim(i, Xs_2[i % 7]), getIntDim(i, Ys_2[i % 7]));
			rlp.setMargins(getIntDim(i, marginXs_2[i % 7]), getIntDim(i, marginYs_2[i % 7]) + offsetY, 0, 0);						
		}
		if (numItems == 3) {
			rlp = new RelativeLayout.LayoutParams(getIntDim(i, Xs_3[i % 7]), getIntDim(i, Ys_3[i % 7]));
			rlp.setMargins(getIntDim(i, marginXs_3[i % 7]), getIntDim(i, marginYs_3[i % 7]) + offsetY, 0, 0);
		}
		if (numItems == 4) {
			rlp = new RelativeLayout.LayoutParams(getIntDim(i, Xs_4[i % 8]), getIntDim(i, Ys_4[i % 8]));
			rlp.setMargins(getIntDim(i, marginXs_4[i % 8]), getIntDim(i, marginYs_4[i % 8]) + offsetY, 0, 0);
		}
		if (numItems == 5) {
			rlp = new RelativeLayout.LayoutParams(getIntDim(i, Xs_5[i % 5]), getIntDim(i, Ys_5[i % 5]));
			rlp.setMargins(getIntDim(i, marginXs_5[i % 5]), getIntDim(i, marginYs_5[i % 5]) + offsetY, 0, 0);
		}
		if (numItems == 6) {
			rlp = new RelativeLayout.LayoutParams(getIntDim(i, Xs_6[i % 7]), getIntDim(i, Ys_6[i % 7]));
			rlp.setMargins(getIntDim(i, marginXs_6[i % 7]), getIntDim(i, marginYs_6[i % 7]) + offsetY, 0, 0);
		}
		return rlp;
	}

	private int getIntDim(int i, int length) {
		int numParts = 0;
		if ((numItems >= 7 && i < 7) || numItems == 14) {
			numParts = numParts_7;
		}
		if (numItems == 1 || (numItems == 8 && i >= 7)) {
			numParts = numParts_1;
		}
		if (numItems == 2 || (numItems == 9 && i >= 7)) {
			numParts = numParts_2;
		}
		if (numItems == 3 || (numItems == 10 && i >= 7)) {
			numParts = numParts_3;
		}
		if (numItems == 4 || (numItems == 11 && i >= 7)) {
			numParts = numParts_4;
		}
		if (numItems == 5 || (numItems == 12 && i >= 7)) {
			numParts = numParts_5;
		}
		if (numItems == 6 || (numItems == 13 && i >= 7)) {
			numParts = numParts_6;
		}
		return (int) (Math.ceil((double)(screenWidth) / (double)numParts))  * length;
	}
	public void setAdapter(SearchAdapter adapter, int numBlocks, int[] blockNum2BeginIdx ) {
		this.adapter = adapter;
		this.numBlocks = numBlocks;
		this.blockNum2BeginIdx = blockNum2BeginIdx;
	}
	@Override
	public int getCount() {
		Log.d("getCount ", String.valueOf(adapter.all_contacts.size()));
		int ot = context.getResources().getConfiguration().orientation;
		if (ot == Configuration.ORIENTATION_LANDSCAPE) {
			return adapter.all_contacts.size();
		}
		return adapter.all_contacts.size() + 1;
	}

	@Override
	public Object[] getSections() {
		String[] sectionsArr = new String[sections.length()];
		for (int i = 0; i < sections.length(); i++) {
			sectionsArr[i] = "" + sections.charAt(i);
		}	
		return sectionsArr;		   
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		if (sectionIndex == 0) {
			return 0;
		}
		if (sectionIndex >= sections.length()) {
			sectionIndex = sections.length() - 1;
		}
		int count = adapter.all_contacts.size();
		for (int i = 0; i < count; i++) {
			String name = adapter.all_contacts.get(i).getName();
			//if (name.toUpperCase().charAt(0) >= sections.charAt(sectionIndex)) {
			if (compare(name.charAt(0), sections.charAt(sectionIndex))) {
				int r =  i + 1 < count - 1 ? i + 1 : count - 1;
				return r;
			}
		}	
		return count - 1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
	private boolean compare(char nameChar, char sectionChar) {
		char uNameChar = Character.toUpperCase(nameChar);
		char uSectionChar = Character.toUpperCase(sectionChar);
		if (isAlphabetic(uSectionChar)) {
			if (uNameChar >= uSectionChar) {
				return true;
			} else {
				return false;
			}
		} else {
			if (!isAlphabetic(uNameChar)) {
				return true;
			} else {
				return false;
			}
		}
	}
	private boolean isAlphabetic(char c) {
		if (c >= 'A' && c <= 'Z') {
			return true;
		}
		if (c >= 'a' && c <= 'z') {
			return true;
		}
		return false;
	}
	
}