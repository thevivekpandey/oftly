package com.oftly.oftly;

import java.util.ArrayList;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

public class CallLogFragment extends Fragment {
	MainAdapter mainAdapter;
	CallAdapter adapter;
    private ImageFetcher imageFetcher;
    private static final String IMAGE_CACHE_DIR = "calllog";

	public CallLogFragment() {
		mainAdapter = MainAdapter.getMainAdapterInstance(getActivity());
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        CallLogLayout callLogLayout = (CallLogLayout)inflater.inflate(R.layout.fragment_call_log, container, false);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.125f); // Set memory cache to 12.5% of app memory
		imageFetcher = new ImageFetcher(getActivity(), 300, 300);
        imageFetcher.setLoadingImage(R.drawable.empty_photo);
        imageFetcher.addImageCache(getActivity().getFragmentManager(), cacheParams);

        //mainAdapter = new MainAdapter();
        ArrayList<Call>calls = Call.getCalls(getActivity().getContentResolver());
    	adapter = new CallAdapter(getActivity(), R.layout.call, calls, mainAdapter, 
    			getActivity().getContentResolver(), imageFetcher);

    	ListView listView = (ListView)callLogLayout.findViewById(R.id.call_log);
    	listView.setAdapter(adapter);
    	
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        imageFetcher.setPauseWork(true);
                    }
                } else {
                    imageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            }
        });

    	return callLogLayout;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        imageFetcher.setExitTasksEarly(false);
        
        //I do not know why next line is there. I copied it from displayingbitmap code
        adapter.notifyDataSetChanged();
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

    public void changeAdapter(String searchText) {
    	ArrayList<Call>calls = Call.getCalls(getActivity().getContentResolver(), searchText, mainAdapter);
    	adapter.setAdapter(calls, mainAdapter);
    	adapter.notifyDataSetChanged();
    }
    public void setAdapter(MainAdapter mainAdapter) {
    	this.mainAdapter = mainAdapter;
    	changeAdapter("");
    }
}