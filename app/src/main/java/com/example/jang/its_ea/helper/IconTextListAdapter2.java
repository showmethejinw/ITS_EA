package com.example.jang.its_ea.helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.jang.its_ea.IconTextView;
import com.example.jang.its_ea.IconTextView2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jang on 2016-12-10.
 */

public class IconTextListAdapter2 extends BaseAdapter {

    private Context mContext;

    private List<IconTextItem2> mItems = new ArrayList<IconTextItem2>();

    public IconTextListAdapter2(Context context) {
        mContext = context;
    }

    public void addItem(IconTextItem2 it1,IconTextItem2 it2,IconTextItem2 it3) {
        mItems.add(it1);
        mItems.add(it2);
        mItems.add(it3);
    }
    public void addItem(IconTextItem2 it1)
    {

        mItems.add(it1);
    }

    public void setListItems(List<IconTextItem2> lit) {
        mItems = lit;
    }

    public int getCount() {
        return mItems.size();
    }

    public Object getItem(int position) {
        return mItems.get(position);
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isSelectable(int position) {
        try {
            return mItems.get(position).isSelectable();
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        IconTextView2 itemView;
        if (convertView == null) {
            itemView = new IconTextView2(mContext, mItems.get(position));
        } else {
            itemView = (IconTextView2) convertView;


            itemView.setText(0, mItems.get(position).getData(0));
            itemView.setText(1, mItems.get(position).getData(1));
            itemView.setText(2, mItems.get(position).getData(2));

        }

        return itemView;
    }

    public void clear()
    {
        mItems.clear();
    }

}