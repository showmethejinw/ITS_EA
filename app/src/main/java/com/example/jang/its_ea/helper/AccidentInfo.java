package com.example.jang.its_ea.helper;

import java.io.Serializable;

/**
 * Created by luvsword on 2016-12-03.
 */

public class AccidentInfo implements Serializable{

    private String accidentType;
    private String address;
    private String age;
    private String symptom;
    private String name;
    private String phoneNumber;
    private String hospitalAddress;
    private int count;
    private double addressLat;
    private double addressLon;
    private double hospitalLat;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAddressLat() {
        return addressLat;
    }

    public void setAddressLat(double addressLat) {
        this.addressLat = addressLat;
    }

    public double getAddressLon() {
        return addressLon;
    }

    public void setAddressLon(double addressLon) {
        this.addressLon = addressLon;
    }

    public double getHospitalLat() {
        return hospitalLat;
    }

    public void setHospitalLat(double hospitalLat) {
        this.hospitalLat = hospitalLat;
    }

    public double getHospitalLon() {
        return hospitalLon;
    }

    public void setHospitalLon(double hospitalLon) {
        this.hospitalLon = hospitalLon;
    }

    private double hospitalLon;
    public AccidentInfo()
    {

    }
    public AccidentInfo(String accidentType, String address, String age, String symptom
    , String name, String phoneNumber,  String hospitalAddress)
    {
        this.accidentType = accidentType;
        this.address = address;
        this.age = age;
        this.symptom = symptom;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.hospitalAddress = hospitalAddress;
    }

    public void setAccidentType(String accidentType) {
        this.accidentType = accidentType;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public String getAccidentType() {
        return accidentType;
    }

    public String getAddress() {
        return address;
    }

    public String getAge() {
        return age;
    }

    public String getSymptom() {
        return symptom;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }
}
