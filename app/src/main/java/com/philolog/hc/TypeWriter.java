package com.philolog.hc;

import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.content.Context;
import android.util.AttributeSet;
import android.os.Handler;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jeremy on 3/20/16.
 */
//http://stackoverflow.com/questions/6700374/android-character-by-character-display-text-animation
public class TypeWriter extends androidx.appcompat.widget.AppCompatTextView {

    private CharSequence mText;
    private int mIndex;
    private long mDelay = 25; //Default 500ms delay
    private Runnable mComplete;


//http://stackoverflow.com/questions/14518706/what-is-the-android-relevant-of-cadisplaylinkios-or-compositiontargetwp
    public TypeWriter(Context context) {
        super(context);
/*
        final ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                // Do whatever you need to do...
                Log.e("vto", "on draw");
                invalidate();
            }
        });
        */
    }

    public void setTextWithPadding(String s, Boolean cTop) {
        super.setText(s);
        setLeftPadding(this, s, cTop);
    }

    public TypeWriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private final Handler mHandler = new Handler();
    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            }
            else {
                if (mComplete != null)
                    mHandler.postDelayed(mComplete, 700);
                //setPadding(50,0,0,0);
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
                    mHandler.postDelayed(mComplete, 1);
            }
        }
    };

    public void animateText(CharSequence text, Boolean cTop, Runnable complete) {
        mText = text;
        mIndex = 0;
        mComplete = complete;
        setLeftPadding(this, text.toString(), cTop);

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


    public int getTextWidth(TextView v, String str)
    {
        Rect bounds = new Rect();
        Paint textPaint = v.getPaint();

        List<String> items = Arrays.asList(str.split("\\s*,\\s*"));
        int longest = 0;
        int longestIndex = 0;
        int numForms = items.size();
        for (int i = 0; i < numForms; i++)
        {
            if(items.get(i).length() > longest) {
                longest = items.get(i).length();
                longestIndex = i;
            }
        }
        if (numForms > 1)
            textPaint.getTextBounds(items.get(longestIndex), 0, longest, bounds);
        else
            textPaint.getTextBounds(str, 0, str.length(), bounds);
        return bounds.width();
    }

    public void setLeftPadding(TextView v, String s, Boolean cTop)
    {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = getTextWidth(v, s);
        int screenWidth = dm.widthPixels;

        int leftPadding = ((screenWidth - width) / 2);
        if (leftPadding < 0) //don't push the check or x off the screen
            leftPadding = 0;

        //leftPadding = convertDpToPx(leftPadding, dm);

        setPadding(leftPadding,0,0,0);
        if (cTop)
            v.setGravity(Gravity.START|Gravity.TOP);
        else
            v.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);


    }

    private int convertDpToPx(int dp, DisplayMetrics displayMetrics) {
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return Math.round(pixels);
    }
}
