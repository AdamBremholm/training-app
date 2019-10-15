package model;

import controller.Controller;
import io.javalin.http.Context;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import repository.ListRepository;
import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;

public class WorkoutTest {

    private Controller controller;
    private User mockUser;
    private Workout mockWorkout;
    private Exercise exercise;

    @Mock
    Context mockContext;


    @Before
    public void setUp()  {
        controller = Controller.getInstance(ListRepository.getInstance(new ArrayList<>()), mockContext);
        String username = "mocky";
        String email = "mock@mockmail.com";
        String password = "mcMocky";
        String userId = "mockUserId";

        String workoutId = "mockWorkOutId";
        Instant startTime = Instant.now().minusSeconds(3600);
        Instant endTime = Instant.now();
        int reps = 10;
        int sets = 3;
        double liftingWeight = 60;
        double height = 180;
        double weight = 80;
        exercise = new Exercise("mock-squats", TrainingType.STRENGTH);
        mockUser = new User.Builder(username, email, password).userId(userId).height(height).weight(weight).build();
        mockWorkout = new Workout.Builder(mockUser, exercise).workoutId(workoutId).startTime(startTime).endTime(endTime).reps(reps).sets(sets).liftingWeight(liftingWeight).build();

    }

    @Test
    public void save() {
        controller.save(mockWorkout);
        assertEquals(1, controller.size());
    }
}