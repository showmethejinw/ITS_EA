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
import com.example.jang.its_ea.helper.RequestCapture;
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

    private double patient_lat,patient_lon,hospital_lat,hospital_lon;
    private String eventDate,eventTime;

    private Geocoder geocoder;

    private static final String SGTIN1 = "urn:epc:id:sgtin:4012345.077889.25";
    private static final String SGTIN2 = "urn:epc:id:sgtin:4012345.077889.26";

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

        adapter.clear();
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
//                                finish();

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

                updateEvent("preparing","inactive");
                /**추가해야 되는가?**/
//                queryEvent();
                /**추가해야 되는가?**/
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

                adapter.clear();

                updateEvent("waiting","ready");
                queryEvent();

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

                /*if(node.getNodeName().equals("accident:assign"))
                {
                    if(node.getTextContent().equals("0"))
                    {

                    }

                }*/
                if (node.getNodeName().equals("accident:address")) {

                    accidentInfo[i].setAddress(node.getTextContent());
//                    adapter.addItem(new IconTextItem(node.getTextContent()));
//                    listview.setAdapter(adapter);

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
                else if(node.getNodeName().equals("epcList"))
                {
                    accidentInfo[i].setGdtiId(node.getTextContent().replace(" ","").split(":")[4]);
                }

            }

        }

        for(int i = 0; i < accidentInfo.length; i++)
        {
            int count = 0;
            for(int j = 0; j < accidentInfo.length; j++)
            {
                if(accidentInfo[i].getAddress().equals(accidentInfo[j].getAddress()))
                {
                    count++;
                    if(count > 1)
                        break;
                }
            }
            if(count == 1)
            {
                adapter.addItem(new IconTextItem(accidentInfo[i].getAddress()));
                listview.setAdapter(adapter);
            }
        }
    }

    private void queryEvent() {

//        adapter.clear();
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
        epcis.execute("");
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

    private void updateEvent(String event, String event2) {
        RequestCapture epcis = new RequestCapture();

        String eventDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format((System.currentTimeMillis()));
        String eventTime = new java.text.SimpleDateFormat("HH:mm:ss").format((System.currentTimeMillis()));

        Log.d(TAG, "updateEvent");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<!DOCTYPE project>\n" +
                "<epcis:EPCISDocument xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\" \n" +
                "                     schemaVersion=\"1.2\" creationDate=\"2016-11-13T11:30:47.0Z\">\n" +
                "  <EPCISBody>\n" +
                "    <EventList>\n" +
                "      <ObjectEvent>\n" +
                "        <!-- When -->\n" +
                "        <eventTime>" + eventDate + "T" + eventTime + ".116-10:00</eventTime>\n" +
                "        <eventTimeZoneOffset>-10:00</eventTimeZoneOffset>\n" +
                "        <!-- When! -->\n" +
                "\n" +
                "        <!--  What -->\n" +
                "        <epcList>\n" +
                "          <epc> " + SGTIN1 + "</epc>\n" +
                "        </epcList>\n" +
                "        <!-- What!-->\n" +
                "\n" +
                "        <!-- Add, Observe, Delete -->\n" +
                "        <action>ADD</action>\n" +
                "\n" +
                "        <!-- Why -->\n" +
                "        <bizStep>urn:epcglobal:cbv:bizstep:"+ event +"</bizStep>\n" +
                "        <disposition>urn:epcglobal:cbv:disp:" + event2 + "</disposition>\n" +
                "        <!-- Why! -->\n" +
                "\n" +
                "        <!-- Where -->\n" +
                "        <bizLocation>\n" +
                "          <id>urn:epc:id:sgln:7654321.54321.1234</id>\n" +
                "          <extension>\n" +
                "            <geo>" + 0 + "," + 0 + "</geo>\n" +
                "          </extension>\n" +
                "        </bizLocation>\n" +
                "        <!-- Where! -->\n" +
                "      </ObjectEvent>\n" +
                "    </EventList>\n" +
                "  </EPCISBody>\n" +
                "</epcis:EPCISDocument>";
        epcis.execute(xml);
    }

    public void assignTask(String event, String event2)
    {
        RequestCapture epcis = new RequestCapture();

        setAssignInfo();

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<!DOCTYPE project>\n" +
                "        <!-- xmlns:accident=\".xsd\"가 새로운 태그를 추가시킬 수 있다!-->\n" +
                "<epcis:EPCISDocument xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\" \n" +
                "xmlns:accident=\".xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                "                     schemaVersion=\"1.2\" creationDate=\"2016-11-13T11:30:47.0Z\">\n" +
                "  <EPCISBody>\n" +
                "    <EventList>\n" +
                "      <ObjectEvent>\n" +
                "        <!-- When -->\n" +
                "        <eventTime>" + eventDate + "T" + eventTime + ".116-10:00</eventTime>\n" +
                "        <eventTimeZoneOffset>-10:00</eventTimeZoneOffset>\n" +
                "        <!-- When! -->\n" +
                "\n" +
                "        <!--  What  -->\n" +
                "        <epcList>\n" +
                "          <epc>urn:epc:id:gdti:" + accidentInfo[number].getGdtiId() + "</epc>\n" +
                "          </epcList>\n" +
                "        <!-- What!  -->\n" +
                "\n" +
                "        <!-- Add, Observe, Delete -->\n" +
                "        <action>ADD</action>\n" +
//                            "        <example:1>\n" +
//                            "            <extension>ADD</extension>\n" +
//                            "           <extension>ADD</extension>\n" +
//                            "        </example:1>\n" +
                "        <bizStep>urn:epcglobal:cbv:bizstep:"+ event +"</bizStep>\n" +
                "        <disposition>urn:epcglobal:cbv:disp:" + event2 + "</disposition>\n" +

                "        <!-- Where!  address, age, symptom, name, phonenumber, hostpital; -->\n" +

                "        <!-- 할당 되지 않음 : 0  / 할당 됨 : 1-->\n" +
                "        <accident:assign>" + 1 + "</accident:assign>\n" +
                "        <accident:address>" + accidentInfo[number].getAddress()+ "</accident:address>\n" +
                "        <accident:address_lat>" + patient_lat + "</accident:address_lat>\n" +
                "        <accident:address_lon>" + patient_lon + "</accident:address_lon>\n" +
                "        <accident:age>" + accidentInfo[number].getAge()+ "</accident:age>\n" +
                "        <accident:symptom>" + accidentInfo[number].getSymptom() + "</accident:symptom>\n" +
                "        <accident:name>" + accidentInfo[number].getName() + "</accident:name>\n" +
                "        <accident:phonenumber>" + accidentInfo[number].getPhoneNumber() + "</accident:phonenumber>\n" +
                "        <accident:hostpital>" + accidentInfo[number].getHospitalAddress() + "</accident:hostpital>\n" +
                "        <accident:hostpital_lat>" + hospital_lat+ "</accident:hostpital_lat>\n" +
                "        <accident:hostpital_lon>" + hospital_lon+ "</accident:hostpital_lon>\n" +
                "        <accident:type>" + accidentInfo[number].getAccidentType()+ "</accident:type>\n" +

                "      </ObjectEvent>\n" +
                "    </EventList>\n" +
                "  </EPCISBody>\n" +
                "</epcis:EPCISDocument>";
        Log.d("xml___",xml);
        epcis.execute(xml);

    }

    public void setAssignInfo()
    {
        geocoder = new Geocoder(this);
        eventDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format((System.currentTimeMillis()));
        eventTime = new java.text.SimpleDateFormat("HH:mm:ss").format((System.currentTimeMillis()));
        try {
            /**환자 주소, 이송할 병원 좌표 계산**/
            patient_lat =  geocoder.getFromLocationName(accidentInfo[number].getAddress(), 1).get(0).getLatitude();
            patient_lon =  geocoder.getFromLocationName(accidentInfo[number].getAddress(), 1).get(0).getLongitude();
            hospital_lat = geocoder.getFromLocationName(accidentInfo[number].getHospitalAddress(), 1).get(0).getLatitude();
            hospital_lon = geocoder.getFromLocationName(accidentInfo[number].getHospitalAddress(), 1).get(0).getLongitude();

//                        Toast.makeText(getApplicationContext(), String.valueOf(lat) + ", " + String.valueOf(lon),Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {

        }
    }
}
