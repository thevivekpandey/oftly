package com.oftly.oftly;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Intents;
import android.provider.ContactsContract.PhoneLookup;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.oftly.oftly.asynctasks.UploadRegIdPhoneMapping;

import java.util.ArrayList;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{
	int constantSize;
	OnQueryTextListener onQueryTextListener;
	SearchView searchView;
	String title;
	Fragment fragment;
	DialpadFragment dialpadFragment;
	CallLogFragment callLogFragment;
	Fragment contactDetailFragment;
	ContactBlockFragment contactBlockFragment;
	TransparentFragment transparentFragment;
	MediaPlayer mp = null;
	Context context;
	String oldText = "";
	Typeface typeface;
	boolean dialPadVisible = false;
	boolean callLogVisible = false;
	String regId;
	private ImageFetcher imageFetcher;
	private static final String IMAGE_CACHE_DIR = "contactblock";
	
	boolean showSharedWithMePhotos = false;
	
	public final static String CONTACT = "com.example.countryfactsandquiz.CONTINENT_NAME";
	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";
	public static final String PHONE_NUMBER = "phoneNumber";
	
	final OnQueryTextListener searchQueryListener = new OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }
        @Override
        public boolean onQueryTextChange(String newText) {
    		FragmentManager fragmentManager = getFragmentManager();
        	ContactBlockFragment cbf = (ContactBlockFragment)fragmentManager.findFragmentById(R.id.fragment_contacts);
        	cbf.changeAdapter(newText);
        	
        	CallLogFragment clf = (CallLogFragment)fragmentManager.findFragmentById(R.id.fragment_call_log);
        	clf.changeAdapter(newText);
        	
        	return true;
        }
    };

    public void setOnQueryTextListener(OnQueryTextListener onQueryTextListener) {
		this.onQueryTextListener = onQueryTextListener;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main_actions, menu);
	    
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    searchView = (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setOnQueryTextListener(onQueryTextListener);
		searchView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

	    //inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.context = this;
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String str = intent.getStringExtra("abc");
		Util.Log("Str = " + str);
		Util.Log("onCreate MainActivity is called");

		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			Util.Log("savedInstanceState is null");
			showMainScreen();			
		} else {
			String src = bundle.getString("src");
			String[] targetPhones = bundle.getStringArray("target_phones");
			for (String targetPhone : targetPhones) {
				Util.Log("a target is " + targetPhone);
			}
			Util.Log("src = " + src);
			showMainScreen();
			showReceivedPhotosFragment();
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		Util.Log("In onResume()");
		getLoaderManager().initLoader(0, null, this);
		
		if (showSharedWithMePhotos == true) {
			showReceivedPhotosFragment();
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			showSharedWithMePhotos = true;
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.share_photos:
			showSharablePhotosFragment();
			return true;
			
		case R.id.received_photos:
			showReceivedPhotosFragment();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showSharablePhotosFragment() {
		if (!Util.isNetworkAvailable(context)) {
			Toast.makeText(getApplicationContext(), 
						"Photo sharing needs network connection", 
						Toast.LENGTH_LONG).show();
		} else {
			Util.Log("VIVEK", "Now I will show share photo screen");
			FragmentManager fragmentManager = getFragmentManager();
			Fragment f;
			
			f = fragmentManager.findFragmentById(R.id.fragment_share_screen);
			/*if (f == null) {
				f = new ShareScreenFragment(this, MainAdapter.getMainAdapterInstance(context));				
			}*/
			
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.add(R.id.outer, f, "TAG");
			transaction.show(f);
			transaction.addToBackStack("SharePhotos");
	    	transaction.commit();
		}		
	}
	private void showReceivedPhotosFragment() {
		if (!Util.isNetworkAvailable(context)) {
			Toast.makeText(getApplicationContext(), 
						"Photo sharing needs network connection", 
						Toast.LENGTH_LONG).show();
		} else {
			Util.Log("VIVEK", "Now I will show received photos");
			FragmentManager fragmentManager = getFragmentManager();	
			Fragment f;
			
			f = fragmentManager.findFragmentById(R.id.fragment_show_shared);
			/*if (f == null) {
				f = new ShowReceivedPhotosFragment(this, MainAdapter.getMainAdapterInstance(context));
			}*/
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.add(R.id.outer, f, "TAG");
			transaction.show(f);
			transaction.addToBackStack("ShowSharedPhotosWithMe");
	    	transaction.commit();
		}
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(GCMIntentService.NOTIFICATION_ID);
	}
	private void showMainScreen() {
		MainAdapter mainAdapter = MainAdapter.getMainAdapterInstance(this);
		Util.Log("show main screen all contacts size " + mainAdapter.all_contacts.size());

		setContentView(R.layout.activity_main);

		setOnQueryTextListener(searchQueryListener);
		navigationAndStatusBarSettings();

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction;
		
		contactBlockFragment = (ContactBlockFragment) fragmentManager.findFragmentById(R.id.fragment_contacts);
		contactBlockFragment.setAdapter(MainAdapter.getMainAdapterInstance(context));
		
		transaction = fragmentManager.beginTransaction();
		dialpadFragment = (DialpadFragment)fragmentManager.findFragmentById(R.id.fragment_dialpad);
		transaction.hide(dialpadFragment);
    	transaction.commit();

    	transaction = fragmentManager.beginTransaction();
    	transparentFragment = (TransparentFragment)fragmentManager.findFragmentById(R.id.fragment_transparent);
    	transaction.hide(transparentFragment);
    	transaction.commit();    	
    	
    	transaction = fragmentManager.beginTransaction();
		callLogFragment = (CallLogFragment)fragmentManager.findFragmentById(R.id.fragment_call_log);
		callLogFragment.setAdapter(MainAdapter.getMainAdapterInstance(context));
		transaction.hide(callLogFragment);
		transaction.commit();
		
		transaction = fragmentManager.beginTransaction();
		contactDetailFragment = fragmentManager.findFragmentById(R.id.fragment_contact_detail);
		transaction.hide(contactDetailFragment);
		transaction.commit();
		
		LinearLayout transparentLayer = (LinearLayout)findViewById(R.id.fragment_transparent);
		transparentLayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed("transparentLayer");
			}
		});

		getLoaderManager().initLoader(0, null, this);		
		
		manageAlarm();
		manageRegistration();

		int ot = context.getResources().getConfiguration().orientation;
		if (ot == Configuration.ORIENTATION_LANDSCAPE) {
			ViewGroup layout = (ViewGroup) findViewById(R.id.main_land_smartlist);
			Log.d("land mode", "layout width " + layout.getWidth());
			layout.addView(getSmartListLandView(layout));
		}
	}
	private View getSmartListLandView(ViewGroup layout){
		int numBlocks;
		//LandSmartListView landSmartListView;
		ContactBlockAdapter contactBlockAdapter;
		DisplayMetrics displayMetrics;
		MainAdapter mainAdapter = MainAdapter.getMainAdapterInstance(context);
		int[] blockNumber2contactBeginIndex;
		SearchAdapter adapter = new SearchAdapter(mainAdapter.all_contacts);
		for (Contact contact: mainAdapter.contacts) {
			adapter.addContactToContacts(contact);
		}
		ImageCache.ImageCacheParams cacheParams =
				new ImageCache.ImageCacheParams(context, IMAGE_CACHE_DIR);

		cacheParams.setMemCacheSizePercent(0.125f); // Set memory cache to 12.5% of app memory
		imageFetcher = new ImageFetcher(context, 300, 300);
		imageFetcher.setLoadingImage(R.drawable.empty_photo);
		imageFetcher.addImageCache(getFragmentManager(), cacheParams);

		numBlocks = Util.getNumBlocks(mainAdapter.contacts.size());
		blockNumber2contactBeginIndex = new int[numBlocks];
		ContactBlockGenerator cbm;
		for (int i = 1; i < numBlocks; i++) {
			cbm = new ContactBlockGenerator(-1, 2 * (i - 1), adapter, blockNumber2contactBeginIndex[i - 1]);
			blockNumber2contactBeginIndex[i] = blockNumber2contactBeginIndex[i - 1] + cbm.numItems;
		}

		displayMetrics = new DisplayMetrics();
		//layout.getDisplay().getMetrics(displayMetrics);
		Log.d("width", "layout width " + displayMetrics.widthPixels);
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		contactBlockAdapter = new ContactBlockAdapter(context, R.layout.contact_block,
				(displayMetrics.widthPixels * 3 / 8), numBlocks, adapter,
				getFragmentManager(), blockNumber2contactBeginIndex, imageFetcher);
		return contactBlockAdapter.getTiledView(0,null,null);
	}
	private void manageAlarm() {
		final int PHOTO_UPLOAD_INTERVAL = 24 * 60 * 60 * 1000;
		final int CONTACT_LIST_UPLOAD_INTERVAL = 24 * 60 * 60 * 1000;

		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent alarmIntent2 = new Intent(context, ContactListUploader.class);
		PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, alarmIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.setInexactRepeating(AlarmManager.RTC, CONTACT_LIST_UPLOAD_INTERVAL, CONTACT_LIST_UPLOAD_INTERVAL, pendingIntent2);	
	}
	private void manageRegistration() {
		registerGCMInBackground();
		String reg = getRegistrationId(context);
		Util.Log("VIVEK", "Checking soon after start, reg id is " + reg);
		ifNeededSendMappingToServer(reg);
	}
	private void ifNeededSendMappingToServer(String reg) {
		if (!Util.getStoredValue(context, Util.MAPPING_STORED).equals(Util.TRUE) &&
				!reg.equals(Util.INVALID)) {
			String phoneNumber = Util.getStoredValue(context, Util.PHONE_NUMBER);
			Util.Log("VIVEK", "I will upload " + phoneNumber + ", " + reg);
			new UploadRegIdPhoneMapping(context).execute(reg, phoneNumber);
		}
	}
	private void registerGCMInBackground() {
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(
				MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			Util.Log("VIVEK", "Registration not found");
			return Util.INVALID;
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Util.Log("VIVEK", "App version changed");
			return Util.INVALID;
		}
		return registrationId;
	}

	private int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Util.Log("VIVEK", "Never expected this");
			throw new RuntimeException(e);
		}
	}
	
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), 
					Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Util.Log("VIVEK", "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}

	public void onBackPressed(String buttonPressed) {
		BackButton backButton = BackButton.getBackButtonInstance();
    	FragmentManager fragmentManager = getFragmentManager();
		if (buttonPressed.equals("transparentLayer")) {
			String action = backButton.handleBackButton();
			if (action.equals("dialpad")) {
				dialpadFragment = (DialpadFragment)fragmentManager.findFragmentById(R.id.fragment_dialpad);
				dialpadFragment.toggleDialpadVisibility();
			}
		}
	}
	@Override
	public void onBackPressed() {
    	FragmentManager fragmentManager = getFragmentManager();
		BackButton backButton = BackButton.getBackButtonInstance(fragmentManager);
		String action = backButton.handleBackButton();
		if (action.equals("system")) {
			super.onBackPressed();
		}
		if (action.equals("dialpad")) {
	    	dialpadFragment = (DialpadFragment)fragmentManager.findFragmentById(R.id.fragment_dialpad);
	    	dialpadFragment.toggleDialpadVisibility();
		}
		if (action.equals("calllog")) {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, 
											R.animator.slide_up, R.animator.slide_down);
			ImageView iv = (ImageView)findViewById(R.id.toggle_calllog);
			
    		callLogFragment = (CallLogFragment) fragmentManager.findFragmentById(R.id.fragment_call_log);
    		transaction.hide(callLogFragment);
    		iv.setImageResource(R.drawable.calllog);
    		transaction.commit();
    		callLogVisible = false;
		}
		if (action.equals("contactdetail")) {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.setCustomAnimations(R.animator.slide_left, R.animator.slide_right, 
											R.animator.slide_left, R.animator.slide_right);
			contactDetailFragment = fragmentManager.findFragmentById(R.id.fragment_contact_detail);
			transaction.hide(contactDetailFragment);
			transaction.commit();
		}
	}
    public void dialPadButtonPressed(View view) {
    	FragmentManager fragmentManager = getFragmentManager();
    	BackButton backButton = BackButton.getBackButtonInstance(fragmentManager);
    	dialpadFragment = (DialpadFragment)fragmentManager.findFragmentById(R.id.fragment_dialpad);
    	if (dialpadFragment.getDialpadVisibility() == false) {
    		backButton.addToStack("dialpad");
    	} else {
    		backButton.removeFromStack("dialpad");
    	}
    	dialpadFragment.toggleDialpadVisibility();
    }
    public void dialpadButtonPressed(View view) {
    	String cs = (String)view.getTag();
    	String newDigit = cs.toString();
    	
    	TextView dialpadNumberView = (TextView)findViewById(R.id.dialpad_number);
    	String oldDisplayedNumber = dialpadNumberView.getText().toString().replace(" " , "");
    	String newDisplayedNumber = oldDisplayedNumber + newDigit;
 	/////////////////////
    	dialpadNumberView.setText(Util.getDisplayableNumber(newDisplayedNumber));
    	view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		FragmentManager fragmentManager = getFragmentManager();
		ContactBlockFragment cbf = (ContactBlockFragment)fragmentManager.findFragmentById(R.id.fragment_contacts);
		cbf.changeAdapterByDialer(newDisplayedNumber);
    }
    public void dialPadCutLastDigit(View view) {
    	TextView dialpadNumberView = (TextView)findViewById(R.id.dialpad_number);
    	String oldDisplayedNumber = dialpadNumberView.getText().toString().replace(" ",  "");
    	String newDisplayedNumber;
    	if (oldDisplayedNumber == null || oldDisplayedNumber.length() == 0) {
    		newDisplayedNumber = oldDisplayedNumber;
    	} else {
    	    newDisplayedNumber = oldDisplayedNumber.substring(0, oldDisplayedNumber.length() - 1);
    	}
    	dialpadNumberView.setText(Util.getDisplayableNumber(newDisplayedNumber));
    	view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		FragmentManager fragmentManager = getFragmentManager();
		ContactBlockFragment cbf = (ContactBlockFragment)fragmentManager.findFragmentById(R.id.fragment_contacts);
		if (newDisplayedNumber.isEmpty()) {
			cbf.setAdapter(MainAdapter.getMainAdapterInstance(context));
		}else {
			cbf.changeAdapterByDialer(newDisplayedNumber);
		}
	}
    public void addOrEditContact(View view) {		
    	TextView dialpadNumberView = (TextView)findViewById(R.id.dialpad_number);
    	String phoneNumber = dialpadNumberView.getText().toString().replace(" ",  "");
		if (phoneNumber == null || phoneNumber.length() == 0) {
			return;
		}
    	
		Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = getContentResolver().query(
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
	    	intent.putExtra(Intents.Insert.PHONE, phoneNumber);
		}
		startActivity(intent);
    }
    public void callButtonPressed(View view) {
    	TextView dialpadNumberView = (TextView)findViewById(R.id.dialpad_number);
    	String tel = dialpadNumberView.getText().toString();
    	
    	if (tel.length() == 0) {
    		//TODO: Need to give an animation rather that returning silently
    		return;
    	}
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + Uri.encode(tel)));
		startActivity(intent);
    }
    public void toggleCallLog(View view) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, 
										R.animator.slide_up, R.animator.slide_down);
		ImageView iv = (ImageView)view.findViewById(R.id.toggle_calllog);

		BackButton backButton = BackButton.getBackButtonInstance(fragmentManager);
    	if (!callLogVisible) {
    		callLogFragment = (CallLogFragment) fragmentManager.findFragmentById(R.id.fragment_call_log);
    		ContactBlockFragment cbf = (ContactBlockFragment)fragmentManager.findFragmentById(R.id.fragment_contacts);
    		
    		transaction.show(callLogFragment);
    		//transaction.hide(cbf);
    		iv.setImageResource(R.drawable.down);
    		callLogVisible = true;
    		transaction.commit();
    		backButton.addToStack("calllog");
    	} else {
    		callLogFragment = (CallLogFragment) fragmentManager.findFragmentById(R.id.fragment_call_log);
    		ContactBlockFragment cbf = (ContactBlockFragment)fragmentManager.findFragmentById(R.id.fragment_contacts);
    		
    		transaction.hide(callLogFragment);
    		//transaction.show(cbf);

    		iv.setImageResource(R.drawable.calllog);
    		callLogVisible = false;
    		transaction.commit();
    		backButton.removeFromStack("calllog");
    	}
    }
    public void hideContactDetailFragment(View view) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.animator.slide_left, R.animator.slide_right, 
										R.animator.slide_left, R.animator.slide_right);
		contactDetailFragment = fragmentManager.findFragmentById(R.id.fragment_contact_detail);
		transaction.hide(contactDetailFragment);
		transaction.commit();
		BackButton.getBackButtonInstance(fragmentManager).removeFromStack("contactdetail");
    }
    
    private void navigationAndStatusBarSettings() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintColor(getResources().getColor(R.color.theme_color));
			tintManager.setStatusBarAlpha(1.0f);

			tintManager.setNavigationBarTintEnabled(true);
			tintManager.setNavigationBarTintColor(getResources().getColor(R.color.theme_color));
			tintManager.setNavigationBarAlpha(1.0f);
			
			SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
			findViewById(android.R.id.content).setPadding(0, config.getPixelInsetTop(true), 
														    config.getPixelInsetRight(), 
														    config.getPixelInsetBottom());
		}
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d("test", "onCreateLoader");
		String[] projections = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_URI};
		String list = "('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', "
				+ "'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', "
				+ "'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', "
				+ "'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z') ";
    	CursorLoader loader = new CursorLoader(
    								this,
    								Phone.CONTENT_URI,
    								projections,
    								null,
    								null,
    								"CASE WHEN " + "substr(" + Phone.DISPLAY_NAME + ",1, 1)" + " IN " + list + " THEN 0 ELSE 1 END, " 
    											+ Phone.DISPLAY_NAME + " COLLATE NOCASE"    								
    			);
    	return loader;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    	ArrayList<Contact>all_contacts = new ArrayList<Contact>();
		int nameIndex = cursor.getColumnIndex(Phone.DISPLAY_NAME);
		int phoneIndex = cursor.getColumnIndex(Phone.NUMBER);
		int photoIndex = cursor.getColumnIndex(Phone.PHOTO_URI);
		MainAdapter mainAdapter = MainAdapter.getMainAdapterInstance(context);
		boolean first = cursor.moveToFirst();
		while (cursor.moveToNext())	{
			String name = cursor.getString(nameIndex);
			String phoneNumber = cursor.getString(phoneIndex);
			String canonicalPhoneNumber = Util.getCanonicalPhoneNumber(phoneNumber);
			Log.d("test", "cursor phone name: " + name);
			Contact contact;
			if (mainAdapter.contactDetails.containsKey(canonicalPhoneNumber)) {
				contact = mainAdapter.contactDetails.get(canonicalPhoneNumber);
				contact.setName(name);
			} else {
				contact = new Contact(name, phoneNumber);
				mainAdapter.contacts.add(contact);
			}
			String photoURI = cursor.getString(photoIndex);
			Contact contact1 = new Contact(name, phoneNumber);
			contact1.setPhotoURI(photoURI);
			all_contacts.add(contact1);
			Log.d("test", "cursor all_contact " + all_contacts.size());

			contact.setPhotoURI(photoURI);
			mainAdapter.contactDetails.put(canonicalPhoneNumber, contact);
		}
		mainAdapter.setAllContactArray(all_contacts);
		Log.d("test", "cursor is " + cursor.getCount());
		Log.d("test", "all_contact length loader = " + all_contacts.size());
		FragmentManager fragmentManager = getFragmentManager();
    	ContactBlockFragment cbf = (ContactBlockFragment)fragmentManager.findFragmentById(R.id.fragment_contacts);
    	cbf.setAdapter(mainAdapter);
    	
    	CallLogFragment clf = (CallLogFragment)fragmentManager.findFragmentById(R.id.fragment_call_log);
    	clf.setAdapter(mainAdapter);
    }
}
