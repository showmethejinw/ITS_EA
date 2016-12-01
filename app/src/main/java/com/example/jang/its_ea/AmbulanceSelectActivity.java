package com.example.jang.its_ea;

/**
 * Created by jang on 2016-12-01.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class AmbulanceSelectActivity extends Activity {

    Button btn_ready, btn_waiting,btn_transfer, btn_return;
    TextView tv_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ambulanceselect_layout);

        init();

        btn_ready.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "준비", Toast.LENGTH_SHORT).show();
                tv_status.setText("준비중");

            }
        });

        btn_waiting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "대기", Toast.LENGTH_SHORT).show();
                tv_status.setText("대기중");
            }
        });

        btn_transfer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "이송", Toast.LENGTH_SHORT).show();
                tv_status.setText("이송중");
            }
        });

        btn_return.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "복귀", Toast.LENGTH_SHORT).show();
                tv_status.setText("복귀중");
            }
        });




    }

    public void init()
    {
        btn_ready = (Button)findViewById(R.id.btn_ready);
        btn_waiting = (Button)findViewById(R.id.btn_waiting);
        btn_transfer = (Button)findViewById(R.id.btn_transfer);
        btn_return = (Button)findViewById(R.id.btn_return);

        tv_status = (TextView)findViewById(R.id.tv_status);
    }


    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();

    }
}
