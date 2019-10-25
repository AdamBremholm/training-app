package model;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class NoOverWriteMapTest {

    @Test (expected = IllegalArgumentException.class)
    public void putThrowsExceptionWhenAddingDuplicatesInsteadOfOverwrite() {
        //noinspection MismatchedQueryAndUpdateOfCollection
        Map<String, String> testMap = new NoOverWriteMap<>();
        //noinspection OverwrittenKey
        testMap.put("1", "1");
        //noinspection OverwrittenKey
        testMap.put("1", "2");
        testMap.get("1");
        fail();
    }
}