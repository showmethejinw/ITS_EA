package com.example.jang.its_ea;

/**
 * Created by jang on 2016-12-01.
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jang.its_ea.helper.AccidentInfo;
import com.example.jang.its_ea.helper.IconTextItem;
import com.example.jang.its_ea.helper.IconTextListAdapter;
import com.example.jang.its_ea.helper.OnEventListener;
import com.example.jang.its_ea.helper.RequestQuery;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class AmbulanceActivity extends AppCompatActivity {

    private ListView listview;
    private IconTextListAdapter adapter;
    private JSONObject json;
    private AccidentInfo accidentInfo;
    private RequestQuery requestQuery;
    private static final String TAG="AM";

    @Override
    protected void onResume() {
        super.onResume();
        listview.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ambulance_layout);

        init();


        adapter.addItem(new IconTextItem("1. 서울시 송파구 송파동"));
        adapter.addItem(new IconTextItem("2. 서울시 강남구 도곡동"));
        listview.setAdapter(adapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {          //리스트 뷰에서 리스트 중 하나를 클릭 했을 때발생
                IconTextItem curItem = (IconTextItem) adapter.getItem(position);
                final String[] curData = curItem.getData();
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(AmbulanceActivity.this);
                alert_confirm.setMessage("이 곳으로 출동하시 겠습니까??").setCancelable(false).setPositiveButton("확인",     //팝업 작동
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                queryEvent();

                                Toast.makeText(getApplicationContext(), "출동 할당 완료", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), AmbulanceSelectActivity.class);
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
    private void queryEvent() {

        RequestQuery epcis = new RequestQuery(getApplicationContext(), new OnEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "query result = " + result);
//                try
//                {
//                    json = XML.toJSONObject(result);
//                    Log.d("json.tostring",json.getJSONArray("epcList").toString());
//                    ;
//                }catch (Exception e)
//                {
//
//                }


                try{
                    String Stringtest = "";
                    json = XML.toJSONObject(result);
                    Log.d("json.tostring",json.toString());
                    JSONObject t1 = json.getJSONObject("EPCISQueryDocumentType");
                    JSONObject t2 =
                    JSONArray ja = json.getJSONArray("epcList");
                    for (int i = 0; i < ja.length(); i++){
                        JSONObject order = ja.getJSONObject(i);
                        Stringtest += "test: " + order.getString("epc") + "\n";
                        Log.d("Stringtest",Stringtest);
                    }
                }catch (Exception e)
                {

                }



            }
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "Failted to query from epcis");
            }
        });
        epcis.execute();
    }

    public void init() {
        listview = (ListView) findViewById(R.id.listview);
        adapter = new IconTextListAdapter(this);

        listview.setAdapter(adapter);
        accidentInfo = new AccidentInfo();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
