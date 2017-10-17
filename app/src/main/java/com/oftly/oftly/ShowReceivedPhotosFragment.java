package com.oftly.oftly;

import com.oftly.oftly.asynctasks.ShowSharedContactsWithMeTask;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShowReceivedPhotosFragment extends Fragment {
	Context context;
	MainAdapter adapter;
	public ShowReceivedPhotosFragment() {
	}
	/*public ShowReceivedPhotosFragment(Context context, MainAdapter adapter) {
		this.context = context;
		this.adapter = adapter;
	}*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_show_shared, container, false);
		TextView textView = (TextView)(view.findViewById(R.id.show_shared_text));
		textView.setText("Please wait...");

		new ShowSharedContactsWithMeTask(context, adapter).execute();
		return view;
	}

}
