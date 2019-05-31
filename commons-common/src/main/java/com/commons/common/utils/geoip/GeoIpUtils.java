package com.commons.common.utils.geoip;

import com.alibaba.fastjson.JSON;
import com.maxmind.db.Reader;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Location;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class GeoIpUtils {
    private DatabaseReader reader = null;
    private static GeoIpUtils utils = null;

    public static synchronized GeoIpUtils getInstance() {
        if (utils == null) {
            utils = new GeoIpUtils();
            utils.initialize();
        }
        return utils;
    }

    public void initialize() {
//        File database = new File("D:\\GeoLite2-City.mmdb");
        try {
            File database = ResourceUtils.getFile(ClassUtils.getDefaultClassLoader().getResource("GeoLite2-City.mmdb"));
            reader = new DatabaseReader.Builder(database)/*....*/.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GeoIp parse(String ip) {

        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = reader.city(ipAddress);
            GeoIp geoIp = new GeoIp();
            geoIp.setIp(ip);
            geoIp.setContinent(response.getContinent().getName());
            geoIp.setCountry(response.getCountry().getName());
            geoIp.setState(response.getMostSpecificSubdivision().getName());
            geoIp.setCity(response.getCity().getName());
            Location location = response.getLocation();
            geoIp.setLat(location.getLatitude());
            geoIp.setLng(location.getLongitude());
            return geoIp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        GeoIp ip = GeoIpUtils.getInstance().parse("221.226.130.5");
        System.out.println(JSON.toJSONString(ip));
    }
}
