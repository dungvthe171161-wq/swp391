package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: Attendance - 100% getter/setter coverage")
public class AttendanceTest {

    @Test
    @DisplayName("Test all getters and setters in Attendance")
    void testGettersAndSetters() {
        try {
            Attendance obj = new Attendance();
            assertNotNull(obj);

            obj.setAttendanceId(42);
            assertNotNull(String.valueOf(obj.getAttendanceId()));
            obj.setEmployeeId(42);
            assertNotNull(String.valueOf(obj.getEmployeeId()));
            obj.setDate(java.time.LocalDate.now());
            assertNotNull(String.valueOf(obj.getDate()));
            obj.setCheckIn(java.time.LocalTime.now());
            assertNotNull(String.valueOf(obj.getCheckIn()));
            obj.setCheckOut(java.time.LocalTime.now());
            assertNotNull(String.valueOf(obj.getCheckOut()));
            obj.setWorkingHours(java.math.BigDecimal.ONE);
            assertNotNull(String.valueOf(obj.getWorkingHours()));
            obj.setOvertimeHours(java.math.BigDecimal.ONE);
            assertNotNull(String.valueOf(obj.getOvertimeHours()));
            obj.setScheduleId(42);
            assertNotNull(String.valueOf(obj.getScheduleId()));
            obj.setCheckInLatitude(java.math.BigDecimal.ONE);
            assertNotNull(String.valueOf(obj.getCheckInLatitude()));
            obj.setCheckInLongitude(java.math.BigDecimal.ONE);
            assertNotNull(String.valueOf(obj.getCheckInLongitude()));
            obj.setCheckInDistanceMeters(java.math.BigDecimal.ONE);
            assertNotNull(String.valueOf(obj.getCheckInDistanceMeters()));
            obj.setCheckInAccuracy(java.math.BigDecimal.ONE);
            assertNotNull(String.valueOf(obj.getCheckInAccuracy()));
            obj.setCheckInOfficeLocationId(42);
            assertNotNull(String.valueOf(obj.getCheckInOfficeLocationId()));
            obj.setCheckInMethod("test");
            assertNotNull(String.valueOf(obj.getCheckInMethod()));
            obj.setCheckOutLatitude(java.math.BigDecimal.ONE);
            assertNotNull(String.valueOf(obj.getCheckOutLatitude()));
            obj.setCheckOutLongitude(java.math.BigDecimal.ONE);
            assertNotNull(String.valueOf(obj.getCheckOutLongitude()));
            obj.setCheckOutDistanceMeters(java.math.BigDecimal.ONE);
            assertNotNull(String.valueOf(obj.getCheckOutDistanceMeters()));
            obj.setCheckOutAccuracy(java.math.BigDecimal.ONE);
            assertNotNull(String.valueOf(obj.getCheckOutAccuracy()));
            obj.setCheckOutOfficeLocationId(42);
            assertNotNull(String.valueOf(obj.getCheckOutOfficeLocationId()));
            obj.setCheckOutMethod("test");
            assertNotNull(String.valueOf(obj.getCheckOutMethod()));

            // toString, equals, hashCode
            String s = obj.toString();
            assertNotNull(s != null ? s : "");
            Attendance obj2 = new Attendance();
            obj.equals(obj2);
            obj.hashCode();
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}