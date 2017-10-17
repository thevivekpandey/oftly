package com.oftly.oftly;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialpadFragment extends Fragment {
	Typeface buttonTypeface;
	Typeface numberTypeface;
	boolean dialpadVisible = false;
	
	public DialpadFragment() {
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dialpad, container, false);
		buttonTypeface = Typeface.createFromAsset(getActivity().getAssets(), "RobotoCondensed-Light.ttf");
		numberTypeface = Typeface.createFromAsset(getActivity().getAssets(), "RobotoCondensed-Light.ttf");
		
	    int[] numbers = {R.id.dialpad_one_number, R.id.dialpad_one_text, R.id.dialpad_two_number, R.id.dialpad_two_text,
    					R.id.dialpad_three_number, R.id.dialpad_three_text, R.id.dialpad_four_number, R.id.dialpad_four_text,
    					R.id.dialpad_five_number, R.id.dialpad_five_text, R.id.dialpad_six_number, R.id.dialpad_six_text,
    					R.id.dialpad_seven_number, R.id.dialpad_seven_text, R.id.dialpad_eight_number, R.id.dialpad_eight_text,
    					R.id.dialpad_nine_number, R.id.dialpad_nine_text, R.id.dialpad_star_number, R.id.dialpad_star_text,
    					R.id.dialpad_zero_number, R.id.dialpad_zero_text, R.id.dialpad_hash_number, R.id.dialpad_hash_text,
	    };
	    for (int number : numbers) {
	    	TextView textView = (TextView)view.findViewById(number);
	        textView.setTypeface(buttonTypeface);
	    }	    
	    TextView textView = (TextView)view.findViewById(R.id.dialpad_number);
	    textView.setTypeface(numberTypeface);
		return view;
	}
	public void toggleDialpadVisibility() {
    	if (!dialpadVisible) {
    		showDialpad();
    	} else {
    		hideDialpad();
    	}
	}
    private void showDialpad() {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, 
										R.animator.slide_up, R.animator.slide_down);
		ImageView iv = (ImageView)getActivity().findViewById(R.id.dialpad_button);
	
		transaction.show(this);   
		iv.setImageResource(R.drawable.down);
        dialpadVisible = true;
        
        //TransparentFragment transparentFragment =
        //		(TransparentFragment)fragmentManager.findFragmentById(R.id.fragment_transparent);
        //transaction.show(transparentFragment);
        //showTransparentLayer();
        transaction.commit();
    }
    private void hideDialpad() {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, 
										R.animator.slide_up, R.animator.slide_down);
		ImageView iv = (ImageView)getActivity().findViewById(R.id.dialpad_button);
		
		transaction.hide(this);
		iv.setImageResource(R.drawable.dialpad);
		dialpadVisible = false;

		TransparentFragment transparentFragment = 
				(TransparentFragment)fragmentManager.findFragmentById(R.id.fragment_transparent);
        transaction.hide(transparentFragment);
        hideTransparentLayer();
        transaction.commit();
    }
    private void showTransparentLayer() {
        Integer colorFrom = getResources().getColor(R.color.transparent);
        Integer colorTo = getResources().getColor(R.color.translucent);
        transparentLayerAnimation(colorFrom, colorTo);
    }
    private void hideTransparentLayer() {
        Integer colorFrom = getResources().getColor(R.color.translucent);
        Integer colorTo = getResources().getColor(R.color.transparent);
        transparentLayerAnimation(colorFrom, colorTo);    	
    }
    private void transparentLayerAnimation(Integer colorFrom, Integer colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
            	LinearLayout layout = (LinearLayout)getActivity().findViewById(R.id.fragment_transparent);
                layout.setBackgroundColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }
    public boolean getDialpadVisibility() {
    	return dialpadVisible;
    }
 
}
