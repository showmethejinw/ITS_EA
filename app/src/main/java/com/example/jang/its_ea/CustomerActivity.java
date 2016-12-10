package com.example.jang.its_ea;

/**
 * Created by jang on 2016-12-01.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jang.its_ea.helper.MarkerItem;
import com.example.jang.its_ea.helper.OnEventListener;
import com.example.jang.its_ea.helper.RequestQuery;
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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

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
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback,GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    static final LatLng SEOUL = new LatLng(37.56, 126.97);
    static final LatLng KAIST_SW = new LatLng(37.483762, 127.043962);
    private static final String TAG = "Customer";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final int REQUEST_CODE_LOCATION = 2;
    private double locationX;
    private double locationY;

    private static final String SGTIN1 = "urn:epc:id:sgtin:4012345.077889.25";
    private static final String SGTIN2 = "urn:epc:id:sgtin:4012345.077889.26";

    private double ambulanceLocationX, ambulanceLocationY;
    private String nodeValueArray[];

    private GoogleMap googleMap;

    private ArrayList<MarkerItem> ambulance;        //marker
    private TextView tv_marker;
    private View marker_root_view;
    private Marker mAmbulance01, mAmbulance02, mMyCar, mAccidentLocation;
    private LatLng ambulance01, ambulance02, myCar, accidentLocation;
    private Circle mMyCarCircle;

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
        googleMap.setMyLocationEnabled(true);

        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 5));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KAIST_SW, 12));

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(5), 2000, null);

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);


//        setCustomMarkerView();
//        getSampleMarkerItems();

        queryEvent();
        updateMarkerPosition();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_layout);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        queryEvent();
    }

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


    private void CheckPermission() {

        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(CustomerActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(CustomerActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                ActivityCompat.requestPermissions(CustomerActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_LOCATION);

                return;
            }
            ActivityCompat.requestPermissions(CustomerActivity.this,
                    new String[]{android.Manifest.permission.WRITE_CONTACTS},
                    REQUEST_CODE_LOCATION);
            return;
        }

        hasWriteContactsPermission = ContextCompat.checkSelfPermission(CustomerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(CustomerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(CustomerActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION);

                return;
            }
            ActivityCompat.requestPermissions(CustomerActivity.this,
                    new String[]{android.Manifest.permission.WRITE_CONTACTS},
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
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        Marker seoul = googleMap.addMarker(new MarkerOptions().position(CURRENT_LOCATION)
                .title("사용자 위치"));

        queryEvent();
        updateMarkerPosition();
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
        NodeList descNodes = doc.getElementsByTagName("bizLocation");
        String nodeValue[] = new String[5];
        for (int i = 0; i < descNodes.getLength(); i++) {
            int j = 0;
            for (Node node = descNodes.item(i).getFirstChild(); node != null; node = node.getNextSibling()) { //첫번째 자식을 시작으로 마지막까지 다음 형제를 실행

                nodeValue[j] = node.getTextContent();
                Log.d(TAG, "nodeValue [" + j + "]" + nodeValue[j]);  //결과값
                ++j;
            }
        }

        String gpsLocation = nodeValue[3];

        nodeValueArray = gpsLocation.replace(" ","").split(",");
        ambulanceLocationX = Double.parseDouble(nodeValueArray[0]);
        ambulanceLocationY = Double.parseDouble(nodeValueArray[1]);
        Log.d(TAG, "ambulanceLocationX : " + ambulanceLocationX +" ,ambulanceLocationY : " + ambulanceLocationY);
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
        epcis.execute("eventCountLimit=1&MATCH_epc=" + SGTIN1);
    }


    /**
     * Requests location updates from the FusedLocationApi.
     */
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
            Log.d(TAG, "Start Location update");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            Toast.makeText(this,"Location Unavailable",Toast.LENGTH_LONG).show();
        }
    }

    /**마커 GPS 좌표 **/
    private void updateMarkerPosition() {
        ambulance01 = new LatLng(ambulanceLocationX , ambulanceLocationY);
        ambulance02 = new LatLng(37.484565, 127.033963);
        myCar = new LatLng(locationX, locationY);
        accidentLocation = new LatLng(37.490678, 127.048493);

        addMarkersToMap();
        addCircleToMap();
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
        return bitmapSizeByScall(orgImage, 0.5f);
    }

    /**마커**/
    private void addMarkersToMap() {

        mAmbulance01 = googleMap.addMarker(new MarkerOptions()
                .position(ambulance01)
                .title("응급차1")
                //.snippet("Population: 2,074,200")
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                .icon(BitmapDescriptorFactory.fromBitmap(markerIconResToBitmap(R.drawable.ambulance))));


        mAmbulance02 = googleMap.addMarker(new MarkerOptions()
                .position(ambulance02)
                .title("응급차2")
                //.snippet("Population: 4,627,300")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                .icon(BitmapDescriptorFactory.fromBitmap(markerIconResToBitmap(R.drawable.ambulance))));


        mMyCar=  googleMap.addMarker(new MarkerOptions()
                .position(myCar)
                .title("내차")
                //.snippet("Population: 4,627,300")
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                .icon(BitmapDescriptorFactory.fromBitmap(markerIconResToBitmap(R.drawable.car))));

        mAccidentLocation = googleMap.addMarker(new MarkerOptions()
                .position(accidentLocation)
                .title("사고지점")
                //.snippet("Population: 4,137,400")
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.warning)));
                .icon(BitmapDescriptorFactory.fromBitmap(markerIconResToBitmap(R.drawable.warning))));
    }

    private void addCircleToMap() {
        CircleOptions circleOptions = new CircleOptions()
                .center(myCar)   //set center
                .radius(500)   //set radius in meters
                .strokeWidth(5)
                .strokeColor(Color.BLUE)
                .fillColor(Color.parseColor("#500084d3"));

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


    /**마커 클릭했을 때 이미지 바꿔주는 것**/
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
}

