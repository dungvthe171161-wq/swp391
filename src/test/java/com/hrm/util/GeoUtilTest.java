package com.hrm.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: GeoUtil - 100% Branch Coverage")
public class GeoUtilTest {

    @Test
    void testDistanceMeters() {
        // Zero distance (same points)
        double dist = GeoUtil.distanceMeters(10.7769, 106.7009, 10.7769, 106.7009);
        assertEquals(0.0, dist, 0.01);

        // Distance between Hanoi and HCM
        double hanoiHcm = GeoUtil.distanceMeters(21.0285, 105.8542, 10.7769, 106.7009);
        assertTrue(hanoiHcm > 1000000);
    }

    @Test
    void testIsValidLatitude() {
        assertTrue(GeoUtil.isValidLatitude(0.0));
        assertTrue(GeoUtil.isValidLatitude(90.0));
        assertTrue(GeoUtil.isValidLatitude(-90.0));
        assertFalse(GeoUtil.isValidLatitude(null));
        assertFalse(GeoUtil.isValidLatitude(Double.NaN));
        assertFalse(GeoUtil.isValidLatitude(Double.POSITIVE_INFINITY));
        assertFalse(GeoUtil.isValidLatitude(90.1));
        assertFalse(GeoUtil.isValidLatitude(-90.1));
    }

    @Test
    void testIsValidLongitude() {
        assertTrue(GeoUtil.isValidLongitude(0.0));
        assertTrue(GeoUtil.isValidLongitude(180.0));
        assertTrue(GeoUtil.isValidLongitude(-180.0));
        assertFalse(GeoUtil.isValidLongitude(null));
        assertFalse(GeoUtil.isValidLongitude(Double.NaN));
        assertFalse(GeoUtil.isValidLongitude(Double.NEGATIVE_INFINITY));
        assertFalse(GeoUtil.isValidLongitude(180.1));
        assertFalse(GeoUtil.isValidLongitude(-180.1));
    }

    @Test
    void testIsWithinRadius() {
        assertTrue(GeoUtil.isWithinRadius(50, 100));
        assertTrue(GeoUtil.isWithinRadius(100, 100));
        assertFalse(GeoUtil.isWithinRadius(150, 100));
        assertFalse(GeoUtil.isWithinRadius(50, 0));
        assertFalse(GeoUtil.isWithinRadius(50, -10));
    }
}
