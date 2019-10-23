package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.*;
import model.Set;
import model.NoOverWriteMap;
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



        Exercise squats = new Exercise.Builder(Exercise.Type.SQUAT, sets1).build();
        Exercise benchPress = new Exercise.Builder(Exercise.Type.BENCHPRESS, sets2).build();
        Exercise deadLift = new Exercise.Builder(Exercise.Type.DEADLIFT, sets3).build();
        Exercise powerClean = new Exercise.Builder(Exercise.Type.POWERCLEAN, sets4).build();
        Exercise press = new Exercise.Builder(Exercise.Type.PRESS, sets5).build();

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
