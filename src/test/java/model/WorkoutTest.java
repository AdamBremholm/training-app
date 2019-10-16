package model;

import controller.Controller;
import io.javalin.http.Context;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import repository.ListRepository;
import repository.Repository;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WorkoutTest {

    private Repository repository;
    private User mockUser1;
    private User mockUser2;
    private User mockUser3;
    private static final double DELTA = 0.001;

    @Mock
    Context mockContext;


    @Before
    public void setUp()  {
        Controller controller = Controller.getInstance(ListRepository.getInstance(new ArrayList<>()), mockContext);
        repository = controller.getRepository();

         mockUser1 = new User.Builder("mocky", "mock@mockmail.com", "mcMocky")
                .userId("mockUserId")
                .height(180)
                .weight(80)
                .build();
         mockUser2 = new User.Builder("mrsMcMock", "mrs@mockmail.com", "mrs")
                .userId("mockUserId2")
                .height(165)
                .weight(60)
                .build();
         mockUser3 = new User.Builder("kidMock", "kid@mockmail.com", "kid")
                .userId("mockUserId3")
                .height(110)
                .weight(50)
                .build();

        ExerciseSet exerciseSetA = new ExerciseSet(5, 60);
        ExerciseSet exerciseSetB = new ExerciseSet(5, 55);
        ExerciseSet exerciseSetC = new ExerciseSet(5, 60);
        ExerciseSet exerciseSetD = new ExerciseSet(3, 40);
        ExerciseSet exerciseSetE = new ExerciseSet(5, 40);
        ExerciseSet exerciseSetF = new ExerciseSet(5, 45);

        Exercise squats = new Exercise(ExerciseType.SQUAT, Arrays.asList(exerciseSetA, exerciseSetA, exerciseSetA));
        Exercise benchpress = new Exercise(ExerciseType.BENCHPRESS, Arrays.asList(exerciseSetB, exerciseSetB, exerciseSetB));
        Exercise deadlift = new Exercise(ExerciseType.DEADLIFT, Collections.singletonList(exerciseSetC));
        Exercise powerclean = new Exercise(ExerciseType.POWERCLEAN, Arrays.asList(exerciseSetE, exerciseSetE, exerciseSetE));
        Exercise press = new Exercise(ExerciseType.PRESS, Arrays.asList(exerciseSetD, exerciseSetD, exerciseSetF));

        List<Exercise> exercisesA = Arrays.asList(squats, benchpress, deadlift);
        List<Exercise> exercisesB = Arrays.asList(squats, powerclean, press);


        Workout mockWorkout1 = new Workout.Builder(mockUser1)
                .workoutId("mockWorkOutId1")
                .startTime(Instant.parse("2019-10-03T10:15:30.00Z"))
                .endTime(Instant.parse("2019-10-03T10:16:30.00Z"))
                .exercises(exercisesA)
                .build();
        Workout mockWorkout2 = new Workout.Builder(mockUser2)
                .workoutId("mockWorkOutId2")
                .startTime(Instant.parse("2019-10-04T10:15:30.00Z"))
                .endTime(Instant.parse("2019-10-04T10:16:30.00Z"))
                .exercises(exercisesB)
                .build();
        Workout mockWorkout3 = new Workout.Builder(mockUser3)
                .workoutId("mockWorkOutId3")
                .startTime(Instant.parse("2019-10-04T10:15:30.00Z"))
                .endTime(Instant.parse("2019-10-04T10:16:30.00Z"))
                .exercises(exercisesA)
                .build();
        Workout mockWorkout4 = new Workout.Builder(mockUser1)
                .workoutId("mockWorkOutId4")
                .startTime(Instant.parse("2019-10-05T15:15:30.00Z"))
                .endTime(Instant.parse("2019-10-05T16:16:30.00Z"))
                .exercises(exercisesB)
                .build();
        Workout mockWorkout5 = new Workout.Builder(mockUser2)
                .workoutId("mockWorkOutId5")
                .startTime(Instant.parse("2019-10-02T15:15:30.00Z"))
                .endTime(Instant.parse("2019-10-02T16:16:30.00Z"))
                .exercises(null)
                .build();

        repository.save(mockWorkout1);
        repository.save(mockWorkout2);
        repository.save(mockWorkout3);
        repository.save(mockWorkout4);
        repository.save(mockWorkout5);
    }

    @Test
    public void save() {
        assertEquals(5, repository.size());
    }

    @Test
    public void totalWeightLiftedByUser() {
        assertEquals(2025, repository.totalLiftedWeightByUser(mockUser1.getUserId()), DELTA);
    }

    @Test
    public void heaviestLiftByUser() {
        assertEquals(60, repository.heaviestLiftByUser(mockUser2.getUserId()), DELTA);
    }

    @Test(expected = IllegalStateException.class)
    public void heaviestLiftByUserThrowsIllegalStateExceptionIfExercisesAreNotInitialized() {
        assertEquals(60, repository.heaviestLiftByUser(mockUser2.getUserId()), DELTA);
    }

    @Test(expected = IllegalStateException.class)
    public void getHeaviestLiftedSetThrowsExceptionIfNotInitialized() {
        Exercise exercise = new Exercise(ExerciseType.SQUAT, null);
    }


}