package model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExerciseTest {

    private Set setA;
    private Set setD;
    private Exercise exercise;
    private String exerciseId;
    private Exercise.Type type;
    private static final double DELTA = 0.001;

    @Before
    public void setUp() {

        exerciseId = "1e";
        type = Exercise.Type.SQUAT;
        setA = new Set.Builder().withRepetitions(5).withWeight(60).withSetId("1s").build();
        setD = new Set.Builder().withRepetitions(3).withWeight(40).build();
        exercise = new Exercise.Builder(type, Arrays.asList(setA, setA, setD)).withExerciseId(exerciseId).build();


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
        assertEquals(Arrays.asList(setA, setA, setD), exercise.getSets());
    }

    @Test
    public void liftedPerExercise() {
        assertEquals(720, exercise.liftedPerExercise(), DELTA);
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
        exercise.fieldsEnumContainsNonComputedFieldsOfParent(exercise, EnumSet.allOf(ComputedFields.class));
    }

    @Test
    public void toStringTest() {
        Reflectable.getFieldNames(exercise.getClass().getDeclaredFields()).forEach((field)->  assertTrue(exercise.toString().contains(field)));
    }
}