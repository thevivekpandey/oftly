package com.oftly.oftly;

import android.content.Context;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialpadLayout extends LinearLayout {
    private float yFraction = 0;
    private ViewTreeObserver.OnPreDrawListener preDrawListener = null;

    public DialpadLayout(Context context) {
        super(context);
    }

    public DialpadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialpadLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    public void onFinishInflate() {
        setOnLongClickListener();    	
    }
    public void setOnLongClickListener() {
    	ImageView imageView = (ImageView)findViewById(R.id.cut_digit);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
            	TextView dialpadNumberView = (TextView)findViewById(R.id.dialpad_number);
            	dialpadNumberView.setText("");
            	return true;
            }
        });
        
        TextView plusNumberView = (TextView)findViewById(R.id.dialpad_zero_number);
        plusNumberView.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View view) {
		    	TextView dialpadNumberView = (TextView)findViewById(R.id.dialpad_number);
		    	String oldDisplayedNumber = dialpadNumberView.getText().toString().replace(" " , "");
		    	String newDisplayedNumber = oldDisplayedNumber + "+";
		    	dialpadNumberView.setText(Util.getDisplayableNumber(newDisplayedNumber));
		    	view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				return true;
			}
		});
        
        TextView plusTextView = (TextView)findViewById(R.id.dialpad_zero_text);
        plusTextView.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View view) {
		    	TextView dialpadNumberView = (TextView)findViewById(R.id.dialpad_number);
		    	String oldDisplayedNumber = dialpadNumberView.getText().toString().replace(" " , "");
		    	String newDisplayedNumber = oldDisplayedNumber + "+";
		    	dialpadNumberView.setText(Util.getDisplayableNumber(newDisplayedNumber));
		    	view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				return true;
			}
		});
    }
    public void setYFraction(float fraction) {
        this.yFraction = fraction;
        if (getHeight() == 0) {
            if (preDrawListener == null) {
                preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
                        setYFraction(yFraction);
                        return true;
                    }
                };
                getViewTreeObserver().addOnPreDrawListener(preDrawListener);
            }
            return;
        }
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
    }
    public float getYFraction() {
        return this.yFraction;
    }
}
