package model;

import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExerciseTest {

    private Set setA;
    private Exercise exercise;
    private String exerciseId;
    private Exercise.Type type;
    private static final double DELTA = 0.001;
    private Map<String, Set> sets1;

    @Before
    public void setUp() {

        exerciseId = "1e";
        type = Exercise.Type.SQUAT;
        setA = new Set.Builder().withRepetitions(5).withWeight(65).build();
        Set setA2 = new Set.Builder().withRepetitions(5).withWeight(60).build();
        Set setD = new Set.Builder().withRepetitions(3).withWeight(40).build();

        sets1 = new NoOverWriteMap<>();
        sets1.put(setA.getSetId(), setA);
        sets1.put(setA2.getSetId(), setA2);
        sets1.put(setD.getSetId(), setD);

        exercise = new Exercise.Builder(type, sets1).withExerciseId(exerciseId).build();


    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalArgumentExceptionWhenNull() {
        new Exercise.Builder(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalArgumentExceptionWhenNull2() {
        new Exercise.Builder(Exercise.Type.SQUAT, null);
    }

    @Test
    public void getExerciseId() {
        assertEquals(exerciseId, exercise.getExerciseId());
    }

    @Test
    public void getType() {
        assertEquals(type, exercise.getType());
    }

    @Test
    public void getSets() {
        assertEquals(sets1, exercise.getSets());
    }

    @Test
    public void liftedPerExercise() {
        assertEquals(745, exercise.liftedPerExercise(), DELTA);
    }

    @Test
    public void getHeaviestSet() {
        assertEquals(setA, exercise.getHeaviestSet());
    }

    @Test
    public void getTotalRepetitions() {
        assertEquals(13, exercise.getTotalRepetitions());
    }

    @Test
    public void fieldsEnumContainsNonComputedFieldsOfParent() {
        exercise.fieldsEnumContainsNonComputedFieldsOfParent(exercise, EnumSet.allOf(ImmutableFields.class));
    }

    @Test
    public void toStringTest() {
        Reflectable.getFieldNames(exercise.getClass().getDeclaredFields()).forEach((field)->  assertTrue(exercise.toString().contains(field)));
    }
}