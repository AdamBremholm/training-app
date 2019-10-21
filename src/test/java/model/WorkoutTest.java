package model;


import controller.Controller;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class WorkoutTest {




    @Test
    public void givenObject_whenGetsFieldNamesAtRuntime_thenCorrect() {
        User mockUser4 = new User.Builder("mockUser4", "4@mockmail.com", "4")
                .withUserId("mockUserId4")
                .withHeight(110)
                .withWeight(50)
                .build();

        Set setA = new Set.Builder().withRepetitions(5).withWeight(60).withSetId("1s").build();
        Set setB = new Set.Builder().withRepetitions(5).withWeight(55).withSetId("2s").build();
        Set setC = new Set.Builder().withRepetitions(5).withWeight(60).withSetId("3s").build();

        Exercise squats = new Exercise.Builder(LiftType.SQUAT, Arrays.asList(setA, setA, setA)).withExerciseId("1e").build();
        Exercise benchPress = new Exercise.Builder(LiftType.BENCHPRESS, Arrays.asList(setB, setB, setB)).withExerciseId("2e").build();
        Exercise deadLift = new Exercise.Builder(LiftType.DEADLIFT, Collections.singletonList(setC)).withExerciseId("3e").build();

        List<Exercise> exercisesA = Arrays.asList(squats, benchPress, deadLift);

        Object workout = new Workout.Builder(mockUser4, exercisesA)
                .withWorkoutId("7b244503-82fd-4cf3-af08-2ffefe5a9320")
                .withStartTime(Instant.parse("2019-10-04T10:15:30Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30Z"))
                .build();



        List<String> actualFieldNames = Controller.getFieldNames(workout);
        actualFieldNames.forEach(System.out::println);
        EnumSet<LiftType> enumSet = EnumSet.of(LiftType.valueOf(actualFieldNames.get(0)));


    }






}