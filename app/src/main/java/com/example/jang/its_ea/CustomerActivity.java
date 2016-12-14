package com.example.jang.its_ea;

/**
 * Created by jang on 2016-12-01.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jang.its_ea.helper.AccidentInfo;
import com.example.jang.its_ea.helper.OnEventListener;
import com.example.jang.its_ea.helper.RequestQuery;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 현재 위치의 지도를 보여주는 방법에 대해 알 수 있습니다.
 * <p>
 * Google Play Services 라이브러리를 링크하여 사용합니다.
 * 구글맵 v2를 사용하기 위한 여러 가지 권한이 있어야 합니다.
 * 매니페스트 파일 안에 있는 키 값을 PC에 맞는 것으로 새로 발급받아서 넣어야 합니다.
 *
 * @author Mike
 */


public class CustomerActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    static final LatLng SEOUL = new LatLng(37.56, 126.97);
    static final LatLng KAIST_SW = new LatLng(37.483762, 127.043962);
    private static final String TAG = "Customer";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final int REQUEST_CODE_LOCATION = 2;
    private double locationX;
    private double locationY;

    private static final String SGTIN1 = "eventCountLimit=1&orderBy=eventTime&MATCH_epc=urn:epc:id:sgtin:4012345.077889.25";
    private static final String SGTIN2 = "eventCountLimit=1&orderBy=eventTime&MATCH_epc=urn:epc:id:sgtin:4012345.077889.26";
    private static final String SGDTI = "urn:epc:id:gdti:";

    private double mEPCISLocationX[], mEPCISLocationY[];
    private String nodeValueArray[], mEPCISBizStep[];

    private ImageView infoImageView;
    private TextView infoText, locationText;

    private GoogleMap googleMap;

    private Marker mAmbulance01, mAmbulance02, mMyCar, mAccidentLoc01, mAccidentLoc02;
    private LatLng ambulance01, ambulance02, myCar, accidentLoc01, accidentLoc02;
    private Circle mMyCarCircle;

    private CircleOptions circleOptions;
    private AccidentInfo[] accidentInfo;

    private TextToSpeech tts;

    private int flag1 = 0, flag2 = 0, flag3 = 0;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 5));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KAIST_SW, 12));

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(5), 2000, null);

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);


//        setCustomMarkerView();
//        getSampleMarkerItems();

        queryEvent(SGTIN1);
        queryEvent(SGTIN2);
        queryEvent(SGDTI);
        updateMarkerPosition();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_layout);

        mEPCISLocationX = new double[5];
        mEPCISLocationY = new double[5];

        mEPCISBizStep = new String[2];

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();

        infoImageView = (ImageView) this.findViewById(R.id.list_image);
        locationText = (TextView) this.findViewById(R.id.location);
        infoText = (TextView) this.findViewById(R.id.title);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        infoImageView.setImageResource(R.drawable.car1);
        infoText.setTextColor(Color.BLACK);
        infoText.setText("주행을 시작합니다. 안전 운전 하세요.");
        locationText.setText("현 지점 : " + getAddress(this, 0, 0));

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // 음성 합성하여 출력하기위한 언어를 Locale.US 로 설정한다.
                    // 안드로이드 시스템의 환경 설정에서도 동일한 언어가 선택되어 있어야만
                    // 해당 언어의 문장이 음성변환 될 수 있다.
                    int result = tts.setLanguage(Locale.KOREA);

                    // 해당 언어에 대한 데이터가 없거나 지원하지 않는 경우
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {

                        // 해당 언어는 사용할 수 없음을 알린다.
                        toast("Language is not available.");

                        // TTS 엔진이 해당 언어를 지원하며 데이터도 가지고 있는 경우
                    } else {
                        // TTS 엔진이 성공적으로 초기화된 경우
                        // EditText 에 쓰여지는 문장을 음성 변환할 수 있도록 버튼을 활성화한다.
//                        for (int id : btnIds) {
//                            btn = (Button) findViewById(id);
//                            btn.setEnabled(true);
//                        }
                    }

                    // TextToSpeech 엔진 초기화에 실패하여 엔진이 TextToSpeech.ERROR 상태인 경우
                } else {
                    // TextToSpeech 엔진이 초기화되지 못했음을 알린다.
                    toast("Could not initialize TextToSpeech.");
                }
            }
        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1500);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CheckPermission();
        }

        startLocationUpdates();
    }


    private void CheckPermission() {

        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(CustomerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(CustomerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                ActivityCompat.requestPermissions(CustomerActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_LOCATION);

                return;
            }
            ActivityCompat.requestPermissions(CustomerActivity.this,
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    REQUEST_CODE_LOCATION);
            return;
        }

        hasWriteContactsPermission = ContextCompat.checkSelfPermission(CustomerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(CustomerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(CustomerActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION);

                return;
            }
            ActivityCompat.requestPermissions(CustomerActivity.this,
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    REQUEST_CODE_LOCATION);
            return;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng CURRENT_LOCATION = new LatLng(location.getLatitude(), location.getLongitude());
        locationX = location.getLatitude();
        locationY = location.getLongitude();

        Log.d(TAG, "location changed x = " + locationX + ", y = " + locationY);
        googleMap.clear();
//        Marker seoul = googleMap.addMarker(new MarkerOptions().position(CURRENT_LOCATION)
//                .title("사용자 위치"));
//        tts("테스트 중입니다");
        queryEvent(SGTIN1);
        queryEvent(SGTIN2);
        queryEvent(SGDTI);

        Log.d(TAG, "SendHandleMsg");
        if (checkDistance(locationX, locationY, mEPCISLocationX[0], mEPCISLocationY[0]) ||
                checkDistance(locationX, locationY, mEPCISLocationX[1], mEPCISLocationY[1])) {
            infoImageView.setImageResource(R.drawable.emergency);
            infoText.setTextColor(Color.RED);
            infoText.setText("이동 경로로 구급차가 접근 중입니다. 길을 양보해주세요.");
            locationText.setTextColor(Color.RED);
            if (mEPCISLocationX[2] != 0 && mEPCISLocationX[3] == 0) {
                locationText.setText("사고 지점 : " + getAddress(CustomerActivity.this, 37.485788, 127.040246));
            } else if (mEPCISLocationX[2] != 0 && mEPCISLocationX[3] != 0) {
                locationText.setText("사고지점 1: " + getAddress(CustomerActivity.this, 37.485788, 127.040246) + "\n사고지점 2: " + getAddress(CustomerActivity.this, mEPCISLocationX[3], mEPCISLocationY[3]));
            } else {
                locationText.setText("사고 지점 : 위치를 파악할 수 없습니다.");
            }

            addCircleToMap(true);
        } else {
            infoImageView.setImageResource(R.drawable.car1);
            infoText.setTextColor(Color.BLACK);
            infoText.setText("주행을 시작합니다. 안전 운전 하세요.");
            locationText.setTextColor(Color.BLACK);
            locationText.setText("현 지점 : " + getAddress(this, locationX, locationY));
            addCircleToMap(false);
        }
        updateMarkerPosition();
    }

    private Document parseXML(InputStream stream) throws Exception {

        DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder = null;
        Document doc = null;

        try {

            objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

            doc = objDocumentBuilder.parse(stream);

        } catch (Exception ex) {
            throw ex;
        }

        return doc;
    }

    private void start(InputStream input, String id) throws Exception {
        Document doc = parseXML(input);
        String gpsLocation = null;
        String bizStep = null;

        if (id == SGTIN1 || id == SGTIN2) {
            NodeList objectNode = doc.getElementsByTagName("ObjectEvent");
            for (int i = 0; i < objectNode.getLength(); i++) {
                for (Node node = objectNode.item(i).getFirstChild(); node != null; node = node.getNextSibling()) { //첫번째 자식을 시작으로 마지막까지 다음 형제를 실행

                    if (node.getNodeName().equals("bizStep")) {
                        //Log.d(TAG, "bizStep : " + node.getTextContent());
                        bizStep = node.getTextContent();
                    }
                }
            }


            NodeList bizNode = doc.getElementsByTagName("bizLocation");
            String nodeValue[] = new String[5];
            for (int i = 0; i < bizNode.getLength(); i++) {
                int j = 0;
                for (Node node = bizNode.item(i).getFirstChild(); node != null; node = node.getNextSibling()) { //첫번째 자식을 시작으로 마지막까지 다음 형제를 실행

                    nodeValue[j] = node.getTextContent();
//                Log.d(TAG, "nodeValue [" + j + "]" + nodeValue[j]);  //결과값
                    ++j;
                }
            }

            // Set GPS Location value from EPCIS
            gpsLocation = nodeValue[3];
            nodeValueArray = gpsLocation.replace(" ", "").split(",");

            if (id == SGTIN1) {
                mEPCISLocationX[0] = Double.parseDouble(nodeValueArray[0]);
                mEPCISLocationY[0] = Double.parseDouble(nodeValueArray[1]);
                mEPCISBizStep[0] = bizStep.split(":")[4];
            } else if (id == SGTIN2) {
                mEPCISLocationX[1] = Double.parseDouble(nodeValueArray[0]);
                mEPCISLocationY[1] = Double.parseDouble(nodeValueArray[1]);
                mEPCISBizStep[1] = bizStep.split(":")[4];
            }

        } else if (id == SGDTI) {
            NodeList descNodes = doc.getElementsByTagName("ObjectEvent");
            accidentInfo = new AccidentInfo[descNodes.getLength()];

            for (int i = 0; i < descNodes.getLength(); i++) {
                accidentInfo[i] = new AccidentInfo();
                for (Node node = descNodes.item(i).getFirstChild(); node != null; node = node.getNextSibling()) { //첫번째 자식을 시작으로 마지막까지 다음 형제를 실행

                    if (node.getNodeName().equals("accident:address_lat")) {
//                        Log.d(TAG, "Accident lat [i]: " + i +", " + node.getTextContent());
                        accidentInfo[i].setAddressLat(Double.parseDouble(node.getTextContent()));
                    }

                    if (node.getNodeName().equals("accident:address_lon")) {
//                        Log.d(TAG, "Accident lon [i]: " + i +", " + node.getTextContent());
                        accidentInfo[i].setAddressLon(Double.parseDouble(node.getTextContent()));
                    }

                    if (mEPCISLocationX[2] == 0 && mEPCISLocationY[2] == 0) {
                        mEPCISLocationX[2] = accidentInfo[i].getAddressLat();
                        mEPCISLocationY[2] = accidentInfo[i].getAddressLon();
                    } else if (accidentInfo[i].getAddressLat() != mEPCISLocationX[2] && accidentInfo[i].getAddressLon() != mEPCISLocationY[2]) {
                        mEPCISLocationX[3] = accidentInfo[i].getAddressLat();
                        mEPCISLocationY[3] = accidentInfo[i].getAddressLon();
                    }

                }
            }
        }

        Log.d(TAG, "mEPCISLocationX[0] : " + mEPCISLocationX[0] + " ,mEPCISLocationY[0] : " + mEPCISLocationY[0]);
        Log.d(TAG, "mEPCISLocationX[1] : " + mEPCISLocationX[1] + " ,mEPCISLocationY[1] : " + mEPCISLocationY[1]);
//        Log.d(TAG, "mEPCISLocationX[2] : " + mEPCISLocationX[2] + " ,mEPCISLocationY[2] : " + mEPCISLocationY[2]);
    }

    private void queryEvent(String epcId) {
        final String id = epcId;
        RequestQuery epcis = new RequestQuery(getApplicationContext(), new OnEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                InputStream input;

                try {
                    input = new ByteArrayInputStream(result.getBytes("utf-8"));
                    start(input, id);
                } catch (Exception e) {
                }
            }

            public void onFailure(Exception e) {
                Log.i("fail", "Failted to query from epcis");
            }
        });
        epcis.execute(id);
    }


    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            Log.d(TAG, "Start Location update");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            Toast.makeText(this, "Location Unavailable", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 마커 GPS 좌표
     **/
    private void updateMarkerPosition() {

        ambulance01 = new LatLng(mEPCISLocationX[0], mEPCISLocationY[0]);
        ambulance02 = new LatLng(mEPCISLocationX[1], mEPCISLocationY[1]);

        myCar = new LatLng(locationX, locationY);
//        accidentLoc01 = new LatLng(mEPCISLocationX[2], mEPCISLocationY[2]);
        accidentLoc01 = new LatLng(37.485788, 127.040246);
        accidentLoc02 = new LatLng(mEPCISLocationX[3], mEPCISLocationY[3]);

        addMarkersToMap();
    }

    public static String getAddress(Context mContext, double lat, double lng) {
        String nowAddress = "위치를 확인할 수 없습니다.";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                address = geocoder.getFromLocation(lat, lng, 1);
                if (address != null && address.size() > 0) {
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    if (currentLocationAddress != null) {
                        nowAddress = currentLocationAddress;
                    }
                }
            }
        } catch (IOException e) {
            Toast.makeText(mContext, "주소를 가져올 수 없습니다.", Toast.LENGTH_LONG).show();
        }
        return nowAddress;
    }


    private boolean checkDistance(double myX1, double myY1, double ambulX2, double ambulY2) {
        if ((myX1 == 0) || (myY1 == 0) || (ambulX2 == 0) || (ambulY2 == 0))
            return false;

        double dist = distance(myX1, myY1, ambulX2, ambulY2, "meter");
        //Log.d(TAG, "dist : " + dist);
        if ((100 < dist) && (dist < 200)) {
            if (mEPCISBizStep[0] == null || mEPCISBizStep[1] == null) {
                return false;
            } else if (mEPCISBizStep[0].equals("departuring")) {
                if(flag1 == 0)
                {
                    tts("이동 경로로 구급차가 접근 중입니다. 길을 양보해주세요.");
                    tts("응급차가 200미터 이내에 접근 중입니다");
                    flag1++;
                }

                return true;
            } else if (mEPCISBizStep[1].equals("departuring")) {
                return true;
            }
            return false;
        } else if (50 < dist && dist <= 100) {
            if (mEPCISBizStep[0].equals("departuring")) {
                if(flag2 == 0)
                {
                    tts("이동 경로로 구급차가 접근 중입니다. 길을 양보해주세요.");
                    tts("응급차가 100미터 이내에 접근 중입니다");
                    flag2++;
                }
                return true;
            } else if (mEPCISBizStep[1].equals("departuring")) {
                return true;
            }
            return false;
        } else if (0 < dist && dist <= 50) {
            if (mEPCISBizStep[0].equals("departuring")) {
                if(flag3 == 0)
                {
                    tts("이동 경로로 구급차가 접근 중입니다. 길을 양보해주세요.");
                    tts("응급차가 50미터 이내에 접근 중입니다");
                    flag3++;
                }
                return true;
            } else if (mEPCISBizStep[1].equals("departuring")) {
                return true;
            }
            return false;
        } else
            return false;
    }


    public Bitmap bitmapSizeByScall(Bitmap bitmapIn, float scall_zero_to_one_f) {

        Bitmap bitmapOut = Bitmap.createScaledBitmap(bitmapIn,
                Math.round(bitmapIn.getWidth() * scall_zero_to_one_f),
                Math.round(bitmapIn.getHeight() * scall_zero_to_one_f), false);

        return bitmapOut;
    }

    private Bitmap markerIconResToBitmap(int res) {
        Bitmap orgImage =
                BitmapFactory.decodeResource(getResources(), res);
        return bitmapSizeByScall(orgImage, 0.35f);  // Icon Image Scale
    }

    /**
     * 마커
     **/
    private void addMarkersToMap() {


        mMyCar = googleMap.addMarker(new MarkerOptions()
                .position(myCar)
                .title("내 차")
                .snippet("내 차")
                .icon(BitmapDescriptorFactory.fromBitmap(markerIconResToBitmap(R.drawable.car))));

        mAccidentLoc01 = googleMap.addMarker(new MarkerOptions()
                .position(accidentLoc01)
                .title("사고지점")
                .snippet("사고지점")
                .icon(BitmapDescriptorFactory.fromBitmap(markerIconResToBitmap(R.drawable.warning))));

        mAccidentLoc02 = googleMap.addMarker(new MarkerOptions()
                .position(accidentLoc02)
                .title("사고지점")
                .snippet("사고지점")
                .icon(BitmapDescriptorFactory.fromBitmap(markerIconResToBitmap(R.drawable.warning))));

        if (mEPCISBizStep[0] == null)  Log.d(TAG, "am01 : " + mEPCISBizStep[0] + " ,am02 : " + mEPCISBizStep[1]);
        else if (mEPCISBizStep[0].equals("departuring")) {
            mAmbulance01 = googleMap.addMarker(new MarkerOptions()
                    .position(ambulance01)
                    .title("응급차1")
                    .snippet("도곡 1호차")
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                    .icon(BitmapDescriptorFactory.fromBitmap(markerIconResToBitmap(R.drawable.ambulance))));
        } else {
            Log.d(TAG, "mAmbulance01 : remove");
            try {
                mAmbulance01.remove();
            } catch (Exception e) {
                Log.d(TAG, "mAmbulance01 is null");
            }
        }

        if (mEPCISBizStep[1] == null)  Log.d(TAG, "am01 : " + mEPCISBizStep[0] + " ,am02 : " + mEPCISBizStep[1]);
        else if (mEPCISBizStep[1].equals("departuring")) {
            mAmbulance02 = googleMap.addMarker(new MarkerOptions()
                    .position(ambulance02)
                    .title("응급차2")
                    .snippet("도곡 2호차")
                    .icon(BitmapDescriptorFactory.fromBitmap(markerIconResToBitmap(R.drawable.ambulance))));
        } else {
            Log.d(TAG, "mAmbulance02 : remove");
            try {
                mAmbulance02.remove();
            } catch (Exception e) {
                Log.d(TAG, "mAmbulance02 is null");
            }
        }


    }

    private void addCircleToMap(boolean near) {
        if (near) {
            circleOptions = new CircleOptions()
                    .center(myCar)   //set center
                    .radius(200)   //set radius in meters
                    .strokeWidth(5)
                    .strokeColor(Color.RED)
                    .fillColor(Color.parseColor("#50F8bbd0"));

        } else {
            circleOptions = new CircleOptions()
                    .center(myCar)   //set center
                    .radius(200)   //set radius in meters
                    .strokeWidth(5)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.parseColor("#500084d3"));

        }
        mMyCarCircle = googleMap.addCircle(circleOptions);

    }


    // View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    /**
     * 마커 클릭했을 때 이미지 바꿔주는 것
     **/
//    private Marker addMarker(Marker marker, boolean isSelectedMarker) {
//        double lat = marker.getPosition().latitude;
//        double lon = marker.getPosition().longitude;
//        MarkerItem temp = new MarkerItem(lat, lon, "현재위치");     //클릭했을 때 어떤 String으로 나오게 할 것인가??
//        return addMarker(temp, isSelectedMarker);
//
//    }
//
//    private void changeSelectedMarker(Marker marker) {
//        // 선택했던 마커 되돌리기
//        if (marker != null) {
//            addMarker(marker, false);
//            marker.remove();
//        }
//
//        // 선택한 마커 표시
//        if (marker != null) {
//            marker = addMarker(marker, true);
//            marker.remove();
//        }
//
//
//    }
    @Override
    public void onMapClick(LatLng latLng) {
        /**마커 클릭했을 때 이미지 바꿔주는 것**/
//        changeSelectedMarker(null);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
        googleMap.animateCamera(center);
        /**마커 클릭했을 때 이미지 바꿔주는 것**/
//        changeSelectedMarker(marker);

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.stop();
        tts.shutdown();
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        Log.d(TAG, "enter to disc function");
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if (unit == "meter") {
            dist = dist * 1609.344;
        }

        return (dist);
    }


    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Customer Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    private void toast(String str) {
        Toast.makeText(CustomerActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    private void speakTTS(String str) {
        tts.speak(str, TextToSpeech.QUEUE_ADD, null);
    }

    public void tts(String str) {
//        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();


        //http://stackoverflow.com/a/29777304
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(str);
        } else {
            ttsUnder20(str);
        }
    }

    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }


    class BackThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {

            }
        }
    }


}
