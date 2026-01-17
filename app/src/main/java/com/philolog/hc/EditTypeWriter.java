package com.philolog.hc;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by jeremy on 4/7/16.
 */
public class EditTypeWriter extends androidx.appcompat.widget.AppCompatEditText {
    private CharSequence mText;
    private int mIndex;
    public Boolean passEvents = false;
    private long mDelay = 25; //Default 500ms delay
    private Runnable mComplete;

    public EditTypeWriter(Context context) {
        super(context);
        setShowSoftInputOnFocus(false);
    }

    public EditTypeWriter(Context context, AttributeSet attrs) {
        super(context, attrs);
        setShowSoftInputOnFocus(false);
    }

    private final Handler mHandler = new Handler();
    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if(mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            }
            else
            {
                if (mComplete != null)
                    mHandler.postDelayed(mComplete, 700);
            }
        }
    };
    private final Runnable characterRemover = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex--));
            if(mIndex >= 0) {
                mHandler.postDelayed(characterRemover, mDelay);
            }
            else
            {
                if (mComplete != null)
                    mHandler.postDelayed(mComplete, 10);
            }
        }
    };

    public void animateText(CharSequence text, Runnable complete) {
        mText = text;
        mIndex = 0;
        mComplete = complete;

        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

    public void animateHideText(Runnable complete) {
        mText = this.getText();
        mIndex = mText.length();
        mComplete = complete;

        //setText(text);
        mHandler.removeCallbacks(characterRemover);
        mHandler.postDelayed(characterRemover, mDelay);
    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }

    //this allows pinch to go through this text edit widget to the parent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
            if (passEvents) {
                Log.e("abc", "pinchdddddddd");
                return false;
            }
            else
            {
                return true;
            }

    }
}
