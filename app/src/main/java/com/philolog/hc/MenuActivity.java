package com.philolog.hc;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.util.Log;

public class MenuActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.philolog.hc.MESSAGE";
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.localSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //http://stackoverflow.com/questions/9627774/android-allow-portrait-and-landscape-for-tablets-but-force-portrait-on-phone/39302787#39302787
        if(getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if ("HCTheme".equals(key)) {
                    recreate();
                }
            }
        };
        sharedPref.registerOnSharedPreferenceChangeListener(prefListener);

        //to be sure the db is created on first run
        HCDBHelperNew dbh = HCDBHelperNew.getInstance(getApplicationContext());
        dbh.getReadableDatabase();

        //to be sure db is setup before first game
        VerbSequence verbSeqObj = new VerbSequence();
        //verbSeqObj.setupUnits(mUnits, isHCGame);

        String datafile = dbh.dbpath;
        verbSeqObj.vsInit( datafile );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (prefListener != null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPref.unregisterOnSharedPreferenceChangeListener(prefListener);
        }
    }

    public void play(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, MainActivity.class);
        String message = "play";
        intent.putExtra(EXTRA_MESSAGE, message);
        overridePendingTransition (0, 0);
        startActivity(intent);
    }

    public void practice(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, MainActivity.class);
        String message = "practice";
        intent.putExtra(EXTRA_MESSAGE, message);
        overridePendingTransition (0, 0);
        startActivity(intent);
    }

    public void showPracticeHistory(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, GameHistory.class);
        String message = "practice";
        intent.putExtra("GameOrPractice", message);
        overridePendingTransition (0, 0);
        startActivity(intent);
    }

    public void showGameHistory(View view) {
        // Do something in response to button
        Log.e("abc", "show game history");
        Intent intent = new Intent(this, GameHistory.class);
        String message = "game";
        intent.putExtra("GameOrPractice", message);
        overridePendingTransition (0, 0);
        startActivity(intent);
    }

    public void showVerbList(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, VerbList.class);
        intent.putExtra("GameID", 1); //1 is the practice game.
        overridePendingTransition (0, 0);
        startActivity(intent);
    }

    public void settings(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void showAbout(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }
}
