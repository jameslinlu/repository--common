package com.commons.common.utils.geoip;

import java.io.Serializable;

public class GeoIp implements Serializable {
    //    Asia China Jiangsu Nanjing 32.0617 118.7778
//   O(Organization组织名称)Asia
//　　L(Locality城市或区域名称)Nanjing
//　　ST(State州或省份名称)Jiangsu
//　　C(Country国家名称）China
    private String continent;//大洲
    private String city;//城市
    private String state;//省份
    private String country;//国家
    private Double lat;//精度
    private Double lng;//纬度
    private String ip;

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
