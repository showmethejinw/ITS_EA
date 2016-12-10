package com.example.jang.its_ea.helper;

import android.graphics.drawable.Drawable;

/**
 * Created by jang on 2016-12-10.
 */

public class IconTextItem2 {

    /**
     * Icon
     */
    private Drawable mIcon;

    /**
     * Data array
     */
    private String[] mData;

    /**
     * True if this item is selectable
     */
    private boolean mSelectable = true;


    /**
     * Initialize with icon and data array
     *
     * @param str1
     * @param str2
     * @param str3
     */
    public IconTextItem2(String str1, String str2, String str3)
    {
        mData = new String[3];
        mData[0] = str1;
        mData[1] = str2;
        mData[2] = str3;
//        str.toString();
        ;
    }

    /**
     * Initialize with icon and data array
     *
     * @param obj
     */

    public IconTextItem2 (String[] obj) {
        mData = obj;
    }

    public IconTextItem2(String str)
    {
        mData = new String[1];
        mData[0] = str;
    }

    /**
     * Initialize with icon and strings
     *
     * @param icon
     * @param obj01
     * @param obj02
     * @param obj03
     */
    public IconTextItem2 (Drawable icon, String obj01, String obj02, String obj03) {

        mData = new String[1];
        mData[0] = obj01;
    }

    /**
     * True if this item is selectable
     */
    public boolean isSelectable() {
        return mSelectable;
    }

    /**
     * Set selectable flag
     */
    public void setSelectable(boolean selectable) {
        mSelectable = selectable;
    }

    /**
     * Get data array
     *
     * @return
     */
    public String[] getData() {
        return mData;
    }

    /**
     * Get data
     */
    public String getData(int index) {
        if (mData == null || index >= mData.length) {
            return null;
        }

        return mData[index];
    }

    /**
     * Set data array
     *
     * @param obj
     */
    public void setData(String[] obj) {
        mData = obj;
    }

    /**
     * Set icon
     *
     * @param icon
     */

    /**
     * Get icon
     *
     * @return
     */
    public Drawable getIcon() {
        return mIcon;
    }

    /**
     * Compare with the input object
     *
     * @param other
     * @return
     */
    public int compareTo(IconTextItem other) {
        if (mData != null) {
            String[] otherData = other.getData();
            if (mData.length == otherData.length) {
                for (int i = 0; i < mData.length; i++) {
                    if (!mData[i].equals(otherData[i])) {
                        return -1;
                    }
                }
            } else {
                return -1;
            }
        } else {
            throw new IllegalArgumentException();
        }

        return 0;
    }

}