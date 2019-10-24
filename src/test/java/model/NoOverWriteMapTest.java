package model;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class NoOverWriteMapTest {

    @Test (expected = IllegalArgumentException.class)
    public void putThrowsExceptionWhenAddingDuplicatesInsteadOfOverwrite() {
        Map<String, String> testMap = new NoOverWriteMap<>();
        testMap.put("1", "1");
        testMap.put("1", "2");
        fail();
    }
}