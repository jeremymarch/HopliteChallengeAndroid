package com.philolog.hc;

import android.app.Activity;
import android.content.res.Resources;
import android.text.Spanned;
import android.view.View;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Button;
import android.text.Editable;
import android.os.Bundle;
import android.os.Build;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ScaleGestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.text.TextUtils;
import android.text.Html;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.animation.Animation;
import android.graphics.Typeface;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.content.DialogInterface;
import android.app.AlertDialog;
import java.lang.String;
import android.preference.PreferenceManager;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.animation.LinearInterpolator;
import android.os.Vibrator;
import android.content.pm.ActivityInfo;
import org.json.JSONObject;
import org.json.JSONException;
import android.os.AsyncTask;
import java.net.URL;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import android.view.Display;
import android.graphics.Point;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import android.content.Context;
import android.content.SharedPreferences.Editor;

public class MainActivity extends Activity
{
    private final static int KEYPRESS_VIBRATE = 40;
    //public final static int NO_ACCENT = 0;
    public final static int ACUTE = 1;
    public final static int CIRCUMFLEX = 2;
    //public final static int GRAVE = 3;
    public final static int MACRON = 4;
    public final static int ROUGH_BREATHING = 5;
    public final static int SMOOTH_BREATHING =6;
    public final static int IOTA_SUBSCRIPT = 7;
    public final static int SURROUNDING_PARENTHESES = 8;
    public final static int COMBINING_GRAVE             = 0x0300;
    public final static int COMBINING_ACUTE             = 0x0301;
    public final static int COMBINING_CIRCUMFLEX        = 0x0302;
    public final static int COMBINING_MACRON            = 0x0304;
    public final static int COMBINING_DIAERESIS         = 0x0308;
    public final static int COMBINING_SMOOTH_BREATHING  = 0x0313;
    public final static int COMBINING_ROUGH_BREATHING   = 0x0314;
    public final static int COMBINING_IOTA_SUBSCRIPT    = 0x0345;

    private final static boolean bSendDiagnostics = false;
    private Vibrator vibrator;
    private View mainView;
    public TypeWriter origFormText, stemText, changedFormText;
    public TextView mTimeLabel, mMFLabelView;
    public String origStr, origStrDecomposed, changedStr, changedStrDecomposed;
    public Spanned stemStr;
    private EditTypeWriter editText;
    private GreekVerb gv1, gv2;
    private Verb v1, v2;
    private VerbSequence verbSeqObj;
    private boolean front;
    private GreekKeyboard mKeyboardView;
    private Button continueButton;
    public ImageView greenCheckRedX;
    private Runnable mUpdateTimeTask, mEnableKeyboard, mShowStem, mShowOrigForm, mStartTimerRunnable;
    private Handler mHandler;
    private long mStartTime;
    private int HCTime;
    public boolean isDecomposedMode = false;
    private boolean mTimerCountDown = false;
    private boolean mMFPressed = false;
    public boolean[] mUnits;
    public int mNumUnits = 20;
    public TextView scoreLabel;
    public Boolean isHCGame = false;
    public int lives = 3;
    public ImageView life1, life2, life3;
    public TextView gameOverLabel;
    public double elapsedTime = 0;
    public boolean allowVibrate = false;
    public String screenSize = "";
    public String osInfo = "";
    public String uniqueDeviceID = "";
    public Boolean timerTimedOut = false;

    public String makeBoldStem(String d1, String d2)
    {
        String[] d1Array = d1.split(" ");
        String[] d2Array = d2.split(" ");

        for (int i = 0; i < 5; i++) {
            if (!d1Array[i].equals(d2Array[i]))
                d2Array[i] = "<b>" + d2Array[i] + "</b>";
        }
        return TextUtils.join(" ", d2Array);
    }

    public void flip()
    {
        if (front)
        {
            Log.d("abc", "front");
        }
        else
        {
            Log.d("abc", "back");
            isDecomposedMode = false;
            origStr = gv1.getForm(0,0);
            origStr = origStr.replace(", ", ",\n");
            origStrDecomposed = gv1.getForm(0,1);
            origStrDecomposed = origStrDecomposed.replace(", ", ",\n");
            changedStr = gv2.getForm(0,0);
            changedStr = changedStr.replace(", ", ",\n");
            changedStrDecomposed = gv2.getForm(0,1);
            changedStrDecomposed = changedStrDecomposed.replace(", ", ",\n");
            stemStr = Html.fromHtml(makeBoldStem(gv1.getAbbrevDescription(), gv2.getAbbrevDescription()));

            mMFPressed = false;
            mKeyboardView.mMFPressed = false;

            mMFLabelView.setVisibility(View.INVISIBLE);
            changedFormText.setVisibility(View.INVISIBLE);
            changedFormText.setText(changedStr);

            mTimeLabel.setTextColor(textColor);
            if (isHCGame)
                mTimeLabel.setText("30.00 sec");
            else
                mTimeLabel.setText("0.00 sec");
            editText.setText("");

            stemText.setText("");
            editText.setTextColor(textColor);
            greenCheckRedX.setVisibility(View.GONE);
            isDecomposedMode = false;

            Log.e("abc", "VerbSeq: "  + verbSeqObj.seq);
            if (verbSeqObj.state == VerbSequence.STATE_NEW) {
                origFormText.setText("");
                mHandler.postDelayed(mShowOrigForm, 700);
            }
            else {
                mHandler.postDelayed(mShowStem, 700);
            }
        }
        front = !front;
    }

    public int getTextWidth(TextView v, String str)
    {
        Rect bounds = new Rect();
        Paint textPaint = v.getPaint();
        textPaint.getTextBounds(str, 0, str.length(), bounds);
        return bounds.width();
    }

    public boolean checkVerb2AndSetCheckOrX()
    {
        //http://stackoverflow.com/questions/3416087/how-to-set-margin-of-imageview-using-code-not-xml
        String text = editText.getText().toString();
        int width = getTextWidth(editText, text);
        DisplayMetrics dm = mainView.getResources().getDisplayMetrics();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(greenCheckRedX.getLayoutParams());
        int offset = ((editText.getWidth() - width) / 2) - convertDpToPx(45, dm);
        if (offset < 0) //don't push the check or x off the screen
            offset = 0;
        if (text.length() == 0) //if no text entered, center it
        {
            offset = 0;
            lp.gravity =  Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
        }
        else
        {
            lp.gravity =  Gravity.CENTER_VERTICAL|Gravity.RIGHT;
        }
        lp.setMargins(0, 0, offset, 0);
        lp.width = convertDpToPx(24, dm);
        lp.height = convertDpToPx(24, dm);
        greenCheckRedX.setLayoutParams(lp);

        boolean isCorrect;
        //change ret back to space to compare
        String expectedForm = changedFormText.getText().toString().replace(",\n", ", ");
        String answerText = editText.getText().toString();
        String sElapsedTime = String.format("%.2f", elapsedTime);
        isCorrect = gv1.compareFormsCheckMFRecordResult(answerText, expectedForm, sElapsedTime, mMFPressed);

        Log.e("abc", "score1: " + gv1.score + ", " + isHCGame);
        if (isHCGame) {
            scoreLabel.setText(Integer.toString(gv1.score));
        }
        if (isCorrect) {
            greenCheckRedX.setImageResource(R.drawable.greencheck);
            greenCheckRedX.setVisibility(View.VISIBLE);
        }
        else
        {
            greenCheckRedX.setImageResource(R.drawable.redx);
            greenCheckRedX.setVisibility(View.VISIBLE);
            editText.setTextColor(0xFF888888);
            lives -= 1;

            if (lives == 3)
            {
                life3.setVisibility(View.VISIBLE);
                life2.setVisibility(View.VISIBLE);
                life1.setVisibility(View.VISIBLE);
            }
            else if (lives == 2)
            {
                life3.setVisibility(View.GONE);
                life2.setVisibility(View.VISIBLE);
                life1.setVisibility(View.VISIBLE);
            }
            else if (lives == 1)
            {
                life3.setVisibility(View.GONE);
                life2.setVisibility(View.GONE);
                life1.setVisibility(View.VISIBLE);
            }
            else if (lives == 0) {
                life3.setVisibility(View.GONE);
                life2.setVisibility(View.GONE);
                life1.setVisibility(View.GONE);
                gameOverLabel.setVisibility(View.VISIBLE);
                continueButton.setText("Play again?");
            }
        }

        if (bSendDiagnostics) {
            sendDiagnostics(isCorrect, lives, gv1.score, answerText, expectedForm, sElapsedTime, mMFPressed);
        }
        return isCorrect;
    }

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    public synchronized static String uid(Context context) {
    if (uniqueID == null) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                PREF_UNIQUE_ID, Context.MODE_PRIVATE);
        uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
        if (uniqueID == null) {
            uniqueID = UUID.randomUUID().toString();
            Editor editor = sharedPrefs.edit();
            editor.putString(PREF_UNIQUE_ID, uniqueID);
            editor.commit();
        }
    }
    return uniqueID;
}

    public void sendDiagnostics(Boolean isCorrect, int lives, int score, String answerText, String expectedForm, String elapsedTime, Boolean mfPressed)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("type", "debugRequestPlusAnswer");
            postData.put("answerText", answerText);
            postData.put("expectedForm", expectedForm);
            postData.put("isCorrect", isCorrect);
            postData.put("answerSeconds", elapsedTime);
            postData.put("timedOut", timerTimedOut);
            postData.put("mfPressed", mfPressed.toString());
            postData.put("lives", lives);
            postData.put("score", score);
            postData.put("verbID", gv2.verbid);
            postData.put("person", gv2.person);
            postData.put("number", gv2.number);
            postData.put("tense", gv2.tense);
            postData.put("voice", gv2.voice);
            postData.put("mood", gv2.mood);
            postData.put("appversion", "1.8.0");
            postData.put("device", uniqueDeviceID);
            postData.put("agent", osInfo);
            postData.put("screen", screenSize);
            postData.put("accessdate", "");
            postData.put("error", "");

            new SendDeviceDetails().execute("https://philolog.us/hc.php", "[" + postData.toString() + "]");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.i("abc", "[" + postData.toString() + "]");
    }

    public void checkVerb()
    {
        editText.passEvents = true;

        if (checkVerb2AndSetCheckOrX()) {
            flip();
            continueButton.setVisibility(View.VISIBLE);
        }
        else
        {
            Runnable runn = new Runnable() {
                public void run() {
                    changedFormText.setVisibility(View.VISIBLE);
                    //centerTextView(changedFormText, changedFormText.getText().toString(), R.id.editholder);
                    Runnable r2 = new Runnable() {
                        public void run() { continueButton.setVisibility(View.VISIBLE); }
                    };
                    changedFormText.animateText(changedFormText.getText().toString(), false, r2);
                    flip();
                }
            };
            mHandler.postDelayed(runn, 700);
        }
    }

    private int convertDpToPx(int dp, DisplayMetrics displayMetrics) {
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return Math.round(pixels);
    }

    public void onEnterPressed()
    {
        stopTimer();
        editText.setEnabled(false);
        editText.passEvents = true;
        hideCustomKeyboard(null);
        Runnable runCheckVerb = new Runnable() {
            public void run() {
                checkVerb();
            }
        };
        mHandler.postDelayed(runCheckVerb, 1);//a tiny delay prevents freeze on api 21
    }

    public void onMFPressed(Editable editable, int start)
    {
        mKeyboardView.mMFPressed = true;
        mKeyboardView.invalidate(); //to be sure it redraws with new mf button
        if( !changedFormText.getText().toString().contains(",") )
        {
            stopTimer();
            editText.setEnabled(false);
            editText.passEvents = true;
            mMFLabelView.setVisibility(View.VISIBLE);
            hideCustomKeyboard(null);
            mMFPressed = true;
            Runnable runCheckVerb = new Runnable() {
                public void run() {
                    checkVerb();
                }
            };
            mHandler.postDelayed(runCheckVerb, 1); //a tiny delay prevents freeze on api 21
        }
        else {
            if (!mMFPressed) {
                mStartTime += (HCTime / 2 * 1e6 * 1000); //add time
                mMFPressed = true;
                mMFLabelView.setVisibility(View.VISIBLE);
                //change key to comma
            }
            else
            {
                //insert comma
                editable.insert(start, ", ");
            }
        }
    }

    public boolean isCombiningCharacter(char s)
    {
        //test this with a visible character: s == 0x03B2 ||
        return s == COMBINING_GRAVE ||
                s == COMBINING_ACUTE ||
                s == COMBINING_CIRCUMFLEX ||
                s == COMBINING_MACRON ||
                s == COMBINING_DIAERESIS ||
                s == COMBINING_SMOOTH_BREATHING ||
                s == COMBINING_ROUGH_BREATHING ||
                s == COMBINING_IOTA_SUBSCRIPT;
    }

    //see if there are one or more combining characters to the right of the cursor
    //if so move past them, so we don't insert a character between the combining characters
    //and their letter.
    public int fixCursorStart(int start, String s, EditTypeWriter edittext)
    {
        if (start < s.length()) //doesn't matter if we're already at end of string
        {
            char charToRight = s.charAt(start);
            while (isCombiningCharacter(charToRight))
            {
                start++;
                edittext.setSelection(start);

                if (start + 1 <= s.length())
                {
                    charToRight = s.charAt(start);
                }
                else //we're at the end
                {
                    break;
                }
            }
            return start;
        }
        else
        {
            return start;
        }
    }

    private final OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {
        @Override public void onKey(int primaryCode, int[] keyCodes) {
            View focusCurrent = MainActivity.this.getWindow().getCurrentFocus();
            if( focusCurrent==null || focusCurrent.getClass()!=EditTypeWriter.class )
                return;
            EditTypeWriter edittext = (EditTypeWriter) focusCurrent;
            Editable editable = edittext.getText();
            //fix bug found by Chris SGI2019
            // where text can be entered after pressing enter
            if (!edittext.isEnabled())
            {
                return;
            }
            int start = edittext.getSelectionStart();

            // this will prevent inserting a char between combining accents
            String str2 = editable.toString();
            start = fixCursorStart(start, str2, edittext);

            if (allowVibrate) {
                vibrator.vibrate(KEYPRESS_VIBRATE);
            }  else if( primaryCode == 1 ) {
                editable.insert(start, "α");
            }  else if( primaryCode == 2 ) {
                editable.insert(start, "β");
            }  else if( primaryCode == 3 ) {
                editable.insert(start, "γ");
            }  else if( primaryCode == 4 ) {
                editable.insert(start, "δ");
            }  else if( primaryCode == 5 ) {
                editable.insert(start, "ε");
            }  else if( primaryCode == 6 ) {
                editable.insert(start, "ζ");
            }  else if( primaryCode == 7 ) {
                editable.insert(start, "η");
            }  else if( primaryCode == 8 ) {
                editable.insert(start, "θ");
            }  else if( primaryCode == 9 ) {
                editable.insert(start, "ι");
            }  else if( primaryCode == 10 ) {
                editable.insert(start, "κ");
            }  else if( primaryCode == 11) {
                editable.insert(start, "λ");
            }  else if( primaryCode == 12 ) {
                editable.insert(start, "μ");
            }  else if( primaryCode == 13 ) {
                editable.insert(start, "ν");
            }  else if( primaryCode == 14 ) {
                editable.insert(start, "ξ");
            }  else if( primaryCode == 15 ) {
                editable.insert(start, "ο");
            }  else if( primaryCode == 16 ) {
                editable.insert(start, "π");
            }  else if( primaryCode == 17 ) {
                editable.insert(start, "ρ");
            }  else if( primaryCode == 18 ) {
                editable.insert(start, "σ");
            }  else if( primaryCode == 19 ) {
                editable.insert(start, "τ");
            }  else if( primaryCode == 20 ) {
                editable.insert(start, "υ");
            }  else if( primaryCode == 21 ) {
                editable.insert(start, "φ");
            }  else if( primaryCode == 22 ) {
                editable.insert(start, "χ");
            }  else if( primaryCode == 23 ) {
                editable.insert(start, "ψ");
            }  else if( primaryCode == 24 ) {
                editable.insert(start, "ω");
            }  else if( primaryCode == 25 ) {
                editable.insert(start, "ς");
            }  else if( primaryCode == 26 ) { //parentheses
                localAccentLetter(editable, start, SURROUNDING_PARENTHESES);
            } else if( primaryCode == 27 ) { //rough breathing
                localAccentLetter(editable, start, ROUGH_BREATHING);
            } else if( primaryCode == 28 ) { //smooth breathing
                localAccentLetter(editable, start, SMOOTH_BREATHING);
            } else if( primaryCode == 29 ) { //acute
                localAccentLetter(editable, start, ACUTE);
            } else if( primaryCode == 30 ) { //circumflex
                localAccentLetter(editable, start, CIRCUMFLEX);
            } else if( primaryCode == 31 ) { //macron
                localAccentLetter(editable, start, MACRON);
            } else if( primaryCode == 32 ) { //iota subscript
                localAccentLetter(editable, start, IOTA_SUBSCRIPT);
            } else if( primaryCode == 33 ) { //MF
                onMFPressed(editable, start);
            } else if( primaryCode == 34 ) { //Enter
                onEnterPressed();
            } else if( primaryCode == 35 ) { //Delete
                if( start > 0 )
                {
                    int i = 0;
                    while (isCombiningCharacter(str2.charAt(start - i - 1)) && start - i - 1 > 0) {
                        i++;
                    }
                    editable.delete(start - i - 1, start);
                }
            }
        }
        @Override public void onPress(int arg0) {}
        @Override public void onRelease(int primaryCode) {}
        @Override public void onText(CharSequence text) {}
        @Override public void swipeDown() {}
        @Override public void swipeLeft() {}
        @Override public void swipeRight() {}
        @Override public void swipeUp() {}
    };

    public void localAccentLetter(Editable editable, int start, int acc)
    {
        int maxSubstringForAccent = 7;
        String str2 = editable.toString();
        String sub;
        String accentedLetter = "";
        int i;

        //this seems like an odd way of doing this??
        //should determine combining diacritics + letter first and
        //only call addAccent once
        for (i = 1; i < maxSubstringForAccent; i++)
        {
            if (start - i < 0)
            {
                break;
            }
            sub = str2.substring(start - i, start);

            accentedLetter = gv1.addAccent(acc, sub);
            if (!accentedLetter.equals("")) {
                break;
            }
        }
        if (!accentedLetter.equals("")) {
            editable.replace(start - i, start, accentedLetter);
        }
    }

    public void openKeyboard(View v, Runnable onComplete) {
        if (mKeyboardView.getVisibility() == View.GONE) {

            Animation animation = AnimationUtils
                    .loadAnimation(MainActivity.this,
                            R.anim.slide_in_bottom);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.RESTART);
            animation.setInterpolator(new LinearInterpolator());
            mKeyboardView.showWithAnimation(animation, onComplete);

            mKeyboardView.setVisibility(View.VISIBLE);
            mKeyboardView.setEnabled(true);
            if (v != null) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            editText.setVisibility(View.VISIBLE);
        }
    }

    public void onHide(View v)
    {
        hideCustomKeyboard(null);
    }

    public void onOpen(View v)
    {
        editText.setVisibility(View.VISIBLE);
        openKeyboard(mainView, null);
    }

    public void hideCustomKeyboard(final Runnable onComplete) {
        //mKeyboardView.setVisibility(View.GONE);

        //InputMethodManager imm = (InputMethodManager) MainActivity.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_bottom);
        mKeyboardView.startAnimation(animation);
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
        if (onComplete != null)
            mHandler.postDelayed(onComplete, 500);
        //mKeyboardView.setVisibility(View.GONE);
        //mKeyboardView.invalidate();

        //if (mKeyboardView.getVisibility() == View.VISIBLE)
        //{
        //    Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_down_bottom);

        //    mKeyboardView.startAnimation(animation);
            /*
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.RESTART);
            animation.setInterpolator(new LinearInterpolator());

            //animation.start();
            //animation.setFillAfter(true);//this fixed freeze
            //mKeyboardView.hideKBWithAnimation(animation, onComplete);
            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub
                    Log.e("starta2", "1");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub
                    Log.e("repeata2", "1");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.e("enda2", "1");
                    mKeyboardView.setVisibility(View.GONE);
                    mKeyboardView.postDelayed(onComplete, 100);
                }
            });
            Log.e("startanimation", "0");
            mKeyboardView.startAnimation(animation);
            Log.e("startanimation", "1");

            mKeyboardView.setEnabled(false);
            mKeyboardView.setVisibility(View.GONE);
            */
    //    }

    }
/*
    public void showCustomKeyboard( View v ) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if( v!=null ) ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
*/
    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    public void resetHCGame()
    {
        lives = 3;
        scoreLabel.setText("0");
        if (gv1 != null)
        {
            gv1.score = 0;
        }
        verbSeqObj.vsReset();
        life1.setVisibility(View.VISIBLE);
        life2.setVisibility(View.VISIBLE);
        life3.setVisibility(View.VISIBLE);
        gameOverLabel.setVisibility(View.GONE);
    }

    public void onContinuePressed(View v)
    {
        if (allowVibrate) {
            vibrator.vibrate(KEYPRESS_VIBRATE);
        }
        if (isHCGame && lives == 0)
        {
            resetHCGame();
        }
        nextSeq3();
        continueButton.setText("Continue");
    }

    public void nextSeq3()
    {
        int state = verbSeqObj.vsNext(gv1, gv2);
        Log.e("abc", "Cleanup: seq: " + verbSeqObj.seq + ", res: " + state);
        if (state == VerbSequence.STATE_NEW)
            cleanUp(true);
        else //if (state == 2)
            cleanUp(false);
    }

    public void cleanUp(final Boolean reset) //if reset, then clear away answer rather than move it up
    {
        continueButton.setVisibility(View.INVISIBLE);
        //changedFormText.animateHideText(null);

        final Runnable fl5 = new Runnable() {
            @Override
            public void run() {
                flip();
            }
        };

        final Runnable fl4 = new Runnable() {
            public void run() {

                //http://stackoverflow.com/questions/19016674/android-translate-animation-permanently-move-view-to-new-position-using-animat
                int[] origLoc = new int[2];
                origFormText.getLocationInWindow(origLoc);
                int[] changedLoc = new int[2];
                changedFormText.getLocationInWindow(changedLoc);

                int[] editLoc = new int[2];
                TranslateAnimation animation;

                //Move up from EDIT
                if (changedFormText.getVisibility() != View.VISIBLE) {
                    editText.getLocationInWindow(editLoc);

                    animation = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.ABSOLUTE, editLoc[1] - changedLoc[1],
                            Animation.ABSOLUTE, origLoc[1] - changedLoc[1] );

                    animation.setDuration(400);
                    //Log.e("abc", "Loc1: " + origLoc[1] + "Loc2: " + changedLoc[1] + ", Loc3: " + editLoc[1]);
                    changedFormText.setTextWithPadding(editText.getText().toString(), false); //here
                    changedFormText.setHeight(editText.getHeight());
                }
                else //Move up from CHANGED FORM
                {
                    animation = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.ABSOLUTE, origLoc[1] - changedLoc[1] );

                    animation.setDuration(500);
                }

                animation.setFillAfter(false);
                animation.setAnimationListener(new TranslateAnimation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        changedFormText.setVisibility(View.VISIBLE); //for if it was correct
                        editText.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        if (isDecomposedMode)
                        {
                            origFormText.setTextWithPadding(changedStr, false);
                            isDecomposedMode = false;
                        }
                        else {
                            origFormText.setTextWithPadding(changedFormText.getText().toString(), false); //setTextWithPadding()
                        }changedFormText.setVisibility(View.INVISIBLE); //this will "go" back to its own location
                        editText.setVisibility(View.INVISIBLE);
                        mHandler.postDelayed(fl5, 600);
                    }
                });

                changedFormText.startAnimation(animation);
            }
        };

        final Runnable fl3 = new Runnable() {
            public void run() {

                if (reset)
                    origFormText.animateHideText(fl5);
                else
                    origFormText.animateHideText(fl4);
            }
        };

        final Runnable fl2 = new Runnable() {
            public void run() {
                greenCheckRedX.setVisibility(View.GONE);
                stemText.animateHideText(fl3);
            }
        };

        final Runnable fl1 = new Runnable() {
            public void run() {
                editText.animateHideText(fl2);
            }
        };

        if (changedFormText.getVisibility() == View.VISIBLE) {
            if (reset)
                changedFormText.animateHideText(fl1);
            else
                mHandler.postDelayed(fl1, 100);
        }
        else
        {
            if (reset)
                editText.animateHideText(fl2);
            else
                mHandler.postDelayed(fl2, 1);
        }
    }

    public void quitPressed(View v) {
        if (isHCGame && lives > 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("Alert");

            dialog.setMessage("Are you sure you want to quit this game?");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //pressed Yes
                    if (front) {
                        stopTimer();
                        editText.setEnabled(false);
                        editText.passEvents = true;
                        gv1.compareFormsCheckMFRecordResult(editText.getText().toString(), changedFormText.getText().toString(), String.format("%.2f", elapsedTime), mMFPressed);
                    }
                    finish();

                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            });

            AlertDialog alert11 = dialog.create();
            alert11.show();
        }
        else
        {
            if (front) {
                stopTimer();
                editText.setEnabled(false);
                editText.passEvents = true;
                gv1.compareFormsCheckMFRecordResult(editText.getText().toString(), changedFormText.getText().toString(), String.format("%.2f", elapsedTime), mMFPressed);
            }
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        // do nothing
        quitPressed(null);
    }

    public void startTimer()
    {
        timerTimedOut = false;
        mTimeLabel.setTextColor(textColor);
        mStartTime = System.nanoTime();
        elapsedTime = 0;
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    public void stopTimer()
    {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    //I made this non-static so it can see my member variables here
    //https://medium.com/@ali.muzaffar/android-detecting-a-pinch-gesture-64a0a0ed4b41#.k0qw1qynj
    class MyPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (front) {
                return true;
            }
            if (detector.getScaleFactor() < 1)
            {
                //together
                if (isDecomposedMode) {
                    //centerTextView(origFormText, origStr, 0);
                    origFormText.setTextWithPadding(origStr, false);
                    //centerTextView(changedFormText, changedStr, R.id.editholder);
                    if (changedFormText.getVisibility() == View.VISIBLE)
                        changedFormText.setTextWithPadding(changedStr, false);//herehere
                    else
                        editText.setText(changedStr);
                    isDecomposedMode = false;
                }
            }
            else
            {
                //apart
                if (!isDecomposedMode) {
                    //centerTextView(origFormText, origStrDecomposed, 0);
                    origFormText.setTextWithPadding(origStrDecomposed, false);
                    //centerTextView(changedFormText, changedStrDecomposed, R.id.editholder);

                    if (changedFormText.getVisibility() == View.VISIBLE)
                        changedFormText.setTextWithPadding(changedStrDecomposed, false);
                    else
                        editText.setText(changedStrDecomposed);

                    isDecomposedMode = true;
                }
            }
            return true;
        }
    }

    int textColor = 0;
    int bgColor = 0;
    int timeIsOutColor = 0;

    public static void localSetTheme(Context context)
    {
        SharedPreferences sharedPref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String themeName = sharedPref.getString("HCTheme", "HCDayNight");
        switch(themeName)
        {
            case "HCDark":
                context.setTheme(R.style.HCDark);
                break;
            case "HCLight":
                context.setTheme(R.style.HCLight);
                break;
            default:
                context.setTheme(R.style.HCDayNight);
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MainActivity.localSetTheme(this);
        super.onCreate(savedInstanceState);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.hctextColor, typedValue, true);
        textColor = typedValue.data;
        theme.resolveAttribute(R.attr.hcbgColor, typedValue, true);
        bgColor = typedValue.data;
        theme.resolveAttribute(R.attr.hctimeIsOutColor, typedValue, true);
        timeIsOutColor = typedValue.data;

        //https://stackoverflow.com/questions/4616095/how-to-get-the-build-version-number-of-your-android-application
        osInfo = "Android: " + android.os.Build.VERSION.RELEASE + " (" + android.os.Build.VERSION.INCREMENTAL + ")";
        osInfo += " (API: " + android.os.Build.VERSION.SDK_INT + ")";
        osInfo += " Device: " + android.os.Build.DEVICE;
        osInfo += " Model (and Product): " + Build.MANUFACTURER + " - " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenSize = size.y + " x " + size.x;
        uniqueDeviceID = uid(getApplicationContext());

        //http://stackoverflow.com/questions/9627774/android-allow-portrait-and-landscape-for-tablets-but-force-portrait-on-phone/39302787#39302787
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.main_activity);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        origFormText = (TypeWriter) findViewById(R.id.origTextView);
        stemText = (TypeWriter) findViewById(R.id.stemTextView);
        changedFormText = (TypeWriter) findViewById(R.id.changedTextView);
        mTimeLabel = (TextView) findViewById(R.id.mTimeLabel);
        editText = (EditTypeWriter) findViewById(R.id.editText);
        continueButton = (Button) findViewById(R.id.continueButton);
        mMFLabelView = (TextView) findViewById(R.id.mfpressed);
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        greenCheckRedX = (ImageView) findViewById(R.id.greenCheckRedX);
        life1 = (ImageView) findViewById(R.id.life1);
        life2 = (ImageView) findViewById(R.id.life2);
        life3 = (ImageView) findViewById(R.id.life3);
        gameOverLabel = (TextView) findViewById(R.id.GameOverLabel);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mUnits = new boolean[mNumUnits];
        mUnits[0] = sharedPref.getBoolean("unit1", true);
        mUnits[1] = sharedPref.getBoolean("unit2", false);
        mUnits[2] = sharedPref.getBoolean("unit3", false);
        mUnits[3] = sharedPref.getBoolean("unit4", false);
        mUnits[4] = sharedPref.getBoolean("unit5", false);
        mUnits[5] = sharedPref.getBoolean("unit6", false);
        mUnits[6] = sharedPref.getBoolean("unit7", false);
        mUnits[7] = sharedPref.getBoolean("unit8", false);
        mUnits[8] = sharedPref.getBoolean("unit9", false);
        mUnits[9] = sharedPref.getBoolean("unit10", false);
        mUnits[10] = sharedPref.getBoolean("unit11", false);
        mUnits[11] = sharedPref.getBoolean("unit12", false);
        mUnits[12] = sharedPref.getBoolean("unit13", false);
        mUnits[13] = sharedPref.getBoolean("unit14", false);
        mUnits[14] = sharedPref.getBoolean("unit15", false);
        mUnits[15] = sharedPref.getBoolean("unit16", false);
        mUnits[16] = sharedPref.getBoolean("unit17", false);
        mUnits[17] = sharedPref.getBoolean("unit18", false);
        mUnits[18] = sharedPref.getBoolean("unit19", false);
        mUnits[19] = sharedPref.getBoolean("unit20", false);

        front = false;
        HCTime = 30;
        Intent intent = getIntent();
        String message = intent.getStringExtra(MenuActivity.EXTRA_MESSAGE);
        if (message.equals("play")) {
            isHCGame = true;
            scoreLabel.setVisibility(View.VISIBLE);
            mTimerCountDown = true;
            lives = 3;
            mTimeLabel.setText("30.00 sec");
        } else {
            scoreLabel.setVisibility(View.INVISIBLE);
            isHCGame = false;
            mTimerCountDown = false;
            lives = 0;
            mTimeLabel.setText("0.00 sec");
        }

        verbSeqObj = new VerbSequence();
        verbSeqObj.setupUnits(mUnits, isHCGame);

        if (isHCGame) {
            resetHCGame();
        }
        verbSeqObj.vsReset();
        HCDBHelperNew dbh = HCDBHelperNew.getInstance(getApplicationContext());
        String datafile = dbh.dbpath;
        int res = verbSeqObj.vsInit( datafile );
        if (res != 0) {
            Log.e("hoplite", "vsInit result: " + res);
            origFormText.setText("Error Code: HC" + res);
            return;
        }
        v1 = new Verb();
        v2 = new Verb();

        gv1 = new GreekVerb();
        gv1.score = 0;
        gv1.verb = v1;
        gv2 = new GreekVerb();
        gv2.verb = v2;

        mHandler = new Handler();

        editText.setEnabled(false);
        editText.passEvents = true;

        mainView = findViewById(R.id.myView2);
        mainView.setBackgroundColor(bgColor);

        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/newathu405.ttf");
        int greekFontSize = 32;
        int stemFontSize = 24;

        origFormText.setTypeface(type);
        changedFormText.setTypeface(type);
        editText.setTypeface(type);
        editText.setTextSize(greekFontSize);
        editText.setEnabled(false);
        editText.passEvents = true;
        origFormText.setTextSize(greekFontSize);
        stemText.setTextSize(stemFontSize);
        changedFormText.setTextSize(greekFontSize);
        mTimeLabel.setTextSize(24);
        origFormText.setGravity(Gravity.CENTER_HORIZONTAL);


        // or https://github.com/chrisboyle/sgtpuzzles/blob/master/app/src/main/java/name/boyle/chris/sgtpuzzles/SmallKeyboard.java

        //http://stackoverflow.com/questions/25786272/how-to-display-custom-keyboard-when-clicking-on-edittext-in-android
        //http://www.fampennings.nl/maarten/android/09keyboard/index.htm
        Keyboard mKeyboard = new Keyboard(this, R.xml.greekkeyboard);
        //draw custom keys:
        // http://stackoverflow.com/questions/18224520/how-to-set-different-background-of-keys-for-android-custom-keyboard
        // Lookup the KeyboardView
        mKeyboardView = (GreekKeyboard) findViewById(R.id.keyboardview);
        // Attach the keyboard to the view
        mKeyboardView.setKeyboard(mKeyboard);
        // Do not show the preview balloons
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);

        //set up timer
        //http://android-developers.blogspot.com/2007/11/stitch-in-time.html
        mUpdateTimeTask = new Runnable() {
            public void run() {
                double difference = 0.0;
                difference = (System.nanoTime() - mStartTime) / 1e6 / 1000;
                elapsedTime = difference;
                if (mTimerCountDown) {
                    difference = HCTime - difference;
                }
                mTimeLabel.setText(String.format("%.2f sec", difference));

                //mHandler.postAtTime(this, 1000);//start + (( seconds + 1) * 1000));
                if (difference > 0 || !mTimerCountDown) {
                    mHandler.postDelayed(mUpdateTimeTask, 50);
                    //mHandler.postAtTime(mUpdateTimeTask, System.currentTimeMillis() + 1000 );
                } else {
                    mTimeLabel.setTextColor(timeIsOutColor); //make red
                    stopTimer();
                    mTimeLabel.setText("0.00 sec");
                    timerTimedOut = true;
                    editText.setEnabled(false);
                    editText.passEvents = true;
                    hideCustomKeyboard(null);
                    Runnable runCheckVerb = new Runnable() {
                        public void run() {
                            checkVerb();
                        }
                    };
                    mHandler.postDelayed(runCheckVerb, 1);//a tiny delay prevents freeze on api 21
                }
            }
        };

        final ScaleGestureDetector mScaleDetector =
                new ScaleGestureDetector(this, new MyPinchListener());
        mainView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                return true;
            }
        });

        mStartTimerRunnable = new Runnable() {
            public void run() {
                editText.setEnabled(true);
                editText.passEvents = false;
                editText.requestFocus();
                startTimer();
            }
        };

        mEnableKeyboard = new Runnable() {
            public void run() {
                openKeyboard(findViewById(R.id.myView2), mStartTimerRunnable);
            }
        };
        mShowStem = new Runnable() {
            public void run() {
                stemText.animateText(stemStr, false, mEnableKeyboard);
                mHandler.postDelayed(mEnableKeyboard, 700);
            }
        };
        mShowOrigForm = new Runnable() {
            public void run() {
                origFormText.animateText(origStr, false, mShowStem);
            }
        };

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int state = verbSeqObj.vsNext(gv1, gv2);
                flip();
            }
        }, 1000);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

/*
        //or http://www.lucazanini.eu/en/2013/android/updating-frequently-a-textview-inside-a-loop/

        //or http://stackoverflow.com/questions/27715602/android-countdowntimer-class-lagging-main-thread

        //http://developer.android.com/reference/android/os/CountDownTimer.html
        new CountDownTimer(30000, 10) {

            public void onTick(long millisUntilFinished) {
                mTimeLabel.setText("seconds remaining: " + millisUntilFinished / 10);
            }

            public void onFinish() {
                //mTextField.setText("done!");
            }
        }.start();
        */

    }

    private class SendDeviceDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String data = "";
            HttpsURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpsURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-Length", "" + params[1].getBytes().length);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                //https://stackoverflow.com/questions/16504527/how-to-do-an-https-post-from-android
                SSLContext sc;
                sc = SSLContext.getInstance("TLS");
                sc.init(null, null, new java.security.SecureRandom());
                httpURLConnection.setSSLSocketFactory(sc.getSocketFactory());

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.write(params[1].getBytes(StandardCharsets.UTF_8));
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
        }
    }
}
