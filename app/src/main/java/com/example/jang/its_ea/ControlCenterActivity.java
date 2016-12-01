package com.example.jang.its_ea;

/**
 * Created by jang on 2016-12-01.
 */


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.jang.its_ea.helper.IconTextItem;
import com.example.jang.its_ea.helper.IconTextListAdapter;

public class ControlCenterActivity extends AppCompatActivity {

    private EditText et_address, et_symptom, et_age, et_name, et_phonenumber, et_hospitaladdress;
    private Button btn_send;
    private String emergency;

    private ArrayAdapter<String> adspinner;
    private Spinner spinner;

    private Geocoder geocoder;

    private ListView listview;
    private IconTextListAdapter adapter;



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing_layout);

        init();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                emergency = adspinner.getItem(position).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(et_address.getText().toString().equals("")
                        && et_age.getText().toString().equals("")
                        && et_symptom.getText().toString().equals("")
                        && et_name.getText().toString().equals("")
                        && et_phonenumber.getText().toString().equals("")
                        && et_hospitaladdress.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "입력되지 않은 정보가 존재합니다.", Toast.LENGTH_SHORT).show();
                else
                {
                    String patient_address, age, symptom, name, phonenumber, hostpitaladdress;
                    double patient_lat, patient_lon, hospital_lat, hospital_lon;

                    patient_address = et_address.getText().toString();
                    age = et_age.getText().toString();
                    symptom = et_symptom.getText().toString();
                    name = et_name.getText().toString();
                    phonenumber = et_phonenumber.getText().toString();
                    hostpitaladdress = et_hospitaladdress.getText().toString();


                    try {
                        /**환자 주소, 이송할 병원 좌표 계산**/
                        patient_lat =  geocoder.getFromLocationName(patient_address, 1).get(0).getLatitude();
                        patient_lon =  geocoder.getFromLocationName(patient_address, 1).get(0).getLongitude();
                        hospital_lat = geocoder.getFromLocationName(hostpitaladdress, 1).get(0).getLatitude();
                        hospital_lon = geocoder.getFromLocationName(hostpitaladdress, 1).get(0).getLongitude();

//                        Toast.makeText(getApplicationContext(), String.valueOf(lat) + ", " + String.valueOf(lon),Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e)
                    {

                    }

                    //전송

                }


            }
        });




        adapter.addItem(new IconTextItem("1. 서울시 송파구 송파동 - 출동중"));
        adapter.addItem(new IconTextItem("2. 서울시 강남구 도곡동 - 출동 준비 중"));
        listview.setAdapter(adapter);
//        listview.setBackgroundColor(Color.rgb(25,25,25));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {          //리스트 뷰에서 리스트 중 하나를 클릭 했을 때발생
                IconTextItem curItem = (IconTextItem) adapter.getItem(position);
                final String[] curData = curItem.getData();
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(ControlCenterActivity.this);
                alert_confirm.setMessage("상세 정보 확인").setCancelable(false).setPositiveButton("확인",     //팝업 작동
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


//                                Toast.makeText(getApplicationContext(), "출동 할당 완료", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), DetailInformationActivity.class);
                                startActivity(intent);
                                finish();

                                onResume();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();

            }


        });

    }

    public void init()
    {

        TabHost tab_host = (TabHost) findViewById(R.id.tabhost);
        tab_host.setup();

        TabHost.TabSpec ts1 = tab_host.newTabSpec("tab1");
        ts1.setIndicator("등록");
        ts1.setContent(R.id.tab1);
        tab_host.addTab(ts1);

        TabHost.TabSpec ts2 = tab_host.newTabSpec("tab2");
        ts2.setIndicator("추적");
        ts2.setContent(R.id.tab2);
        tab_host.addTab(ts2);

        et_address = (EditText)findViewById(R.id.et_address);
        et_age = (EditText)findViewById(R.id.et_age);
        et_symptom = (EditText)findViewById(R.id.et_symptom);
        et_name = (EditText)findViewById(R.id.et_name);
        et_phonenumber = (EditText)findViewById(R.id.et_phonenumber);
        et_hospitaladdress = (EditText)findViewById(R.id.et_hospitaladdress);

        btn_send = (Button)findViewById(R.id.btn_send);

        geocoder = new Geocoder(this);

        String[]  emergencyList= {"긴급","일반"};

        spinner = (Spinner)findViewById(R.id.spinner);

        adspinner = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                emergencyList);
        spinner.setAdapter(adspinner);

        spinner.setSelection(0);

        listview = (ListView) findViewById(R.id.listview);
        adapter = new IconTextListAdapter(this);

        listview.setAdapter(adapter);

    }
}
