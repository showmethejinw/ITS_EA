package com.example.jang.its_ea;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jang.its_ea.helper.IconTextItem;
import com.example.jang.its_ea.helper.IconTextItem2;

/**
 * Created by jang on 2016-12-10.
 */

public class IconTextView2 extends LinearLayout {

    /**
     * Icon
     */

    /**
     * TextView 01
     */
    private TextView mText01;

    /**
     * TextView 02
     */
    private TextView mText02;
    /**
     * TextView 03
     */
    private TextView mText03;


    public IconTextView2(Context context, IconTextItem2 aItem) {
        super(context);

        // Layout Inflation
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem2, this, true);

        // Set Icon

        // Set Text 01
        mText01 = (TextView) findViewById(R.id.dataItem01);
        mText01.setText(aItem.getData(0));

        mText02 = (TextView) findViewById(R.id.dataItem02);
        mText02.setText(aItem.getData(1));

        mText03 = (TextView) findViewById(R.id.dataItem03);
        mText03.setText(aItem.getData(2));

        // Set Text 02

        // Set Text 03

    }

    /**
     * set Text
     *
     * @param index
     * @param data
     */
    public void setText(int index, String data) {
        if (index == 0) {
            mText01.setText(data);
        } else {
//            throw new IllegalArgumentException();
        }
        if (index == 1) {
            mText02.setText(data);
        } else {
//            throw new IllegalArgumentException();
        }

        if (index == 2) {
            mText03.setText(data);
        } else {
//            throw new IllegalArgumentException();
        }


    }

    /**
     * set Icon
     *
     * @param icon
     */


}

