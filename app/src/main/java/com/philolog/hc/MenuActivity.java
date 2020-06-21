package com.philolog.hc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;


public class MenuActivity extends Activity {
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
        SharedPreferences sharedPref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals("HCTheme")) {
                recreate();
            }
        };
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

    public void play(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, MainActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = "play";//editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition (0, 0);
        startActivity(intent);
        //finish();
    }

    public void practice(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, MainActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = "practice";//editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition (0, 0);
        startActivity(intent);
    }

    public void showPracticeHistory(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, GameHistory.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = "practice history";//editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        //intent.putExtra("GameID", 1); //1 is the practice game.
        String message = "practice";//editText.getText().toString();
        intent.putExtra("GameOrPractice", message);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition (0, 0);
        startActivity(intent);
    }

    public void showGameHistory(View view) {
        // Do something in response to button
        Log.e("abc", "show game history");
        Intent intent = new Intent(this, GameHistory.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = "game";//editText.getText().toString();
        intent.putExtra("GameOrPractice", message);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition (0, 0);
        startActivity(intent);
    }

    public void showVerbList(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, VerbList.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = "practice history";//editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra("GameID", 1); //1 is the practice game.
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition (0, 0);
        startActivity(intent);
    }

    public void settings(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, SettingsActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = "practice";//editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void showAbout(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, TutorialActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = "practice";//editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
