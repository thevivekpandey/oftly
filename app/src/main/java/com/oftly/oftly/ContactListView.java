package com.oftly.oftly;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class ContactListView extends ListView {
    private Context ctx;
    
    private static int indWidth = 20;
	private String[] sections1 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
			"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "."};
	private String[] sections = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
    		"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "."};
    private float scaledWidth;
    private float sx;
    private int indexSize;
    private String section;
    private boolean showLetter = false;
    private boolean isSearchOn = false;

    public ContactListView(Context context) {
		super(context);
		ctx = context;
	}
    public ContactListView(Context context, AttributeSet attrs) {
        super(context, attrs);        
		ctx = context;
    }

    public ContactListView(Context context, String keyList) {
        super(context);         
		ctx = context;

    }
    
    public void setSearchOn() {
    	isSearchOn = true;
    }
    public void setSearchOff() {
    	isSearchOn = false;
    }
    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);

		int ot = ctx.getResources().getConfiguration().orientation;
		if (ot == Configuration.ORIENTATION_LANDSCAPE) {
			sections = new String[]{"a", "c", "e", "g", "i", "k", "m", "o", "q", "s", "u", "w", "y", "z", "."};
		}
        scaledWidth = indWidth * 3;
        sx = this.getWidth() - this.getPaddingRight() - scaledWidth;
     
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setAlpha(100);
     
        canvas.drawRect(sx, this.getPaddingTop(), sx + scaledWidth,
                this.getHeight() - this.getPaddingBottom(), p);
         
        indexSize = (this.getHeight() - this.getPaddingTop() - getPaddingBottom())
                / sections.length;
     
        Paint textPaint = new Paint();
        textPaint.setColor(Color.LTGRAY);
        textPaint.setTextSize(scaledWidth / 2);
     
        if (!isSearchOn) {
	        for (int i = 0; i < sections.length; i++) {
	        	float x = sx + textPaint.getTextSize() / 2;
	        	float y = getPaddingTop() + indexSize * (i + 1);
		            canvas.drawText(sections[i].toUpperCase(), x, y, textPaint);
	        }
        }
        
        if (!isSearchOn && showLetter && section != null && !section.equals("")) {            
            Paint textPaint2 = new Paint();           
            textPaint2.setColor(Color.LTGRAY);
            textPaint2.setTextSize(8 * indWidth);
             
            canvas.drawText(section.toUpperCase(), getWidth() / 2,  getHeight() / 2, textPaint2);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        
    	Rect rectf = new Rect();
    	getChildAt(0).getLocalVisibleRect(rectf);

    	int firstVisiblePosition = getFirstVisiblePosition();
    	int height = rectf.height();

        switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN: {
	            if (x < sx) {
	                return super.onTouchEvent(event);
	            } else {
	                // We touched the index bar
	                float y = event.getY() - this.getPaddingTop() - getPaddingBottom();
	                if (shouldReact(y, firstVisiblePosition, height)) {
		                int currentPosition = (int) Math.floor(y / sections1.length);
		                currentPosition = sanitizeCurrentPosition(currentPosition);
		                section = sections1[currentPosition];
		                this.setSelection(((SectionIndexer) getAdapter())
		                        .getPositionForSection(currentPosition));
		            }
	            }
	            break;
	        }
	        case MotionEvent.ACTION_MOVE: {
	            if (x < sx) {
	                return super.onTouchEvent(event);
	            } else {
	                float y = event.getY();
	                if (shouldReact(y, firstVisiblePosition, height)) {
		                int currentPosition = (int) Math.floor(y / sections1.length);
		                currentPosition = sanitizeCurrentPosition(currentPosition);
		                section = sections1[currentPosition];
		                this.setSelection(((SectionIndexer) getAdapter())
		                        .getPositionForSection(currentPosition));
		                showLetter = true;
	                }
	            }
	            break;
	        }
	        case MotionEvent.ACTION_UP: {
	        	showLetter = false;
	        	ContactListView.this.invalidate();
	        	break;
	        }
        }
        return super.onTouchEvent(event);
    }
    private int sanitizeCurrentPosition(int pos) {
    	if (pos >= sections1.length) {
    		return sections1.length - 1;
    	}
    	if (pos < 0) {
    		return 0;
    	}
    	return pos;    	
    }
    
    /* 0th item of the list is the smart contact grid. If the user touches
     * right portion of that, we do not have to react.
     */
    private boolean shouldReact(float drawingLocation, int firstVisiblePosition, int height) {
    	if (firstVisiblePosition > 0) {
    		return true;
    	}
    	if (drawingLocation > height) {
    		return true;
    	}
    	return false;
    }
}