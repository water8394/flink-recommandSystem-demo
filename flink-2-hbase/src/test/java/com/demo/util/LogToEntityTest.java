package com.demo.util;

import com.demo.domain.LogEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LogToEntityTest {

    @Test
    public void parsesUserProductTimeAndAction() {
        LogEntity log = LogToEntity.getLog("12,34,1600000000,1");
        assertEquals(12, log.getUserId());
        assertEquals(34, log.getProductId());
        assertEquals(Long.valueOf(1600000000L), log.getTime());
        assertEquals("1", log.getAction());
    }

    @Test
    public void returnsNullWhenTheMessageHasTooFewFields() {
        assertNull(LogToEntity.getLog("only-one"));
    }
}
