package com.ajeyone.studyretrofit.data;

public class BaiduPOIBean {
    public String name;
    public BaiduLocation location;
    public String address;
    public String province;
    public String city;
    public String area;
    public String street_id;
    public String uid;

    @Override
    public String toString() {
        return "BaiduPOIBean{" +
                "name='" + name + '\'' +
                ", location=" + location +
                ", address='" + address + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", street_id='" + street_id + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
