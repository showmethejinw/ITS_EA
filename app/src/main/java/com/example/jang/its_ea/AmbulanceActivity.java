package com.example.jang.its_ea;

/**
 * Created by jang on 2016-12-01.
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jang.its_ea.helper.AccidentInfo;
import com.example.jang.its_ea.helper.IconTextItem;
import com.example.jang.its_ea.helper.IconTextListAdapter;
import com.example.jang.its_ea.helper.OnEventListener;
import com.example.jang.its_ea.helper.RequestQuery;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class AmbulanceActivity extends AppCompatActivity {

    private ListView listview;
    private IconTextListAdapter adapter;
    private JSONObject json;
    private AccidentInfo [] accidentInfo;
    private RequestQuery requestQuery;
    private static final String TAG = "AM";
    private Button btn_prepare, btn_standby;
    private int originColor;
    private int flag = 0;
    private int number;
    @Override
    protected void onResume() {
        super.onResume();
//        listview.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ambulance_layout);

        init();


//        adapter.addItem(new IconTextItem("1. 서울시 송파구 송파동"));
//        adapter.addItem(new IconTextItem("2. 서울시 강남구 도곡동"));
//        listview.setAdapter(adapter);

        queryEvent();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {          //리스트 뷰에서 리스트 중 하나를 클릭 했을 때발생
                IconTextItem curItem = (IconTextItem) adapter.getItem(position);
                final String[] curData = curItem.getData();
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(AmbulanceActivity.this);
                number = position;          //위치 파악
                alert_confirm.setMessage("이 곳으로 출동하시 겠습니까??").setCancelable(false).setPositiveButton("확인",     //팝업 작동
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


//                                queryEvent();
                                Toast.makeText(getApplicationContext(), "출동 할당 완료", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), AmbulanceSelectActivity.class);
                                intent.putExtra("OBJECT", accidentInfo[number]);
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
        originColor = btn_prepare.getDrawingCacheBackgroundColor();


        btn_prepare.setOnClickListener(new View.OnClickListener() {             // 준비중
            @Override
            public void onClick(View v) {
                Toast.makeText(AmbulanceActivity.this, "준비중....", Toast.LENGTH_SHORT).show();
                btn_prepare.setTextColor(Color.RED);
                btn_prepare.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_standby.setBackgroundColor(originColor);
                btn_standby.setClickable(true);
                btn_standby.setTextColor(Color.BLACK);
            }
        });


        btn_standby.setOnClickListener(new View.OnClickListener() {         //  대기중
            @Override
            public void onClick(View v) {

                Toast.makeText(AmbulanceActivity.this, "대기중....", Toast.LENGTH_SHORT).show();
                btn_standby.setTextColor(Color.RED);
                btn_standby.setBackgroundColor(Color.rgb(255, 255, 255));
                btn_prepare.setBackgroundColor(originColor);
                btn_standby.setClickable(false);
                btn_prepare.setTextColor(Color.BLACK);
                flag = 0;
            }
        });


    }

    private Document parseXML(InputStream stream) throws Exception{

        DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder = null;
        Document doc = null;

        try{

            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

            doc = objDocumentBuilder.parse(stream);

        }catch(Exception ex){
            throw ex;
        }

        return doc;
    }

    private void start(InputStream input) throws Exception{
        Document doc = parseXML(input);
        NodeList descNodes = doc.getElementsByTagName("ObjectEvent");

        accidentInfo = new AccidentInfo[descNodes.getLength()];

        for (int i = 0; i < descNodes.getLength(); i++) {
            accidentInfo[i] = new AccidentInfo();
            for (Node node = descNodes.item(i).getFirstChild(); node != null; node = node.getNextSibling()) { //첫번째 자식을 시작으로 마지막까지 다음 형제를 실행

                if(node.getNodeName().equals("accident:address")){

                    accidentInfo[i].setAddress(node.getTextContent());
                    adapter.addItem(new IconTextItem(node.getTextContent()));
                    listview.setAdapter(adapter);

                    Log.d("result_", node.getTextContent());      //결과값
                }
                else if(node.getNodeName().equals("accident:age"))
                {
                    accidentInfo[i].setAge(node.getTextContent());
                }
                else if(node.getNodeName().equals("accident:symptom"))
                {
                    accidentInfo[i].setSymptom(node.getTextContent());
                }
                else if(node.getNodeName().equals("accident:name"))
                {
                    accidentInfo[i].setName(node.getTextContent());
                }
                else if(node.getNodeName().equals("accident:phonenumber"))
                {
                    accidentInfo[i].setPhoneNumber(node.getTextContent());
                }
                else if(node.getNodeName().equals("accident:hostpital"))
                {
                    accidentInfo[i].setHospitalAddress(node.getTextContent());
                    accidentInfo[i].setCount(descNodes.getLength());
                }
                else if(node.getNodeName().equals("accident:type"))
                {
                    accidentInfo[i].setAccidentType(node.getTextContent());
                }

            }

        }
    }

    private void queryEvent() {

        RequestQuery epcis = new RequestQuery(getApplicationContext(), new OnEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                InputStream input;

                try {
                    input = new ByteArrayInputStream(result.getBytes("utf-8"));
                    start(input);
                } catch (Exception e) {
                }
            }

            public void onFailure(Exception e) {
                Log.i("fail", "Failted to query from epcis");
            }
        });
        epcis.execute();
    }

    public void init() {
        listview = (ListView) findViewById(R.id.listview);
        adapter = new IconTextListAdapter(this);

//        listview.setAdapter(adapter);

        btn_prepare = (Button) findViewById(R.id.btn_prepare);
        btn_standby = (Button) findViewById(R.id.btn_standby);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
