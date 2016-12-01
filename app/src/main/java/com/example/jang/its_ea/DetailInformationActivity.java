package com.example.jang.its_ea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by jang on 2016-12-02.
 */

public class DetailInformationActivity extends Activity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), ControlCenterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailinformation_layout);
    }
}
