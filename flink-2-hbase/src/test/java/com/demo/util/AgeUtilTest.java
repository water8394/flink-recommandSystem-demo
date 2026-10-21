package com.demo.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AgeUtilTest {

    @Test
    public void mapsDecadeBuckets() {
        assertEquals("10s", AgeUtil.getAgeType("10"));
        assertEquals("10s", AgeUtil.getAgeType("19"));
        assertEquals("20s", AgeUtil.getAgeType("20"));
        assertEquals("30s", AgeUtil.getAgeType("35"));
        assertEquals("40s", AgeUtil.getAgeType("40"));
        assertEquals("50s", AgeUtil.getAgeType("59"));
    }

    @Test
    public void agesOutsideTheTrackedDecadesShareTheZeroBucket() {
        assertEquals("0s", AgeUtil.getAgeType("9"));
        assertEquals("0s", AgeUtil.getAgeType("60"));
        assertEquals("0s", AgeUtil.getAgeType("0"));
    }

    @Test(expected = NumberFormatException.class)
    public void rejectsNonNumericAge() {
        AgeUtil.getAgeType("abc");
    }
}
