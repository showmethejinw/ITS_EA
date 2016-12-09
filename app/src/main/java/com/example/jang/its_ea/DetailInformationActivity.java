package com.example.jang.its_ea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), ControlCenterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailinformation_layout);
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

        for(int i=0; i<descNodes.getLength();i++){

            for(Node node = descNodes.item(i).getFirstChild(); node!=null; node=node.getNextSibling()){

                if(node.getNodeName().equals("accident:address")){

                    Log.d("result_", node.getTextContent());
                }
            }

        }
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
                  //  start(input);
                }catch (Exception e)
                {
                }
            }
            public void onFailure(Exception e) {
                Log.i("fail", "Failted to query from epcis");
            }
        });
        epcis.execute("MATCH_epc=urn:epc:id:gdti:"+ accidentinfo.getGdtiId());
    }
}
