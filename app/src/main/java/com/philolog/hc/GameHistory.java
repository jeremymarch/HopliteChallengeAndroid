package com.philolog.hc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.ArrayAdapter;
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
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class GameHistory extends ListActivity {
    public final static String EXTRA_MESSAGE = "com.philolog.hc.MESSAGE";
    private ArrayList<String> results = new ArrayList<String>();
    private MyCustomAdapter mAdapter;
    private SQLiteDatabase newDB;
    private ListView lv;
    private boolean isHCGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        Log.e("abc", "loaded game history");
        mAdapter = new MyCustomAdapter();

        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("GameOrPractice");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("GameOrPractice");
        }
        if (newString.contentEquals("game"))
        {
            isHCGame = true;
        }
        else
        {
            isHCGame = false;
        }



        setContentView(R.layout.game_history);
        openAndQueryDatabase();

        displayResultList();

        lv = getListView();
        lv.setOnItemClickListener(new OnItemClickListener()
        {
            //http://stackoverflow.com/questions/7645880/listview-with-onitemclicklistener-android
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {

                //Toast.makeText(SuggestionActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                Log.e("abc", "Click: " + position);
                ViewHolder vh = (ViewHolder)arg1.getTag();
                Integer gameID = vh.gameID;

                Intent intent = new Intent(getApplicationContext(), PracticeHistory.class);
                //EditText editText = (EditText) findViewById(R.id.edit_message);
                //String message = "practice";//editText.getText().toString();
                intent.putExtra("GameID", gameID);
                if (isHCGame)
                {
                    intent.putExtra("GameOrPractice", "game");
                }
                else
                {
                    intent.putExtra("GameOrPractice", "practice");
                }
                //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                overridePendingTransition (0, 0);
                startActivity(intent);
            }
        });
        TextView title = (TextView) findViewById(R.id.GamesLabel);
        if (isHCGame)
            title.setText("Games");
        else
            title.setText("Practice");
    }

    private void displayResultList() {
        /*
        TextView tView = new TextView(this);
        tView.setBackgroundResource(R.color.ButtonDarkBlue);
        tView.setTextColor(0xFFFFFFFF);
        tView.setText(" Games");
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

    private void openAndQueryDatabase() {
        try {
            String whereClause = "";
            if (isHCGame)
            {
                whereClause = " WHERE score != -1 ";
            }
            else
            {
                whereClause = " WHERE score == -1 ";
            }

            HCDBHelperNew dbHelper = HCDBHelperNew.getInstance(getApplicationContext());
            newDB = dbHelper.getReadableDatabase();//.getWritableDatabase();
            Cursor c = newDB.rawQuery("SELECT gameid, timest, score FROM games " +
                    whereClause + " ORDER BY gameid DESC LIMIT 1000", null);

            Log.e("abc", "Row count: " + c.getCount());

            if (c != null ) {
                if (c.moveToFirst()) {
                    do {
                        Log.e("abc", "a");
                        String timest = c.getString(c.getColumnIndex("timest"));
                        long timestamp = Long.parseLong(timest) * 1000L;
                        timest = getDate(timestamp );

                        String score = c.getString(c.getColumnIndex("score"));
                        String gameid = c.getString(c.getColumnIndex("gameid"));
                        results.add(timest + ", Score: " + score);
                        gamesHistory g = new gamesHistory();
                        g.date = timest;
                        g.score = score;
                        g.gameID = Integer.parseInt(gameid);
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

        private ArrayList<gamesHistory> mData = new ArrayList<gamesHistory>();
        //private ArrayList mData = new ArrayList();
        private LayoutInflater mInflater;

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final gamesHistory item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public gamesHistory getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println("getView " + position + " " + convertView);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.gameshistoryitem, null);
                holder = new ViewHolder();
                holder.textView = (TextView)convertView.findViewById(R.id.gamedate);
                holder.textView2 = (TextView) convertView.findViewById(R.id.score);

                holder.gameID = mData.get(position).gameID;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.textView.setText(mData.get(position).date);
        if (isHCGame)
        {
            holder.textView2.setText(mData.get(position).score);
        }
        else
        {
            holder.textView2.setText("");
        }

            return convertView;
        }
    }

    private class gamesHistory {
        String date;
        String score;
        Integer gameID;
    }

    public static class ViewHolder {
        public TextView textView;
        public TextView textView2;
        public Integer gameID;
    }
}