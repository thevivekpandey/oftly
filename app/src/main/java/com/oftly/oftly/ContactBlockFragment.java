package com.oftly.oftly;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactBlockFragment extends ListFragment {
	int numBlocks;
	SearchAdapter adapter;
	int[] blockNumber2contactBeginIndex;
	public ContactBlockAdapter contactBlockAdapter;
	public ContactListView listView;
	DisplayMetrics displayMetrics;
	MainAdapter mainAdapter;
	String oldText = "";
    private ImageFetcher imageFetcher;
    private static final String IMAGE_CACHE_DIR = "contactblock";
    Activity activity;

	public ContactBlockFragment() {
		if (getActivity() != null) {
			activity = getActivity();
		}
		mainAdapter = MainAdapter.getMainAdapterInstance(activity);
    	adapter = new SearchAdapter(mainAdapter.all_contacts);
    	for (Contact contact: mainAdapter.contacts) {
			adapter.addContactToContacts(contact);
    	}
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.125f); // Set memory cache to 12.5% of app memory
		imageFetcher = new ImageFetcher(getActivity(), 300, 300);
        imageFetcher.setLoadingImage(R.drawable.empty_photo);
        imageFetcher.addImageCache(getActivity().getFragmentManager(), cacheParams);

        numBlocks = Util.getNumBlocks(mainAdapter.contacts.size());
        blockNumber2contactBeginIndex = new int[numBlocks];
        ContactBlockGenerator cbm;
        for (int i = 1; i < numBlocks; i++) {
        	cbm = new ContactBlockGenerator(-1, 2 * (i - 1), adapter, blockNumber2contactBeginIndex[i - 1]);
        	blockNumber2contactBeginIndex[i] = blockNumber2contactBeginIndex[i - 1] + cbm.numItems;
        }

    	displayMetrics = new DisplayMetrics();
    	getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    	listView = (ContactListView)inflater.inflate(R.layout.fragment_contacts, container, false);
    	listView.setTextFilterEnabled(true);

    	contactBlockAdapter = new ContactBlockAdapter(getActivity(), R.layout.contact_block, 
				displayMetrics.widthPixels, numBlocks, adapter, 
				getFragmentManager(), blockNumber2contactBeginIndex, imageFetcher);
    	listView.setAdapter(contactBlockAdapter);  	
    	
    	return listView;
    }
	
    @Override
    public void onResume() {
        super.onResume();
        imageFetcher.setExitTasksEarly(false);
        //I do not know why next line is there. I copied it from displayingbitmap code
        contactBlockAdapter.notifyDataSetChanged();
    }
    @Override
    public void onPause() {
        super.onPause();
        imageFetcher.setPauseWork(false);
        imageFetcher.setExitTasksEarly(true);
        imageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageFetcher.closeCache();
    }

	public void changeAdapterByDialer(String dialText) {
		adapter = new SearchAdapter(dialText);
		String canonicalSearchText = dialText.toLowerCase();
		String rex = "^";
		for (int i = 0; i < canonicalSearchText.length(); i++) {
			char digit = canonicalSearchText.charAt(i);
			switch (digit) {
				case '0': rex += "[+]";
					break;
				case '1': rex += "";
					break;
				case '2': rex += "[abc]";
					break;
				case '3': rex += "[def]";
					break;
				case '4': rex += "[ghi]";
					break;
				case '5': rex += "[jkl]";
					break;
				case '6': rex += "[mno]";
					break;
				case '7': rex += "[pqrs]";
					break;
				case '8': rex += "[tuv]";
					break;
				case '9': rex += "[xyz]";
					break;
			}
		}
		Pattern pattern = Pattern.compile(rex);
    	/* all_contacts is used for linear list. */
		for (Contact contact: mainAdapter.all_contacts) {
			if (dialText.length() == 0) {
				adapter.addContactToAllContacts(contact);
				continue;
			}
			if (contact.phone.replace(" ","").contains(dialText)) {
				adapter.addContactToAllContacts(contact);
				continue;
			}
			if (contact.name != null) {
				String canonicalContactName = contact.name.toLowerCase();
				String[] nameArray = canonicalContactName.split(" ");
				for (String name : nameArray) {
					Matcher match = pattern.matcher(name);
					if (match.find()) {
						adapter.addContactToAllContacts(contact);
						break;
					}
				}
			}
		}

		contactBlockAdapter.setAdapter(adapter, numBlocks, blockNumber2contactBeginIndex);

		/*if (dialText.length() == 0) {
			listView.setSearchOff();
		} else {
			listView.setSearchOn();
		}*/
		contactBlockAdapter.notifyDataSetChanged();
	}
    public void changeAdapter(String searchText) {
    	if (searchText.equals(oldText)) {
    		return;
    	}
    	
    	adapter = new SearchAdapter(searchText);
    	/* all_contacts is used for linear list. */
    	for (Contact contact: mainAdapter.all_contacts) {
    		if (searchText.length() == 0) {
    			adapter.addContactToAllContacts(contact);
    			continue;
    		}
    		if (contact.name != null) {
    			String canonicalContactName = contact.name.toLowerCase();
    			String canonicalSearchText = searchText.toLowerCase();

				if (canonicalContactName.startsWith(canonicalSearchText) ||
						canonicalContactName.contains(" " + canonicalSearchText)) {
    				adapter.addContactToAllContacts(contact);
    			}    			
    		}
    	}

    	/* contacts is used for smart list. */
    	if (searchText.length() == 0) { /* If we are searching, we do not have smart list. */
	    	for (Contact contact: mainAdapter.contacts) {
	    		if (searchText.length() == 0) {
	    			adapter.addContactToContacts(contact);
	    			continue;
	    		}
	    		if (contact.name != null) {
	    			String canonicalContactName = contact.name.toLowerCase();
	    			String canonicalSearchText = searchText.toLowerCase();
					if (canonicalContactName.startsWith(canonicalSearchText) ||
							canonicalContactName.contains(" " + canonicalSearchText)) {
	    				adapter.addContactToContacts(contact);
	    			}
	    		}
	    	}
    	}
    	
    	oldText = searchText;
    	contactBlockAdapter.setAdapter(adapter, numBlocks, blockNumber2contactBeginIndex);
    	
    	if (searchText.length() == 0) {
    		listView.setSearchOff();
    	} else {
    		listView.setSearchOn();
    	}
    	contactBlockAdapter.notifyDataSetChanged();
    }
    public void setAdapter(MainAdapter mainAdapter) {
    	this.mainAdapter = mainAdapter;
    	adapter = new SearchAdapter(mainAdapter.all_contacts);

    	for (Contact contact: mainAdapter.contacts) {
			adapter.addContactToContacts(contact);
    	}
        numBlocks = Util.getNumBlocks(mainAdapter.contacts.size());
        blockNumber2contactBeginIndex = new int[numBlocks];
        ContactBlockGenerator cbm;
        for (int i = 1; i < numBlocks; i++) {
        	cbm = new ContactBlockGenerator(-1, 2 * (i - 1), adapter, blockNumber2contactBeginIndex[i - 1]);
        	blockNumber2contactBeginIndex[i] = blockNumber2contactBeginIndex[i - 1] + cbm.numItems;
        }
    	contactBlockAdapter.setAdapter(adapter, numBlocks, blockNumber2contactBeginIndex);
    	contactBlockAdapter.notifyDataSetChanged();
    }
    public void newPhotoAdded() {
    	contactBlockAdapter.notifyDataSetChanged();
    	Util.Log("I have notified");
    }
}
