package com.example.jang.its_ea.helper;

/**
 * Created by luvsword on 2016-12-03.
 */

public interface OnEventListener<T> {
    public void onSuccess(T object);
    public void onFailure(Exception e);
}
