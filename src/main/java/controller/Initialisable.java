package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.*;
import model.Set;
import repository.Repository;

import java.time.Instant;
import java.util.*;

public interface Initialisable {

    static Controller populate(Controller controller)  {

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

        Set setA = new Set.Builder().withRepetitions(5).withWeight(60).build();
        Set setB = new Set.Builder().withRepetitions(5).withWeight(55).build();
        Set setC = new Set.Builder().withRepetitions(5).withWeight(60).build();
        Set setD = new Set.Builder().withRepetitions(3).withWeight(40).build();;
        Set setE = new Set.Builder().withRepetitions(5).withWeight(40).build();
        Set setF =new Set.Builder().withRepetitions(5).withWeight(45).build();

        Exercise squats = new Exercise.Builder(Exercise.Type.SQUAT, Arrays.asList(setA, setA, setA)).build();
        Exercise benchPress = new Exercise.Builder(Exercise.Type.BENCHPRESS, Arrays.asList(setB, setB, setB)).build();
        Exercise deadLift = new Exercise.Builder(Exercise.Type.DEADLIFT, Collections.singletonList(setC)).build();
        Exercise powerClean = new Exercise.Builder(Exercise.Type.POWERCLEAN, Arrays.asList(setE, setE, setE)).build();
        Exercise press = new Exercise.Builder(Exercise.Type.PRESS, Arrays.asList(setD, setD, setF)).build();

        Map<String, Exercise> exercisesA = new HashMap<>();
        exercisesA.put(squats.getExerciseId(), squats);
        exercisesA.put(benchPress.getExerciseId(), benchPress);
        exercisesA.put(deadLift.getExerciseId(), deadLift);

        Map<String, Exercise> exercisesB = new HashMap<>();
        exercisesB.put(squats.getExerciseId(), squats);
        exercisesB.put(powerClean.getExerciseId(), powerClean);
        exercisesB.put(press.getExerciseId(), press);


        Workout mockWorkout1 = new Workout.Builder(mockUser1, exercisesA)
                .withStartTime(Instant.parse("2019-10-03T10:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-03T10:16:30.00Z"))
                .withWorkoutId("mockId1")
                .build();
        Workout mockWorkout2 = new Workout.Builder(mockUser2, exercisesB)
                .withStartTime(Instant.parse("2019-10-04T10:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30.00Z"))
                .build();
        Workout mockWorkout3 = new Workout.Builder(mockUser3, exercisesA)
                .withStartTime(Instant.parse("2019-10-04T10:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30.00Z"))
                .build();
        Workout mockWorkout4 = new Workout.Builder(mockUser1, exercisesB)
                .withStartTime(Instant.parse("2019-10-05T15:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-05T16:16:30.00Z"))
                .build();


        repository.save(mockWorkout1);
        repository.save(mockWorkout2);
        repository.save(mockWorkout3);
        repository.save(mockWorkout4);

        return controller;

    }

    static ObjectMapper getObjectMapperWithJavaDateTimeModule(){
       return JsonMapper.builder().addModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .build();
    }


}
