package com.philolog.hc;

import android.app.ListActivity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.view.View;
import android.content.Intent;

public class VerbList extends ListActivity {

    //http://stacktips.com/tutorials/android/listview-with-section-header-in-android
    private HeaderListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.localSetTheme(this);
        super.onCreate(savedInstanceState);

        if(getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mAdapter = new HeaderListAdapter(VerbList.this, 0);
        setContentView(R.layout.verblist);
        loadList();

        displayResultList();

        //ListView listview = this.getListView();
    }

    public void clearSearch(View v)
    {
        EditText s = findViewById(R.id.word_search);
        s.setText("");
    }

    private void displayResultList() {
/*
        TextView tView = new TextView(this);
        tView.setBackgroundResource(R.color.ButtonDarkBlue);
        tView.setTextColor(0xFFFFFFFF);
        tView.setText(" H&Q Verbs");
        tView.setTextSize(26);
        getListView().addHeaderView(tView);
*/
        setListAdapter(mAdapter);
    }

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
        if (mAdapter.getItemViewType(position) == HeaderListAdapter.TYPE_ITEM) {
            VerbListItem s = mAdapter.getItem(position);
            Log.e("abc", "Position clicked : " + String.valueOf(position) + ", " + s.id);

            Intent intent = new Intent(this, VerbForms.class);
            Bundle dataBundle = new Bundle();
            dataBundle.putInt("VerbID", s.id);
            intent.putExtras(dataBundle);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            overridePendingTransition (0, 0);
            startActivity(intent);

        }
    }

    private void loadList() {
        int unit = 2;
        for (int i = 0; i < 127; i++)
        {
            if (i == 0 || i == 4 || i == 8 || i == 12 || i == 16 || i == 20 || i == 23 || i == 25 || i == 29 || i == 35 || i == 42 || i == 52 || i == 60 || i == 73 || i == 83 || i == 92 || i == 99 || i == 110 || i == 120) {
                mAdapter.addSectionHeaderItem("Unit " + unit++);
            }
            Verb v = new Verb();
            v.getVerb(i);

            if (v.present.isEmpty())
                mAdapter.addItem(new VerbListItem(i, v.future)); //v.future);
            else
                mAdapter.addItem(new VerbListItem(i, v.present));
        }
    }

}
