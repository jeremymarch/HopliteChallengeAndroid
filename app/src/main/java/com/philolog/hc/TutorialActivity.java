package com.philolog.hc;

import android.content.pm.ActivityInfo;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.app.Activity;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.util.Log;
import java.util.List;
import java.util.ArrayList;
import android.webkit.WebView;

//http://stackoverflow.com/questions/34155294/showing-webview-in-viewpager
public class TutorialActivity extends Activity {
    private List<View> mListViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.localSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        if(getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.vpPager);

        mListViews = new ArrayList<View>();
        addView(mListViews, "file:///android_asset/tutorialTitlePage.html");
        addView(mListViews, "file:///android_asset/tutorialAcknowledgements.html");
        addView(mListViews, "file:///android_asset/tutorialGamePlay.html");
        addView(mListViews, "file:///android_asset/tutorialPractice.html");
        addView(mListViews, "file:///android_asset/tutorialKeyboard.html");
        addView(mListViews, "file:///android_asset/tutorialPinch.html");

        MyPagerAdapter myAdapter = new MyPagerAdapter();
        viewPager.setAdapter(myAdapter);

        //viewPager.setAdapter(new TutorialPagerAdapter(this));
    }

    private void addView(List<View> viewList,String url)
    {
        WebView webView=new WebView(this);
        webView.loadUrl(url);
        viewList.add(webView);
    }

    private class MyPagerAdapter extends PagerAdapter {
        public final String[] pages = {"Introduction", "Acknowledgements", "Game Play", "Practice Mode", "Keyboard", "Pinch to View Endings"};
        @Override
        public void destroyItem(@NonNull View arg0, int arg1, @NonNull Object arg2) {
            Log.d("k", "destroyItem");
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(@NonNull View arg0) {
            Log.d("k", "finishUpdate");
        }

        @Override
        public int getCount() {
            Log.d("k", "getCount");
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(@NonNull View arg0, int arg1) {
            Log.d("k", "instantiateItem");
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
            Log.d("k", "isViewFromObject");
            return arg0 == (arg1);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            //TutorialModelObject customPagerEnum = TutorialModelObject.values()[position];
            //fix me
            return pages[position];
        }
    }
}

