package com.hrm.util;

public final class GeoUtil {
    private static final double EARTH_RADIUS_METERS = 6371000.0;

    private GeoUtil() {
    }

    public static double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double startLat = Math.toRadians(lat1);
        double endLat = Math.toRadians(lat2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(startLat) * Math.cos(endLat)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }

    public static boolean isValidLatitude(Double lat) {
        return lat != null && !lat.isNaN() && !lat.isInfinite() && lat >= -90 && lat <= 90;
    }

    public static boolean isValidLongitude(Double lng) {
        return lng != null && !lng.isNaN() && !lng.isInfinite() && lng >= -180 && lng <= 180;
    }

    public static boolean isWithinRadius(double distanceMeters, int radiusMeters) {
        return radiusMeters > 0 && distanceMeters <= radiusMeters;
    }
}
