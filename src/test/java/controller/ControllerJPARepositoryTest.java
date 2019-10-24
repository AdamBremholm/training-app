package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import repository.JPARepository;
import repository.MapRepository;
import spark.Request;
import spark.Response;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ControllerJPARepositoryTest {


    private MapRepository repository;
    private Controller controller;
    private ObjectMapper mapper;
    private User mockUser4;
    private Workout mockWorkout3;

    @Before
    public void setUp() {
        mapper = Initialisable.getObjectMapperWithJavaDateTimeModule();
        controller = Controller.getInstance(JPARepository.getInstance(), mapper);
        MockitoAnnotations.initMocks(this);

        mockUser4 = new User.Builder("mockUser4", "4@mockmail.com", "4")
                .withUserId("mockUserId4")
                .withHeight(110)
                .withWeight(50)
                .build();

        Set setA = new Set.Builder().withRepetitions(5).withWeight(60).withSetId("A").build();
        Set setA2 = new Set.Builder().withRepetitions(5).withWeight(60).withSetId("A2").build();
        Set setA3 = new Set.Builder().withRepetitions(5).withWeight(60).withSetId("A3").build();
        Set setB = new Set.Builder().withRepetitions(5).withWeight(55).withSetId("B").build();
        Set setB2 = new Set.Builder().withRepetitions(5).withWeight(55).withSetId("B2").build();
        Set setB3 = new Set.Builder().withRepetitions(5).withWeight(55).withSetId("B3").build();
        Set setC = new Set.Builder().withRepetitions(5).withWeight(60).withSetId("C").build();


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



        Exercise squats = new Exercise.Builder(Exercise.Type.SQUAT, sets1).withExerciseId("1e").build();
        Exercise benchPress = new Exercise.Builder(Exercise.Type.BENCHPRESS, sets2).withExerciseId("2e").build();
        Exercise deadLift = new Exercise.Builder(Exercise.Type.DEADLIFT, sets3).withExerciseId("3e").build();

        Map<String, Exercise> exercisesA = new HashMap<>();
        exercisesA.put(squats.getExerciseId(), squats);
        exercisesA.put(benchPress.getExerciseId(), benchPress);
        exercisesA.put(deadLift.getExerciseId(), deadLift);

        mockWorkout3 = new Workout.Builder(mockUser4, exercisesA)
                .withWorkoutId("7b244503-82fd-4cf3-af08-2ffefe5a9320")
                .withStartTime(Instant.parse("2019-10-04T10:15:30Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30Z"))
                .build();
    }

    @Mock
    Request mockRequest;

    @Mock
    Response mockResponse;

    @Captor
    ArgumentCaptor argCaptor;


    @Test
    public void list() {


    }

    @Test
    public void save() {
    }

    @Test
    public void get() {
    }

    @Test
    public void update() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void findByUserId() {
    }

    @Test
    public void size() {
    }

    @Test
    public void totalLiftedWeightByUser() {
    }

    @Test
    public void heaviestLiftByUser() {
    }

    @Test
    public void totalLiftsByUser() {
    }
}
