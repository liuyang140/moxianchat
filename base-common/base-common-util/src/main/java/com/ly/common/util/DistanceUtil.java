package com.ly.common.util;

public class DistanceUtil {
    private static final double EARTH_RADIUS = 6371.0; // km

    public static double calculate(double lat1, double lon1, double lat2, double lon2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double deltaLat = radLat1 - radLat2;
        double deltaLon = Math.toRadians(lon1 - lon2);

        double a = Math.pow(Math.sin(deltaLat / 2), 2)
                 + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(deltaLon / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS * c;
    }
}