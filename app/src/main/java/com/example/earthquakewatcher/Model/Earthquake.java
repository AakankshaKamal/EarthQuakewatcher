package com.example.earthquakewatcher.Model;

public class Earthquake {
    public String place;
    public long time;
    public String detailLink;
    public String type;
    public double magnitude;
    public double latitude;
    public double longitude;

    public Earthquake(String place, long time, String detailLink, String type, double magnitude, double latitude, double longitude) {
        this.place = place;
        this.time = time;
        this.detailLink = detailLink;
        this.type = type;
        this.magnitude = magnitude;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Earthquake() {
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
