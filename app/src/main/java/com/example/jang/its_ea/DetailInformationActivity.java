package com.example.jang.its_ea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.jang.its_ea.helper.IconTextItem;
import com.example.jang.its_ea.helper.OnEventListener;
import com.example.jang.its_ea.helper.RequestQuery;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.example.jang.its_ea.R.id.listview;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by jang on 2016-12-02.
 */

import com.example.jang.its_ea.helper.AccidentInfo;

public class DetailInformationActivity extends Activity {
    private AccidentInfo accidentinfo;
    private TextView accInfo;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        Intent intent = new Intent(getApplicationContext(), ControlCenterActivity.class);
//        intent.putExtra("tab",true);
//        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailinformation_layout);
        accInfo = (TextView)findViewById(R.id.accInfo);
        accidentinfo = new AccidentInfo();
        accidentinfo.setGdtiId(intent.getStringExtra("gdti"));
        getAccidentInfo();
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
//        adapter.clear();
        String result="";
        for(int i=0; i<descNodes.getLength();i++){

            for(Node node = descNodes.item(i).getFirstChild(); node!=null; node=node.getNextSibling()){

                if(node.getNodeName().equals("accident:address")){

                    accidentinfo.setAddress(node.getTextContent());
                    Log.d("result_", node.getTextContent());      //결과값
                }
                else if(node.getNodeName().equals("accident:age"))
                {
                    accidentinfo.setAge(node.getTextContent());
                }
                else if(node.getNodeName().equals("accident:symptom"))
                {
                    accidentinfo.setSymptom(node.getTextContent());
                }
                else if(node.getNodeName().equals("accident:name"))
                {
                    accidentinfo.setName(node.getTextContent());
                }
                else if(node.getNodeName().equals("accident:phonenumber"))
                {
                    accidentinfo.setPhoneNumber(node.getTextContent());
                }
                else if(node.getNodeName().equals("accident:hostpital"))
                {
                    accidentinfo.setHospitalAddress(node.getTextContent());
//                    accidentinfo[i].setCount(descNodes.getLength());
                }
                else if(node.getNodeName().equals("accident:type"))
                {
                    accidentinfo.setAccidentType(node.getTextContent());
                }
                else if(node.getNodeName().equals("bizStep"))
                {
                    accidentinfo.setStatus(node.getTextContent().replace(" ", "").split(":")[4]);
                }
            }
            result =    "긴급도 : " + accidentinfo.getAccidentType() + "\n"
                            +  "주소 : " + accidentinfo.getAddress() + "\n"
                            + "연령 : " + accidentinfo.getAge() + "\n"
                            + "신고자 이름 : " + accidentinfo.getName() + "\n"
                            + "신고자 연락처 : " + accidentinfo.getPhoneNumber() + "\n"
                            + "병원 주소 : " + accidentinfo.getHospitalAddress() + "\n"
                            + "상태 : " + accidentinfo.getStatus();
            }
        accInfo.setText(result);
        }

    private void getAccidentInfo() {

        Log.d("getAccidentInfo", accidentinfo.getGdtiId());
        RequestQuery epcis = new RequestQuery(getApplicationContext(), new OnEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                InputStream input;

                try {
                    input = new ByteArrayInputStream(result.getBytes("utf-8"));
                    Log.d("getAccidentInfo", result);
                    start(input);
                }catch (Exception e)
                {
                }
            }
            public void onFailure(Exception e) {
                Log.i("fail", "Failted to query from epcis");
            }
        });
        epcis.execute("MATCH_epc=urn:epc:id:gdti:"+ accidentinfo.getGdtiId() +
                      "&orderBy=eventTime&eventCountLimit");
    }
}
