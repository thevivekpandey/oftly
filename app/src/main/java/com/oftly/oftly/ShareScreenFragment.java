package com.oftly.oftly;

import com.oftly.oftly.asynctasks.ShowSharableContactsTask;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShareScreenFragment extends Fragment {
	Context context;
	MainAdapter adapter;
	public ShareScreenFragment() {
	}
	/*public ShareScreenFragment(Context context, MainAdapter adapter) {
		this.context = context;
		this.adapter = adapter;
	}*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_share_screen, container, false);
		TextView textView = (TextView)(view.findViewById(R.id.share_screen_text));
		textView.setText("Please wait...");

		new ShowSharableContactsTask(context, adapter).execute();
		return view;
	}
}