package com.example.jang.its_ea.helper;

/**
 * Created by jang on 2016-12-01.
 */

public class MarkerItem {


    double lat;
    double lon;
    int price;
    String destination;

    public MarkerItem(double lat, double lon, String destination) {
        this.lat = lat;
        this.lon = lon;
        this.destination = destination;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String  destination) {
        this.destination = destination;
    }


}
