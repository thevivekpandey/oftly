package com.oftly.oftly;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

public class CallLogLayout extends LinearLayout {
    private float xFraction = 0;
    private float yFraction = 0;

    private ViewTreeObserver.OnPreDrawListener preDrawListener = null;
    private ViewTreeObserver.OnPreDrawListener preDrawListener1 = null;

    public CallLogLayout(Context context) {
        super(context);
    }

    public CallLogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CallLogLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void setXFraction(float fraction) {

        this.xFraction = fraction;

        if (getWidth() == 0) {
            if (preDrawListener1 == null) {
                preDrawListener1 = new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(preDrawListener1);
                        setXFraction(xFraction);
                        return true;
                    }
                };
                getViewTreeObserver().addOnPreDrawListener(preDrawListener1);
            }
            return;
        }
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
    }

    public float getXFraction() {
        return this.xFraction;
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

    /*public float getYFraction() {
        return this.yFraction;
    }*/
}
