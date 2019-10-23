package model;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class SetTest {

    private Set set;
    private Set set2;
    private Set set3;
    private Set set4;
    private static final double DELTA = 0.001;

    @Before
    public void setUp() {
        set = new Set.Builder()
                .withRepetitions(5)
                .withWeight(10)
                .build();

        set2 = new Set.Builder()
                .withSetId("mock2")
                .withRepetitions(4)
                .withWeight(20)
                .build();
        set3 = new Set.Builder()
                .withRepetitions(4)
                .withWeight(20)
                .build();
        set4 = new Set.Builder()
                .build();
    }

    @Test
    public void constructorGeneratesSetIdIfNoneProvided() {
        assertNotNull(set.getSetId());
    }

    @Test
    public void noRepetitionsOrWeightDefaultsToZero() {
        assertEquals(0, set4.getWeight(), DELTA);
        assertEquals(0, set4.getRepetitions(), DELTA);
    }

    @Test (expected = NumberFormatException.class)
    public void NumberFormatExceptionIsThrownIfNegativeValues() {
        set = new Set.Builder()
                .withRepetitions(-5)
                .build();
        fail();
    }

    @Test (expected = NumberFormatException.class)
    public void NumberFormatExceptionIsThrownIfNegativeValues2() {
        set = new Set.Builder()
                .withWeight(-5)
                .build();
        fail();
    }

    @Test
    public void getRepetitions() {
        assertEquals(5, set.getRepetitions());
    }

    @Test
    public void getWeight() {
        assertEquals(10, set.getWeight(), DELTA);
    }

    @Test
    public void getSetId() {
        assertEquals("mock2", set2.getSetId());
    }

    @Test
    public void totalWeightPerSet() {
        assertEquals(50, set.totalWeightPerSet(), DELTA);
    }

    @Test
    public void compareTo() {
        assertEquals(-1, set.compareTo(set2));
        assertEquals(1, set2.compareTo(set));
        assertEquals(0, set2.compareTo(set3));
    }

    @Test
    public void toStringTest() {
        Reflectable.getFieldNames(set.getClass().getDeclaredFields()).forEach((field)->  assertTrue(set.toString().contains(field)));
    }

    @Test
    public void fieldsEnumContainsNonComputedFieldsOfParent() {
        assertTrue(set.fieldsEnumContainsNonComputedFieldsOfParent(set, EnumSet.allOf(ComputedFields.class)));
    }
}