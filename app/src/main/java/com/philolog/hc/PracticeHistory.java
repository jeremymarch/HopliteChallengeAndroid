package com.philolog.hc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.text.Html;

public class PracticeHistory extends ListActivity {

    private ArrayList<String> results = new ArrayList<String>();
    private MyCustomAdapter mAdapter;
    private SQLiteDatabase newDB;
    private Integer gameid;
    private boolean isHCGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.localSetTheme(this);
        super.onCreate(savedInstanceState);

        if(getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        Log.e("abc", "loaded game history");
        mAdapter = new MyCustomAdapter();

        Intent intent = getIntent();
        gameid = intent.getIntExtra("GameID", 1);

        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString = extras.getString("GameOrPractice");
                gameid = extras.getInt("GameID");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("GameOrPractice");
            gameid = (Integer) savedInstanceState.getSerializable("GameID");
        }
        if (newString.contentEquals("game"))
        {
            isHCGame = true;
        }
        else
        {
            isHCGame = false;
        }

        setContentView(R.layout.activity_practice_history);
        openAndQueryDatabase(gameid);

        displayResultList();
    }

    private void displayResultList() {

        TextView title = (TextView) findViewById(R.id.GameHistoryTitle);
        if (isHCGame)
            title.setText("Game History");
        else
            title.setText("Practice History");

        /*
        TextView tView = new TextView(this);
        tView.setBackgroundResource(R.color.ButtonDarkBlue);
        tView.setTextColor(0xFFFFFFFF);
        if (gameid == 1)
            tView.setText(" Practice History");
        else
            tView.setText(" Game History");
        tView.setTextSize(28);
        getListView().addHeaderView(tView);
*/
        //setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, results));
        setListAdapter(mAdapter);
        //getListView().setTextFilterEnabled(true);

    }
    private String getDate(long timeStamp){

        try{
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "000-00-00";
        }
    }

    private void openAndQueryDatabase(Integer gameID) {
        try {
            HCDBHelperNew dbHelper = HCDBHelperNew.getInstance(getApplicationContext());
            newDB = dbHelper.getReadableDatabase();//.getWritableDatabase();
            Cursor c = newDB.rawQuery("SELECT person, number, tense, voice, mood, verbid, correct, elapsedtime, answerGiven FROM verbseq " +
                    " WHERE gameid=" + gameID.toString() + " ORDER BY id DESC LIMIT 1000", null);

            Log.e("abc", "Row count: " + c.getCount());

            if (c != null ) {
                if (c.moveToFirst()) {
                    do {
                        //Log.e("abc", "a");
                        String person = c.getString(c.getColumnIndex("person"));
                        String number = c.getString(c.getColumnIndex("number"));
                        String tense = c.getString(c.getColumnIndex("tense"));
                        String voice = c.getString(c.getColumnIndex("voice"));
                        String mood = c.getString(c.getColumnIndex("mood"));
                        String verbid = c.getString(c.getColumnIndex("verbid"));

                        String given = c.getString(c.getColumnIndex("answerGiven"));
                        String correct = c.getString(c.getColumnIndex("correct"));
                        String elapsedTime = c.getString(c.getColumnIndex("elapsedtime"));

                        GreekVerb gv = new GreekVerb();
                        Verb v = new Verb();
                        v.getVerb(Integer.parseInt(verbid));

                        gv.person = Integer.parseInt(person);
                        gv.number = Integer.parseInt(number);
                        gv.tense = Integer.parseInt(tense);
                        gv.voice = Integer.parseInt(voice);
                        gv.mood = Integer.parseInt(mood);
                        gv.verb = v;
                        gv.verbid = Integer.parseInt(verbid);

                        historyItem g = new historyItem();
                        g.stem = gv.getAbbrevDescription();
                        g.correct = gv.getForm(1,0);
                        g.given = given;
                        g.time = elapsedTime;
                        if (correct.equalsIgnoreCase("1"))
                            g.isCorrect = true;
                        else
                            g.isCorrect = false;

                        mAdapter.addItem(g);
                    }while (c.moveToNext());
                }
            }
        } catch (SQLiteException se ) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        } finally {
            if (newDB != null)
                //newDB.execSQL("DELETE FROM " + tableName);
                newDB.close();
        }

    }

    private class MyCustomAdapter extends BaseAdapter {

        private ArrayList<historyItem> mData = new ArrayList<historyItem>();
        //private ArrayList mData = new ArrayList();
        private LayoutInflater mInflater;

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final historyItem item) {
            //Log.e("Hoplite", item.given);
            mData.add(item);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {

            Log.e("Hoplite", "mdata size: " + mData.size());
            return mData.size();
        }

        @Override
        public historyItem getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public String makeBoldStem(String d1, String d2)
        {
            String[] d1Array = d1.split(" ");
            String[] d2Array = d2.split(" ");

            for (int i = 0; i < 5; i++) {
                if (!d1Array[i].equals(d2Array[i]))
                    d2Array[i] = "<b>" + d2Array[i] + "</b>";
            }
            //Log.d("abc", TextUtils.join(" ", d2Array));
            return TextUtils.join(" ", d2Array);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println("getView " + position + " " + convertView);

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.historylistitem, null);
                holder = new ViewHolder();
                holder.textView = (TextView)convertView.findViewById(R.id.stem);
                holder.textView2 = (TextView)convertView.findViewById(R.id.correct);
                Typeface type = Typeface.createFromAsset(getAssets(),"fonts/newathu405.ttf");
                holder.textView2.setTypeface(type);
                holder.textView3 = (TextView)convertView.findViewById(R.id.given);
                holder.textView3.setTypeface(type);
                holder.textView4 = (TextView)convertView.findViewById(R.id.time);

                holder.greenCheck = (ImageView)convertView.findViewById(R.id.greenCheck);
                //holder.redX = (ImageView)convertView.findViewById(R.id.redX);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            if ( mData.get(position).time.equals("(null)")) {
                holder.greenCheck.setVisibility(View.INVISIBLE);
                //holder.redX.setVisibility(View.INVISIBLE);
                //holder.textView4.setVisibility(View.INVISIBLE);
            }
            else
            {
                if (mData.get(position).isCorrect) {
                    holder.greenCheck.setImageResource(R.drawable.greencheck);
                    holder.greenCheck.setVisibility(View.VISIBLE);
                } else {
                    holder.greenCheck.setImageResource(R.drawable.redx);
                    holder.greenCheck.setVisibility(View.VISIBLE);
                    //holder.redX.setVisibility(View.VISIBLE);
                }
            }

            String stem = mData.get(position).stem;

            if (!mData.get(position).given.equals("START")) {

                if ( position + 1 < mData.size() ) {
                    String prevStem = mData.get(position + 1).stem;
                    stem = makeBoldStem(prevStem, stem);
                }

                holder.textView4.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.textView4.setVisibility(View.INVISIBLE);
            }

            holder.textView.setText(Html.fromHtml(stem));
            holder.textView2.setText(mData.get(position).correct);
            holder.textView3.setText(mData.get(position).given);
            holder.textView4.setText(mData.get(position).time);

            Log.e("abc", mData.get(position).time);
            return convertView;
        }

    }

    private class historyItem {
        String stem;
        String correct;
        String given;
        String time;
        Boolean isCorrect;
    }

    public static class ViewHolder {
        public TextView textView;
        public TextView textView2;
        public TextView textView3;
        public TextView textView4;
        public ImageView redX;
        public ImageView greenCheck;
    }
}