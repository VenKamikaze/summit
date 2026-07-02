package org.awiki.kamikaze.summit.util;

import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilsTest {

    @Test
    void testToParameterMap() {
        String pageParams = "key1:value1,key2:value2";
        MultiValueMap<String, String> result = StringUtils.toParameterMap(pageParams);

        assertEquals(2, result.size());
        assertEquals("value1", result.getFirst("key1"));
        assertEquals("value2", result.getFirst("key2"));
    }

    @Test
    void testToParameterMapEmpty() {
        MultiValueMap<String, String> result = StringUtils.toParameterMap("");
        assertTrue(result.isEmpty());
    }

    @Test
    void testToParameterMapNull() {
        MultiValueMap<String, String> result = StringUtils.toParameterMap(null);
        assertTrue(result.isEmpty());
    }
}
