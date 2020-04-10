package com.map.track.model;

/**
 * 定位模型 对象
 */
public class LocationModel {
    public double latitude;
    public double longitude;
    public float radius;
    public float direction;
    public float course;//手机方向传感器的方向
    public String adCode;
    public String addrStr;
    public String country;
    public String countryCode;
    public String province;
    public String city;
    public String cityCode;
    public String district;
    public String street;
    public String streetNumber;
    public String address;
    public String adcode;
    public String town;

    @Override
    public String toString() {
        return "LocationModel{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius +
                ", direction=" + direction +
                ", course=" + course +
                ", adCode='" + adCode + '\'' +
                ", addrStr='" + addrStr + '\'' +
                ", country='" + country + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", district='" + district + '\'' +
                ", street='" + street + '\'' +
                ", streetNumber='" + streetNumber + '\'' +
                ", address='" + address + '\'' +
                ", adcode='" + adcode + '\'' +
                ", town='" + town + '\'' +
                '}';
    }
}
