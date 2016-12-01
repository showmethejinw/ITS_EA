package com.example.jang.its_ea;

/**
 * Created by jang on 2016-12-01.
 */


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jang.its_ea.helper.IconTextItem;

/**
 * 아이템으로 보여줄 뷰 정의
 *
 * @author Mike
 *
 */
public class IconTextView extends LinearLayout {

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

    /**
     * TextView 03
     */

    public IconTextView(Context context, IconTextItem aItem) {
        super(context);

        // Layout Inflation
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem, this, true);

        // Set Icon

        // Set Text 01
        mText01 = (TextView) findViewById(R.id.dataItem01);
        mText01.setText(aItem.getData(0));

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
            throw new IllegalArgumentException();
        }
    }

    /**
     * set Icon
     *
     * @param icon
     */


}
