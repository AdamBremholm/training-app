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
import java.util.List;
import java.util.stream.Collectors;

public class WorkoutTest {

    private Repository repository;
    private User mockUser1;
    private User mockUser2;
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
        User mockUser3 = new User.Builder("kidMock", "kid@mockmail.com", "kid")
                .userId("mockUserId3")
                .height(110)
                .weight(50)
                .build();


        WorkoutSet workoutSetA = new WorkoutSet(10, 30);
        WorkoutSet workoutSetB = new WorkoutSet(10, 35);
        WorkoutSet workoutSetC = new WorkoutSet(8, 45);

        WorkoutSet workoutSetD = new WorkoutSet(7, 50);
        WorkoutSet workoutSetE = new WorkoutSet(5, 55);
        WorkoutSet workoutSetF = new WorkoutSet(5, 56);


        Workout mockWorkout1 = new Workout.Builder(mockUser1, Exercise.SQUAT)
                .workoutId("mockWorkOutId1")
                .startTime(Instant.parse("2019-10-03T10:15:30.00Z"))
                .endTime(Instant.parse("2019-10-03T10:16:30.00Z"))
                .workoutSets(Arrays.asList(workoutSetA, workoutSetB, workoutSetC))
                .build();
        Workout mockWorkout2 = new Workout.Builder(mockUser2, Exercise.CHINS)
                .workoutId("mockWorkOutId2")
                .startTime(Instant.parse("2019-10-04T10:15:30.00Z"))
                .endTime(Instant.parse("2019-10-04T10:16:30.00Z"))
                .workoutSets(Arrays.asList(workoutSetF, workoutSetF, workoutSetB))
                .build();
        Workout mockWorkout3 = new Workout.Builder(mockUser3, Exercise.BENCHPRESS)
                .workoutId("mockWorkOutId3")
                .startTime(Instant.parse("2019-10-04T10:15:30.00Z"))
                .endTime(Instant.parse("2019-10-04T10:16:30.00Z"))
                .workoutSets(Arrays.asList(workoutSetA, workoutSetE, workoutSetD))
                .build();
        Workout mockWorkout4 = new Workout.Builder(mockUser1, Exercise.BENCHPRESS)
                .workoutId("mockWorkOutId4")
                .startTime(Instant.parse("2019-10-05T15:15:30.00Z"))
                .endTime(Instant.parse("2019-10-05T16:16:30.00Z"))
                .workoutSets(Arrays.asList(workoutSetA, workoutSetE, workoutSetD))
                .build();
        Workout mockWorkout5 = new Workout.Builder(mockUser2, Exercise.BENCHPRESS)
                .workoutId("mockWorkOutId5")
                .startTime(Instant.parse("2019-10-02T15:15:30.00Z"))
                .endTime(Instant.parse("2019-10-02T16:16:30.00Z"))
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
    public void totalWeightLiftedByUSer() {
        assertEquals(1935, repository.totalLiftedWeightByUser(mockUser1.getUserId()), DELTA);
    }

    @Test
    public void totalWeightLiftedByUserWhenWorkoutSetIsNull() {
        assertEquals(910, repository.totalLiftedWeightByUser(mockUser2.getUserId()), DELTA);
    }


}