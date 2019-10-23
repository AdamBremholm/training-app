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
        Set setB = new Set.Builder().withRepetitions(5).withWeight(55).build();
        Set setC = new Set.Builder().withRepetitions(5).withWeight(60).build();
        Set setD = new Set.Builder().withRepetitions(3).withWeight(40).build();;
        Set setE = new Set.Builder().withRepetitions(5).withWeight(40).build();
        Set setF =new Set.Builder().withRepetitions(5).withWeight(45).build();

         squats = new Exercise.Builder(Exercise.Type.SQUAT, Arrays.asList(setA, setA, setA)).build();
         benchPress = new Exercise.Builder(Exercise.Type.BENCHPRESS, Arrays.asList(setB, setB, setB)).build();
         deadLift = new Exercise.Builder(Exercise.Type.DEADLIFT, Collections.singletonList(setC)).build();
         powerClean = new Exercise.Builder(Exercise.Type.POWERCLEAN, Arrays.asList(setE, setE, setE)).build();
         press = new Exercise.Builder(Exercise.Type.PRESS, Arrays.asList(setD, setD, setF)).build();


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

    @Test
    public void IllegalArgumentExceptionIsThrownIfEndTimeIsBeforeStartTime() {

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