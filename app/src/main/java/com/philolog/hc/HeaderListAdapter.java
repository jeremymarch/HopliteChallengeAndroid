package com.philolog.hc;

import java.util.ArrayList;
import java.util.TreeSet;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by jeremy on 7/30/16.
 */
//http://stacktips.com/tutorials/android/listview-with-section-header-in-android
public class HeaderListAdapter extends BaseAdapter {

    public static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private int rowHeight = 0;
    private ArrayList mData = new ArrayList();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;
    private Context appContext;
    public HeaderListAdapter(Context context, int rowHeightp) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appContext = context;
        rowHeight = rowHeightp;
    }

    public void clearAll()
    {
        mData.clear();
        notifyDataSetChanged();
    }

    public void updateItem(final int i, final String str) {
        VerbListItem v = (VerbListItem)mData.get(i);
        v.verb = str;
        notifyDataSetChanged();
    }

    public void addItem(final VerbListItem item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    public void removeLastSectionHeaderItem() {
        mData.remove(mData.size() - 1 );
        sectionHeader.remove( ((TreeSet) sectionHeader).last() );
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public VerbListItem getItem(int position) {
        return (VerbListItem) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    if (rowHeight == 0) {
                        convertView = mInflater.inflate(R.layout.verblistitem, null);
                    }
                    else {
                        convertView = mInflater.inflate(R.layout.verbformitem, null);
                        holder.label = (TextView) convertView.findViewById(R.id.verbformlabel);
                        holder.label.setTextSize(20);
                    }
                    holder.textView = (TextView) convertView.findViewById(R.id.verbitem);


                    final Typeface type = Typeface.createFromAsset(appContext.getAssets(),"fonts/newathu405.ttf");
                    holder.textView.setTypeface(type);
                    holder.textView.setTextSize(26);

                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.verblistitemheader, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.verbitemheader);
                    holder.textView.setTextSize(18);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        switch (rowType) {
            case TYPE_ITEM:
                VerbListItem v = (VerbListItem) mData.get(position);
                if (rowHeight == 0) {
                    holder.textView.setText(v.verb);
                }
                else {
                    String pattern = "(\\d+.:)(.*)";
                    Pattern r = Pattern.compile(pattern, Pattern.DOTALL);
                    Matcher m = r.matcher(v.verb);
                    if (m.find( )) {
                        holder.textView.setText(m.group(2));
                        holder.label.setText(m.group(1));
                    }
                }
                break;
            case TYPE_SEPARATOR:
                holder.textView.setText((String)mData.get(position));
                break;
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
        public TextView label;
    }

}


