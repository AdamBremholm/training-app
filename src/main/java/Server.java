
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import controller.Controller;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;
import model.*;
import repository.ListRepository;
import repository.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Server {




    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .build();

        JavalinJackson.configure(mapper);

        Controller controller = init(Controller.getInstance(ListRepository.getInstance(new ArrayList<>()),mapper));


        app.get("/api/workouts", ctx -> {
            ctx.json(controller.list());
        });

        app.post("/api/workouts", ctx -> {
            // some code
          Workout res = controller.save(ctx);
           ctx.json(res).status(201);
        });

    }

    public static Controller init(Controller controller)  {

        Repository repository = controller.getRepository();


        User mockUser1 = new User.Builder("mrMock", "mock@mockmail.com", "mr")
                .withUserId("mockUserId")
                .withHeight(180)
                .withWeight(80)
                .build();
        User mockUser2 = new User.Builder("mrsMcMock", "mrs@mockmail.com", "mrs")
                .withUserId("mockUserId2")
                .withHeight(165)
                .withWeight(60)
                .build();
        User mockUser3 = new User.Builder("kidMock", "kid@mockmail.com", "kid")
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

        return controller;

    }
}
