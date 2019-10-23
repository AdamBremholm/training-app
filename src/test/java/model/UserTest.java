package model;

import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.*;

public class UserTest {

    private User mockUser1;
    private User mockUser2;
    private User mockUser3;
    private static final double DELTA = 0.001;

    @Before
    public void setUp() {


         mockUser1 = new User.Builder("mrMock", "mock@mockmail.com", "mr")
                .withUserId("mockUserId")
                .withHeight(180)
                .withWeight(80)
                .build();
         mockUser2 = new User.Builder("mrsMcMock", "mrs@mockmail.com", "mrs")
                .withHeight(165)
                .withWeight(60)
                .build();
         mockUser3 = new User.Builder("kidMock", "kid@mockmail.com", "kid")
                .withUserId("mockUserId3")
                .build();

    }

    @Test
    public void constructorSetsUserIdIfNotProvided() {
        assertNotNull(mockUser2.getUserId());
    }

    @Test
    public void constructorDefaultsWeightAndHeightToZeroIfNotProvided() {
       assertEquals(0, mockUser3.getHeight(), DELTA);
        assertEquals(0, mockUser3.getWeight(), DELTA);
    }

    @Test (expected = IllegalArgumentException.class)
    public void IllegalArgumentExceptionIsThrownIfNullParameters() {
        new User.Builder(null, "kid@mockmail.com", "kid").build();
       fail();
    }

    @Test (expected = IllegalArgumentException.class)
    public void IllegalArgumentExceptionIsThrownIfNullParameters2() {
        new User.Builder("hej", null, "kid").build();
        fail();
    }

    @Test (expected = IllegalArgumentException.class)
    public void IllegalArgumentExceptionIsThrownIfNullParameters3() {
        new User.Builder("hej", "hej", null).build();
        fail();
    }

    @Test (expected = NumberFormatException.class)
    public void NumberFormatExceptionIsThrownIfNegativeValues() {
        mockUser1 = new User.Builder("mrMock", "mock@mockmail.com", "mr")
                .withUserId("mockUserId")
                .withHeight(-1.5)
                .build();
        fail();
    }

    @Test (expected = NumberFormatException.class)
    public void NumberFormatExceptionIsThrownIfNegativeValues2() {
        mockUser1 = new User.Builder("mrMock", "mock@mockmail.com", "mr")
                .withUserId("mockUserId")
                .withWeight(-2)
                .build();
        fail();
    }

    @Test
    public void getUserId() {
        assertEquals("mockUserId", mockUser1.getUserId());
    }

    @Test
    public void getUsername() {
        assertEquals("mrMock", mockUser1.getUsername());
    }

    @Test
    public void getEmail() {
        assertEquals("mock@mockmail.com", mockUser1.getEmail());
    }

    @Test
    public void getPassword() {
        assertEquals("mr", mockUser1.getPassword());
    }

    @Test
    public void getWeight() {
        assertEquals(80, mockUser1.getWeight(), DELTA);
    }

    @Test
    public void getHeight() {
        assertEquals(180, mockUser1.getHeight(), DELTA);
    }

    @Test
    public void randomId() {
        assertNotNull(mockUser1.randomId());
    }

    @Test
    public void toStringTest() {
        Reflectable.getFieldNames(mockUser1.getClass().getDeclaredFields()).forEach((field)->  assertTrue(mockUser1.toString().contains(field)));
    }

    @Test
    public void fieldsEnumContainsNonComputedFieldsOfParent() {

        assertTrue(mockUser1.fieldsEnumContainsNonComputedFieldsOfParent(mockUser1, EnumSet.allOf(ImmutableFields.class)));
    }
}