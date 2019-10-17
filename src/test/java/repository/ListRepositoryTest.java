package repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.Controller;
import model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ListRepositoryTest {


    private Repository repository;
    private User mockUser1;
    private User mockUser2;
    private User mockUser3;
    private List<Workout> workouts;
    private static final double DELTA = 0.001;

    @Before
    public void setUp()  {
        workouts = new ArrayList<>();
        Controller controller = Controller.getInstance(ListRepository.getInstance(workouts), new ObjectMapper());
        repository = controller.getRepository();

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

        Set setA = new Set(5, 60);
        Set setB = new Set(5, 55);
        Set setC = new Set(5, 60);
        Set setD = new Set(3, 40);
        Set setE = new Set(5, 40);
        Set setF = new Set(5, 45);

        Exercise squats = new Exercise(LiftType.SQUAT, Arrays.asList(setA, setA, setA));
        Exercise benchPress = new Exercise(LiftType.BENCHPRESS, Arrays.asList(setB, setB, setB));
        Exercise deadLift = new Exercise(LiftType.DEADLIFT, Collections.singletonList(setC));
        Exercise powerClean = new Exercise(LiftType.POWERCLEAN, Arrays.asList(setE, setE, setE));
        Exercise press = new Exercise(LiftType.PRESS, Arrays.asList(setD, setD, setF));

        List<Exercise> exercisesA = Arrays.asList(squats, benchPress, deadLift);
        List<Exercise> exercisesB = Arrays.asList(squats, powerClean, press);


        Workout mockWorkout1 = new Workout.Builder(mockUser1)
                .withWorkoutId("mockWorkOutId1")
                .withStartTime(Instant.parse("2019-10-03T10:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-03T10:16:30.00Z"))
                .withExercises(exercisesA)
                .build();
        Workout mockWorkout2 = new Workout.Builder(mockUser2)
                .withWorkoutId("mockWorkOutId2")
                .withStartTime(Instant.parse("2019-10-04T10:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30.00Z"))
                .withExercises(exercisesB)
                .build();
        Workout mockWorkout3 = new Workout.Builder(mockUser3)
                .withWorkoutId("mockWorkOutId3")
                .withStartTime(Instant.parse("2019-10-04T10:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30.00Z"))
                .withExercises(exercisesA)
                .build();
        Workout mockWorkout4 = new Workout.Builder(mockUser1)
                .withWorkoutId("mockWorkOutId4")
                .withStartTime(Instant.parse("2019-10-05T15:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-05T16:16:30.00Z"))
                .withExercises(exercisesB)
                .build();


        repository.save(mockWorkout1);
        repository.save(mockWorkout2);
        repository.save(mockWorkout3);
        repository.save(mockWorkout4);

    }

    @Mock
    io.javalin.http.Context mockContext;

    @Test(expected = IllegalStateException.class)
    public void listThrowsIllegalStateExceptionIfNull(){
        Controller controller = Controller.getInstance(ListRepository.getInstance(null), new ObjectMapper());
        repository = controller.getRepository();
        repository.list();
        fail();
    }

    @Test
    public void totalWeightLiftedByUser() {
        assertEquals(3990, repository.totalLiftedWeightByUser(mockUser1.getUserId()), DELTA);
    }

    @Test
    public void heaviestLiftByUser() {
        assertEquals(60, repository.heaviestLiftByUser(mockUser2.getUserId()), DELTA);
    }

    @Test(expected = IllegalStateException.class)
    public void heaviestLiftByUserThrowsIllegalStateExceptionIfExercisesAreNotInitialized() {
        Workout mockWorkout5 = new Workout.Builder(mockUser2)
                .withWorkoutId("mockWorkOutId5")
                .withStartTime(Instant.parse("2019-10-02T15:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-02T16:16:30.00Z"))
                .withExercises(null)
                .build();
        repository.save(mockWorkout5);
       fail();
    }

    @Test(expected = IllegalStateException.class)
    public void getHeaviestLiftedSetThrowsExceptionIfNotInitialized() {
        Exercise exercise = new Exercise(LiftType.SQUAT, null);
    }
}