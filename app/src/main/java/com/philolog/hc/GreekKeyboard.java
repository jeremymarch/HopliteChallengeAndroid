package com.philolog.hc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import androidx.core.content.ContextCompat;
import java.util.List;

/**
 * Created by jeremy on 3/27/16.
 * http://stackoverflow.com/questions/18224520/how-to-set-different-background-of-keys-for-android-custom-keyboard
 */
public class GreekKeyboard extends KeyboardView {

  public boolean mMFPressed = false;
  private final int keyTextColor;
  private final int keyTextColorDown;
  private final int diacriticTextColor;
  private final int diacriticTextColorDown;
  private final int enterTextColor;
  private final int enterTextColorDown;
  private final int keyboardBgColor;
  private final int mfTextColor;
  private final int mfTextColorDown;

  private final Paint mPaint;
  private final Typeface mNewAthuTypeface;
  private final Typeface mDefaultTypeface;
  private final float mScale;

  private final Drawable mMFButtonDr;
  private final Drawable mMFButtonDownDr;
  private final Drawable mMFPressedDr;
  private final Drawable mMFPressedDownDr;
  private final Drawable mEnterButtonDr;
  private final Drawable mEnterButtonDownDr;
  private final Drawable mAccentButtonDr;
  private final Drawable mAccentButtonDownDr;
  private final Drawable mNormalButtonDr;
  private final Drawable mNormalButtonDownDr;
  private final Drawable mDeleteButtonDr;

  public GreekKeyboard(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedValue typedValue = new TypedValue();
    Resources.Theme theme = context.getTheme();
    theme.resolveAttribute(R.attr.keyTextColor, typedValue, true);
    keyTextColor = typedValue.data;
    theme.resolveAttribute(R.attr.keyTextColorDown, typedValue, true);
    keyTextColorDown = typedValue.data;
    theme.resolveAttribute(R.attr.diacriticTextColor, typedValue, true);
    diacriticTextColor = typedValue.data;
    theme.resolveAttribute(R.attr.diacriticTextColorDown, typedValue, true);
    diacriticTextColorDown = typedValue.data;
    theme.resolveAttribute(R.attr.enterTextColor, typedValue, true);
    enterTextColor = typedValue.data;
    theme.resolveAttribute(R.attr.enterTextColorDown, typedValue, true);
    enterTextColorDown = typedValue.data;
    theme.resolveAttribute(R.attr.keyboardBgColor, typedValue, true);
    keyboardBgColor = typedValue.data;

    theme.resolveAttribute(R.attr.hcMFBtnTextColor, typedValue, true);
    mfTextColor = typedValue.data;
    theme.resolveAttribute(R.attr.hcMFBtnTextColorDown, typedValue, true);
    mfTextColorDown = typedValue.data;

    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setFakeBoldText(true);
    mPaint.setTextAlign(Paint.Align.CENTER);

    mNewAthuTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/newathu405.ttf");
    mDefaultTypeface = Typeface.DEFAULT;

    mScale = context.getResources().getDisplayMetrics().density;

    mMFButtonDr = ContextCompat.getDrawable(context, R.drawable.mfbutton);
    mMFButtonDownDr = ContextCompat.getDrawable(context, R.drawable.mfbuttondown);
    mMFPressedDr = ContextCompat.getDrawable(context, R.drawable.mfbuttondown);
    mMFPressedDownDr = ContextCompat.getDrawable(context, R.drawable.mfpresseddown);
    mEnterButtonDr = ContextCompat.getDrawable(context, R.drawable.enterbutton);
    mEnterButtonDownDr = ContextCompat.getDrawable(context, R.drawable.enterbuttondown);
    mAccentButtonDr = ContextCompat.getDrawable(context, R.drawable.accentbutton);
    mAccentButtonDownDr = ContextCompat.getDrawable(context, R.drawable.accentbuttondown);
    mNormalButtonDr = ContextCompat.getDrawable(context, R.drawable.normalbutton);
    mNormalButtonDownDr = ContextCompat.getDrawable(context, R.drawable.normalbuttondown);
    mDeleteButtonDr = ContextCompat.getDrawable(context, R.drawable.deletebutton);
  }

  // http://stackoverflow.com/questions/3972445/how-to-put-text-in-a-drawable
  @Override
  public void onDraw(Canvas canvas) {
    Keyboard keyboard = getKeyboard();
    if (keyboard == null) {
      return;
    }

    // background color:
    mPaint.setColor(keyboardBgColor);
    mPaint.setStyle(Paint.Style.FILL); // fill the background with blue color
    canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

    List<Key> keys = keyboard.getKeys();
    for (Key key : keys) {
      Drawable dr;
      int primaryCode = key.codes[0];

      if (primaryCode == 33 && !mMFPressed) {
        if (key.pressed) {
          dr = mMFButtonDownDr;
          mPaint.setColor(mfTextColorDown);
        } else {
          dr = mMFButtonDr;
          mPaint.setColor(mfTextColor);
        }
        if (dr != null) {
          dr.setBounds(key.x, key.y + 6, key.x + key.width, key.y + key.height);
        }
      } else if (primaryCode == 33 && mMFPressed) {
        if (key.pressed) {
          dr = mMFPressedDownDr;
          mPaint.setColor(mfTextColor);
        } else {
          dr = mMFPressedDr;
          mPaint.setColor(mfTextColorDown);
        }
        if (dr != null) {
          dr.setBounds(key.x, key.y + 6, key.x + key.width, key.y + key.height);
        }
      } else if (primaryCode == 34) { // Enter
        if (key.pressed) {
          dr = mEnterButtonDownDr;
          mPaint.setColor(enterTextColorDown);
        } else {
          dr = mEnterButtonDr;
          mPaint.setColor(enterTextColor);
        }
        if (dr != null) {
          dr.setBounds(key.x, key.y + 6, key.x + key.width, key.y + key.height);
        }
      } else if (primaryCode > 26 && primaryCode < 33) { // diacritics
        if (key.pressed) {
          dr = mAccentButtonDownDr;
          mPaint.setColor(diacriticTextColorDown);
        } else {
          dr = mAccentButtonDr;
          mPaint.setColor(diacriticTextColor);
        }
        if (dr != null) {
          dr.setBounds(key.x, key.y + 6, key.x + key.width, key.y + key.height);
        }
      } else if (primaryCode == 26) { // parentheses
        if (key.pressed) {
          dr = mAccentButtonDownDr;
          mPaint.setColor(diacriticTextColorDown);
        } else {
          dr = mAccentButtonDr;
          mPaint.setColor(diacriticTextColor);
        }
        if (dr != null) {
          dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        }
      } else if (primaryCode == 35) { // delete
        if (key.pressed) {
          dr = mNormalButtonDownDr;
          mPaint.setColor(keyTextColorDown);
        } else {
          dr = mDeleteButtonDr;
          mPaint.setColor(keyTextColorDown);
        }
        if (dr != null) {
          dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        }
      } else {
        if (key.pressed) {
          dr = mNormalButtonDownDr;
          mPaint.setColor(keyTextColorDown);
        } else {
          dr = mNormalButtonDr;
          mPaint.setColor(keyTextColor);
        }
        if (dr != null) {
          dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        }
      }

      if (dr != null) {
        dr.draw(canvas);
      }
      float FONT_SIZE;
      // Convert the dips to pixels
      if (primaryCode == 28 || primaryCode == 27) {
        FONT_SIZE = 38.0f; // or 26.0?
        mPaint.setTypeface(mNewAthuTypeface);
      } else if (primaryCode == 29) {
        FONT_SIZE = 44.0f; // or 26.0?
        mPaint.setTypeface(mNewAthuTypeface);
      } else if (primaryCode == 32) {
        FONT_SIZE = 32.0f; // or 26.0?
        mPaint.setTypeface(mNewAthuTypeface);
      } else if (primaryCode == 33 && mMFPressed) {
        FONT_SIZE = 32.0f; // or 26.0?
        mPaint.setTypeface(mNewAthuTypeface);
      } else {
        FONT_SIZE = 23.0f; // or 26.0?
        mPaint.setTypeface(mDefaultTypeface);
      }

      final int fontSizeInPx = (int) (FONT_SIZE * mScale + 0.5f);
      mPaint.setTextSize(fontSizeInPx); // was 72px

      mPaint.setStyle(Paint.Style.FILL);

      String s;
      int offset = 0;
      if (key.label != null) {
        if (primaryCode == 27) {
          s = "῾";
          offset = 18;
        } else if (primaryCode == 28) {
          s = "᾿";
          offset = 18;
        } else if (primaryCode == 29) {
          s = "´"; // "´";
          offset = 17;
        } else if (primaryCode == 30) {
          s = key.label.toString();
          offset = 2;
        } else if (primaryCode == 31) {
          s = "—"; // key.label.toString();
          offset = 2;
        } else if (primaryCode == 32) {
          s = "ι"; // "ι";//"ͺ";
          offset = 16;
        } else if (primaryCode == 33 && mMFPressed) {
          s = ",";
          offset = 5;
        } else {
          s = key.label.toString();
          offset = 9;
        }
        offset = (int) (offset * mScale + 0.5f); // convert dp to px
        canvas.drawText(s, key.x + (key.width / 2), key.y + (key.height / 2) + offset, mPaint);
      } else if (key.icon != null) {
        key.icon.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        key.icon.draw(canvas);
      }
    }
  }

  public void showWithAnimation(Animation animation, final Runnable onComplete) {
    animation.setAnimationListener(
        new AnimationListener() {

          @Override
          public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onAnimationEnd(Animation animation) {
            // setVisibility(View.VISIBLE);
            postDelayed(onComplete, 200);
          }
        });

    setAnimation(animation);
  }

  public void hideKBWithAnimation(Animation animation, final Runnable onComplete) {}
}
