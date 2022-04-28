package com.philolog.hc;

import android.app.ListActivity;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;
import android.view.MotionEvent;
import android.os.Handler;
import android.view.Gravity;

public class VerbForms extends ListActivity {
    public static final int INDICATIVE  = 0;
    public static final int SUBJUNCTIVE = 1;
    public static final int OPTATIVE    = 2;
    public static final int IMPERATIVE  = 3;
    public static final int INFINITIVE  = 4;
    public static final int PARTICIPLE  = 5;

    public static final int PRESENT     = 0;
    public static final int IMPERFECT   = 1;
    public static final int FUTURE      = 2;
    public static final int AORIST      = 3;
    public static final int PERFECT     = 4;
    public static final int PLUPERFECT  = 5;

    public static final int ACTIVE      = 0;
    public static final int MIDDLE      = 1;
    public static final int PASSIVE     = 2;

    public static final int NUM_TENSES  = 6;
    public static final int NUM_VOICES  = 3;
    public static final int NUM_MOODS   = 4;
    public static final int NUM_NUMBERS = 2;
    public static final int NUM_PERSONS = 3;


    public static final int NOT_DEPONENT      = 0;
    public static final int MIDDLE_DEPONENT   = 1;
    public static final int PASSIVE_DEPONENT  = 2;
    public static final int PARTIAL_DEPONENT  = 3;
    public static final int DEPONENT_GIGNOMAI = 4; //see H&Q page 382


    public static final String[] tenses = {"Present", "Imperfect", "Future", "Aorist", "Perfect", "Pluperfect"};
    public static final String[] voices = { "Active", "Middle", "Passive" };
    public static final String[] moods = { "Indicative", "Subjunctive", "Optative", "Imperative" };

    //http://stacktips.com/tutorials/android/listview-with-section-header-in-android
    private HeaderListAdapter mAdapter;
    private int verbID;
    private boolean isDecomposedMode = false;
    private ScaleGestureDetector mScaleDetector;
    private Handler handlerTimer;
    private Boolean mBlock = true;
    private int ppTextColor = 0;
    //I made this non-static so it can see my member variables here
    //https://medium.com/@ali.muzaffar/android-detecting-a-pinch-gesture-64a0a0ed4b41#.k0qw1qynj
    private class MyPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
//Log.e("abc", "Scale End");
            //getListView().setScrollContainer(true);
            getListView().setEnabled(true);
        }
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            //mBlock = true;
            //getListView().setScrollContainer(false);
            getListView().setEnabled(false);

                handlerTimer.postDelayed(new Runnable(){
                    public void run() {
                        // do something
                        //Log.e("abc", "runnnnn");
                        getListView().setEnabled(true);
                        mBlock = false;
                        //getListView().setScrollContainer(true);
                    }}, 200);
                //}

            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //Log.e("abc", "pinch: " + detector.getScaleFactor());

            if (detector.getScaleFactor() < 1)
            {
                //Log.e("abc", "pinch together");
                //together
                /*if (!mBlock) {
                    getListView().setEnabled(false);
                    mBlock = true;
*/
                    if (isDecomposedMode) {

                        isDecomposedMode = false;
                        //mAdapter.clearAll();
                        //getListView().setEnabled(false);
                        loadList(true);
/*
                        handlerTimer.postDelayed(new Runnable(){
                            public void run() {
                                // do something
                                Log.e("abc", "runnnnn");
                                getListView().setEnabled(true);
                                mBlock = false;
                                //getListView().setScrollContainer(true);
                            }}, 2000); */
                    //}
                }
            }
            else
            {
                //apart
                //Log.e("abc", "apart");
                /*
                if (!mBlock) {
                    getListView().setEnabled(false);
                    mBlock = true;
*/
                    if (!isDecomposedMode) {

                        isDecomposedMode = true;
                        //mAdapter.clearAll();
                        //getListView().setEnabled(false);
                        loadList(true);
/*
                        handlerTimer.postDelayed(new Runnable(){
                            public void run() {
                                // do something
                                Log.e("abc", "runnnnn");
                                getListView().setEnabled(true);
                                mBlock = false;
                                //getListView().setScrollContainer(true);
                            }}, 2000);*/
                    //}
                }
                /*
                handlerTimer.postDelayed(new Runnable(){
                    public void run() {
                        // do something
                        Log.e("abc", "runnnnn");
                        getListView().setEnabled(true);
                        mBlock = false;
                        //getListView().setScrollContainer(true);
                    }}, 200);
                //}
                */
            }

            //getListView().setEnabled(true);
            getListView().invalidate();
            return true;
        }
    }
/*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if(ev.getAction()==MotionEvent.ACTION_MOVE && mBlock)
            return true;
        return super.dispatchTouchEvent(ev);
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.localSetTheme(this);
        super.onCreate(savedInstanceState);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.hctextColor, typedValue, true);
        ppTextColor = typedValue.data;

        if(getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        Log.e("abc", "loaded verb forms");
        handlerTimer = new Handler();

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            verbID = extras.getInt("VerbID");
            System.out.println("VerbID = " + verbID);
        }

        mAdapter = new HeaderListAdapter(VerbForms.this, 1);

        setContentView(R.layout.verblist);
        getListView().setDivider(null);

        loadList(false);

        displayResultList();

        //mScaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());

        final ScaleGestureDetector mScaleDetector =
                new ScaleGestureDetector(this, new MyPinchListener());
        getListView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    private String commaToOr(String s)
    {
        if (s.isEmpty())
            return "—";
        else
            return s.replace(",", " or");
    }

    private void displayResultList() {

        final Typeface type = Typeface.createFromAsset(getAssets(),"fonts/newathu405.ttf");
        Verb ve = new Verb();
        ve.getVerb(verbID);
        String l;
        l = commaToOr(ve.present) + ", " + commaToOr(ve.future) + ", " + commaToOr(ve.aorist) + ", " + commaToOr(ve.perfect) + ", " + commaToOr(ve.perfmid) + ", " + commaToOr(ve.aoristpass);

        TextView title = (TextView) findViewById(R.id.VerbListTitle);
        title.setTypeface(type);
        title.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        title.setText(ve.present);

        //text view for principal parts
        TextView tView = new TextView(this);
        tView.setPadding(20,10,10,10);
        tView.setTextColor(ppTextColor);

        tView.setTypeface(type);
        tView.setTextSize(24);
        tView.setText(l);
        getListView().addHeaderView(tView);

        setListAdapter(mAdapter);
    }

    private void loadList(Boolean update) {

        Verb ve = new Verb();
        GreekVerb vf = new GreekVerb();
        ve.getVerb(verbID);
        vf.verb = ve;
        vf.verbid = verbID;
        int updateIndex = 0;

        boolean isOida = false;
        if (ve.present.equals("οἶδα") || ve.present.equals("σύνοιδα")) {
            isOida = true;
        }

        for (int t = 0; t < NUM_TENSES; t++) {
            vf.tense = t;
            for (int v = 0; v < NUM_VOICES; v++) {
                vf.voice = v;
                for (int m = 0; m < NUM_MOODS; m++) {
                    if (!isOida && ((m != INDICATIVE && (t == PERFECT || t == PLUPERFECT || t == IMPERFECT) || ((m == SUBJUNCTIVE || m == IMPERATIVE) && t == FUTURE))))
                        continue;
                    else if (isOida && (m != INDICATIVE && (t == PLUPERFECT || t == IMPERFECT)  || ((m == SUBJUNCTIVE || m == IMPERATIVE) && t == FUTURE)))
                        continue;

                    String s;
                    if (v == ACTIVE || t == AORIST || t == FUTURE)
                    {
                        s = tenses[t] + " " + voices[v] + " " + moods[m];
                    }
                    else if (v == MIDDLE)
                    {
                        //FIX ME, is this right?? how do we label these.
                        //yes it's correct, middle deponents do not have a passive voice.  H&Q page 316
                        int deponentType = ve.deponentType();
                        if (deponentType == MIDDLE_DEPONENT || deponentType == PASSIVE_DEPONENT || deponentType == DEPONENT_GIGNOMAI || ve.present.equals("κεῖμαι"))
                        {
                            s = tenses[t] + " Middle " + moods[m];
                        }
                        else
                        {
                            s =  tenses[t] + " Middle/Passive " + moods[m];
                        }
                    }
                    else
                    {
                        continue; //skip passive if middle+passive are the same
                    }
                    if (!update) {
                        mAdapter.addSectionHeaderItem(s);
                    }
                    updateIndex++;

                    vf.mood = m;
                    int countPerSection = 0;
                    for (int n = 0; n < NUM_NUMBERS; n++) {
                        for (int p = 0; p < NUM_PERSONS; p++) {

                            vf.number = n;
                            vf.person = p;

                            if (vf.mood == IMPERATIVE && vf.person == 0)
                            {
                                continue;
                            }

                            int mf = 0;
                            if (isDecomposedMode)
                                mf = 1;

                            String form = vf.getForm(1,mf);
                            form = form.replace(", ", "\n");
                            if (!form.isEmpty()) {
                                if (!update)
                                    mAdapter.addItem(new VerbListItem(verbID, (p+1) + ((n == 0) ? "s:" : "p:") + form));
                                else
                                    mAdapter.updateItem(updateIndex++, (p+1) + ((n == 0) ? "s:" : "p:") + form);
                                countPerSection++;
                            }
                        }
                    }
                    if (countPerSection == 0)
                    {
                        //remove section header
                        if (!update)
                            mAdapter.removeLastSectionHeaderItem();
                        else
                            updateIndex--;
                    }
                }
            }
        }
    }
}
