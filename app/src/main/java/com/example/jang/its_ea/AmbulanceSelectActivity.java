package com.example.jang.its_ea;

/**
 * Created by jang on 2016-12-01.
 */

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jang.its_ea.helper.AccidentInfo;
import com.example.jang.its_ea.helper.RequestCapture;

import org.w3c.dom.Text;

public class AmbulanceSelectActivity extends Activity {

    private Button btn_ready, btn_waiting,btn_transfer, btn_return;
    private TextView tv_status;
    private AccidentInfo accidentInfo;

    private Geocoder geocoder;

    private  double patient_lat , patient_lon , hospital_lat , hospital_lon;
    private String eventDate, eventTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ambulanceselect_layout);

        init();
        Intent intent = getIntent();
        accidentInfo = (AccidentInfo)intent.getSerializableExtra("OBJECT");

        tv_status.setText(
                   "긴급도 : " + accidentInfo.getAccidentType() + "\n"
                +  "주소 : " + accidentInfo.getAddress() + "\n"
                + "이름 : " + accidentInfo.getName() + "\n"
                + "연령 : " + accidentInfo.getAge() + "\n"
                + "신고자 이름 : " + accidentInfo.getName() + "\n"
                + "신고자 연락처 : " + accidentInfo.getPhoneNumber() + "\n"
                + "병원 주소 : " + accidentInfo.getHospitalAddress()
        );

        btn_return.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "복귀", Toast.LENGTH_SHORT).show();
//                tv_status.setText("복귀중");
                Intent intent = new Intent(getApplicationContext(), AmbulanceActivity.class);
                startActivity(intent);
                finish();
            }
        });


        assignTask();

    }

    public void init()
    {
//        btn_ready = (Button)findViewById(R.id.btn_ready);
//        btn_waiting = (Button)findViewById(R.id.btn_waiting);
//        btn_transfer = (Button)findViewById(R.id.btn_transfer);
        btn_return = (Button)findViewById(R.id.btn_return);

        tv_status = (TextView)findViewById(R.id.tv_status);
        accidentInfo = new AccidentInfo();
        geocoder = new Geocoder(this);
    }


    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(getApplicationContext(), AmbulanceActivity.class);
//        startActivity(intent);
//        finish();

    }

    public void assignTask()
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
                "          <epc>urn:epc:id:gdti:0614141.06012.1234</epc>\n" +
                "          </epcList>\n" +
                "        <!-- What!  -->\n" +
                "\n" +
                "        <!-- Add, Observe, Delete -->\n" +
                "        <action>ADD</action>\n" +
//                            "        <example:1>\n" +
//                            "            <extension>ADD</extension>\n" +
//                            "           <extension>ADD</extension>\n" +
//                            "        </example:1>\n" +
                "        <!-- Where!  address, age, symptom, name, phonenumber, hostpital; -->\n" +
                "        <accident:address>" + accidentInfo.getAddress()+ "</accident:address>\n" +
                "        <accident:address_lat>" + patient_lat + "</accident:address_lat>\n" +
                "        <accident:address_lon>" + patient_lon + "</accident:address_lon>\n" +
                "        <accident:age>" + accidentInfo.getAge()+ "</accident:age>\n" +
                "        <accident:symptom>" + accidentInfo.getSymptom()+ "</accident:symptom>\n" +
                "        <accident:name>" + accidentInfo.getName()+ "</accident:name>\n" +
                "        <accident:phonenumber>" + accidentInfo.getPhoneNumber()+ "</accident:phonenumber>\n" +
                "        <accident:hostpital>" + accidentInfo.getHospitalAddress()+ "</accident:hostpital>\n" +
                "        <accident:hostpital_lat>" + hospital_lat+ "</accident:hostpital_lat>\n" +
                "        <accident:hostpital_lon>" + hospital_lon+ "</accident:hostpital_lon>\n" +
                "        <accident:type>" + accidentInfo.getAccidentType()+ "</accident:type>\n" +
                "        <!-- 할당 되지 않음 : 0  / 할당 됨 : 1-->\n" +
                "        <accident:assign>" + 1 + "</accident:assign>\n" +
                "      </ObjectEvent>\n" +
                "    </EventList>\n" +
                "  </EPCISBody>\n" +
                "</epcis:EPCISDocument>";
        Log.d("xml___",xml);
        epcis.execute(xml);

    }

    public void setAssignInfo()
    {
        eventDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format((System.currentTimeMillis()));
        eventTime = new java.text.SimpleDateFormat("HH:mm:ss").format((System.currentTimeMillis()));
        try {
            /**환자 주소, 이송할 병원 좌표 계산**/
            patient_lat =  geocoder.getFromLocationName(accidentInfo.getAddress(), 1).get(0).getLatitude();
            patient_lon =  geocoder.getFromLocationName(accidentInfo.getAddress(), 1).get(0).getLongitude();
            hospital_lat = geocoder.getFromLocationName(accidentInfo.getHospitalAddress(), 1).get(0).getLatitude();
            hospital_lon = geocoder.getFromLocationName(accidentInfo.getHospitalAddress(), 1).get(0).getLongitude();

//                        Toast.makeText(getApplicationContext(), String.valueOf(lat) + ", " + String.valueOf(lon),Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {

        }
    }
}
