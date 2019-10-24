package model;

import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;

public class WorkoutTest {

    private Workout mockWorkout1;
    private Workout mockWorkout2;
    private Workout mockWorkout3;
    private Workout mockWorkout4;
    private User mockUser1;
    private User mockUser2;
    private User mockUser3;
    private Map<String, Exercise> exercisesA;
    private Map<String, Exercise> exercisesB;
    private Exercise squats;
    private Exercise benchPress;
    private Exercise deadLift;
    private Exercise powerClean;
    private Exercise press;

    @Before
    public void setUp() throws Exception {

         mockUser1 = new User.Builder("mrMock", "mock@mockmail.com", "mr")
                .withUserId("mockUserId")
                .withHeight(180)
                .withWeight(80)
                .build();
         mockUser2 = new User.Builder("mrsMcMock", "mrs@mockmail.com", "mrs")
                .withUserId("mockUserId2")
                .withHeight(165)
                .withWeight(60)
                .build();
         mockUser3 = new User.Builder("kidMock", "kid@mockmail.com", "kid")
                .withUserId("mockUserId3")
                .withHeight(110)
                .withWeight(50)
                .build();

        Set setA = new Set.Builder().withRepetitions(5).withWeight(60).build();
        Set setA2 = new Set.Builder().withRepetitions(5).withWeight(60).build();
        Set setA3 = new Set.Builder().withRepetitions(5).withWeight(60).build();
        Set setB = new Set.Builder().withRepetitions(5).withWeight(55).build();
        Set setB2 = new Set.Builder().withRepetitions(5).withWeight(55).build();
        Set setB3 = new Set.Builder().withRepetitions(5).withWeight(55).build();
        Set setC = new Set.Builder().withRepetitions(5).withWeight(60).build();
        Set setD = new Set.Builder().withRepetitions(3).withWeight(40).build();;
        Set setD2 = new Set.Builder().withRepetitions(3).withWeight(40).build();;
        Set setE = new Set.Builder().withRepetitions(5).withWeight(40).build();
        Set setE2 = new Set.Builder().withRepetitions(5).withWeight(40).build();
        Set setE3 = new Set.Builder().withRepetitions(5).withWeight(40).build();
        Set setF =new Set.Builder().withRepetitions(5).withWeight(45).build();

        Map<String, Set> sets1 = new NoOverWriteMap<>();
        sets1.put(setA.getSetId(), setA);
        sets1.put(setA2.getSetId(), setA2);
        sets1.put(setA3.getSetId(), setA3);

        Map<String, Set> sets2 = new NoOverWriteMap<>();
        sets2.put(setB.getSetId(),setB);
        sets2.put(setB2.getSetId(), setB2);
        sets2.put(setB3.getSetId(), setB3);

        Map<String, Set> sets3 = new NoOverWriteMap<>();
        sets3.put(setC.getSetId(),setC);

        Map<String, Set> sets4 = new NoOverWriteMap<>();
        sets4.put(setE.getSetId(),setE);
        sets4.put(setE2.getSetId(), setE2);
        sets4.put(setE3.getSetId(), setE3);

        Map<String, Set> sets5 = new NoOverWriteMap<>();
        sets5.put(setD.getSetId(),setD);
        sets5.put(setD2.getSetId(), setD2);
        sets5.put(setF.getSetId(), setF);



         squats = new Exercise.Builder(Exercise.Type.SQUAT, sets1).build();
         benchPress = new Exercise.Builder(Exercise.Type.BENCHPRESS, sets2).build();
         deadLift = new Exercise.Builder(Exercise.Type.DEADLIFT, sets3).build();
         powerClean = new Exercise.Builder(Exercise.Type.POWERCLEAN, sets4).build();
         press = new Exercise.Builder(Exercise.Type.PRESS, sets5).build();


        exercisesA = new HashMap<>();
        exercisesA.put(squats.getExerciseId(), squats);
        exercisesA.put(benchPress.getExerciseId(), benchPress);
        exercisesA.put(deadLift.getExerciseId(), deadLift);

        exercisesB = new HashMap<>();
        exercisesB.put(squats.getExerciseId(), squats);
        exercisesB.put(powerClean.getExerciseId(), powerClean);
        exercisesB.put(press.getExerciseId(), press);


         mockWorkout1 = new Workout.Builder(mockUser1, exercisesA)
                .withStartTime(Instant.parse("2019-10-03T10:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-03T10:16:30.00Z"))
                .withWorkoutId("mockId1")
                .build();
         mockWorkout2 = new Workout.Builder(mockUser2, exercisesB)
                .withStartTime(Instant.parse("2019-10-04T10:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30.00Z"))
                .build();
         mockWorkout3 = new Workout.Builder(mockUser3, exercisesA)
                .withStartTime(Instant.parse("2019-10-04T10:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30.00Z"))
                .build();
         mockWorkout4 = new Workout.Builder(mockUser1, exercisesB)
                .withStartTime(Instant.parse("2019-10-05T15:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-05T16:16:30.00Z"))
                .build();
    }

    @Test (expected =  IllegalArgumentException.class)
    public void IllegalArgumentExceptionIsThrownIfEndTimeIsBeforeStartTime() {
        mockWorkout4 = new Workout.Builder(mockUser1, exercisesB)
                .withStartTime(Instant.parse("2019-10-05T15:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-04T16:16:30.00Z"))
                .build();
        fail();
    }

    @Test
    public void okToConstructWithOnlyStartTimeOnlyEndTimeOrNoTime() {
        mockWorkout4 = new Workout.Builder(mockUser1, exercisesB)
                .withStartTime(Instant.parse("2019-10-05T15:15:30.00Z"))
                .build();
        mockWorkout4 = new Workout.Builder(mockUser1, exercisesB)
                .withEndTime(Instant.parse("2019-10-05T15:15:30.00Z"))
                .build();
        mockWorkout4 = new Workout.Builder(mockUser1, exercisesB)
                .build();
    }



    @Test(expected =  IllegalArgumentException.class)
    public void IllegalArgumentExceptionIsThrownIfMandatoryParamsNull() {
        mockWorkout4 = new Workout.Builder(null, exercisesB)
                .withStartTime(Instant.parse("2019-10-05T15:15:30.00Z"))
                .build();
        fail();
    }

    @Test(expected =  IllegalArgumentException.class)
    public void IllegalArgumentExceptionIsThrownIfMandatoryParamsNull2() {
        mockWorkout4 = new Workout.Builder(mockUser1, null)
                .withStartTime(Instant.parse("2019-10-05T15:15:30.00Z"))
                .build();
        fail();
    }

    @Test
    public void getWorkoutId() {
        assertEquals("mockId1", mockWorkout1.getWorkoutId());
    }

    @Test
    public void getUser() {
        assertEquals(mockUser1, mockWorkout1.getUser());
    }

    @Test
    public void getStartTime() {
        assertEquals(Instant.parse("2019-10-03T10:15:30.00Z"), mockWorkout1.getStartTime());
    }

    @Test
    public void getEndTime() {
        assertEquals(Instant.parse("2019-10-03T10:16:30.00Z"), mockWorkout1.getEndTime());
    }

    @Test
    public void randomId() {
        assertNotNull(mockWorkout1.randomId());
    }

    @Test
    public void getExercises() {
        assertEquals(exercisesA, mockWorkout1.getExercises());
    }

    @Test
    public void getHeaviestExercise() {
        assertEquals(squats, mockWorkout2.getHeaviestExercise());
    }

    @Test
    public void getTotalRepetitions() {
        assertEquals(35, mockWorkout1.getTotalRepetitions());
    }

    @Test
    public void liftedPerWorkOut() {
        assertEquals(2025.0, mockWorkout1.liftedPerWorkOut(), 0.001);
    }

    @Test
    public void fieldsEnumContainsNonComputedFieldsOfParent() {

        assertTrue(mockWorkout1.fieldsEnumContainsNonComputedFieldsOfParent(mockWorkout1, EnumSet.allOf(ImmutableFields.class)));
    }

    @Test
    public void calculateHeaviestExercisePerWorkout() {
        assertEquals(squats, mockWorkout2.calculateHeaviestExercisePerWorkout());
    }

    @Test
    public void calculateTotalRepetitionsPerWorkout() {
        assertEquals(35, mockWorkout1.calculateTotalRepetitionsPerWorkout());
    }

    @Test
    public void testToString() {
       Reflectable.getFieldNames(mockWorkout1.getClass().getDeclaredFields()).forEach((field)->  assertTrue(mockWorkout1.toString().contains(field)));
    }
}