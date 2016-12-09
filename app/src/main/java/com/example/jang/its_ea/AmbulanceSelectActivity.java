package com.example.jang.its_ea;

/**
 * Created by jang on 2016-12-01.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jang.its_ea.helper.AccidentInfo;
import com.example.jang.its_ea.helper.RequestCapture;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;



public class AmbulanceSelectActivity extends Activity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private Button btn_ready, btn_waiting,btn_transfer, btn_return;
    private TextView tv_status;
    private AccidentInfo accidentInfo;

    private Geocoder geocoder;

    private  double patient_lat , patient_lon , hospital_lat , hospital_lon;
    private String eventDate, eventTime;

    private double locationX, locationY;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static final int  REQUEST_CODE_LOCATION = 2;
    private static final String SGTIN = "urn:epc:id:sgtin:4012345.077889.25";


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ambulanceselect_layout);

        init();
        Intent intent = getIntent();
        accidentInfo = (AccidentInfo)intent.getSerializableExtra("OBJECT");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

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
                updateEvent("returning", "in_progress");
                assignTask("closing", "completed");
                Intent intent = new Intent(getApplicationContext(), AmbulanceActivity.class);
                intent.putExtra("OBJECT", accidentInfo);
                startActivity(intent);
                finish();
            }
        });


        assignTask("processing", "in_progress");


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
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
                "          <epc>urn:epc:id:gdti:" + accidentInfo.getGdtiId() + "</epc>\n" +
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
                "        <accident:address>" + accidentInfo.getAddress()+ "</accident:address>\n" +
                "        <accident:address_lat>" + patient_lat + "</accident:address_lat>\n" +
                "        <accident:address_lon>" + patient_lon + "</accident:address_lon>\n" +
                "        <accident:age>" + accidentInfo.getAge()+ "</accident:age>\n" +
                "        <accident:symptom>" + accidentInfo.getSymptom() + "</accident:symptom>\n" +
                "        <accident:name>" + accidentInfo.getName() + "</accident:name>\n" +
                "        <accident:phonenumber>" + accidentInfo.getPhoneNumber() + "</accident:phonenumber>\n" +
                "        <accident:hostpital>" + accidentInfo.getHospitalAddress() + "</accident:hostpital>\n" +
                "        <accident:hostpital_lat>" + hospital_lat+ "</accident:hostpital_lat>\n" +
                "        <accident:hostpital_lon>" + hospital_lon+ "</accident:hostpital_lon>\n" +
                "        <accident:type>" + accidentInfo.getAccidentType()+ "</accident:type>\n" +

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



    private void updateEvent(String event, String event2) {
        RequestCapture epcis = new RequestCapture();

        String eventDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format((System.currentTimeMillis()));
        String eventTime = new java.text.SimpleDateFormat("HH:mm:ss").format((System.currentTimeMillis()));


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
                "          <epc>" + SGTIN + "</epc>\n" +
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
                "            <geo>" + locationX + "," + locationY + "</geo>\n" +
                "          </extension>\n" +
                "        </bizLocation>\n" +
                "        <!-- Where! -->\n" +
                "      </ObjectEvent>\n" +
                "    </EventList>\n" +
                "  </EPCISBody>\n" +
                "</epcis:EPCISDocument>";
        epcis.execute(xml);
    }
    /** gps start**/
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1500);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CheckPermission();
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    public void onLocationChanged(Location location) {
        locationX = location.getLatitude();
        locationY = location.getLongitude();

        updateEvent("departuring", "in_progress");
        Log.d("location___", locationX + ", " +  locationY);
    }



    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            Log.d("", "Start Location update");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            Toast.makeText(this,"Location Unavialable",Toast.LENGTH_LONG).show();
        }
    }

    private void CheckPermission() {

        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(AmbulanceSelectActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(AmbulanceSelectActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                ActivityCompat.requestPermissions(AmbulanceSelectActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_LOCATION);

                return;
            }
            ActivityCompat.requestPermissions(AmbulanceSelectActivity.this,
                    new String[]{android.Manifest.permission.WRITE_CONTACTS},
                    REQUEST_CODE_LOCATION);
            return;
        }

        hasWriteContactsPermission = ContextCompat.checkSelfPermission(AmbulanceSelectActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(AmbulanceSelectActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(AmbulanceSelectActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION);

                return;
            }
            ActivityCompat.requestPermissions(AmbulanceSelectActivity.this,
                    new String[]{android.Manifest.permission.WRITE_CONTACTS},
                    REQUEST_CODE_LOCATION);
            return;
        }

    }
    /** gps end**/
}
