import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controller.Initialisable;

import model.*;
import okhttp3.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


public class TrainingServerTest {

    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = "http://localhost:4567/api";
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private JsonNode mockWorkoutJsonNode;
    private ObjectMapper mapper;


    @BeforeClass
    public static void ensureAppIsRunning(){
        ServerSparkStarter.
                get("localhost", "/heartbeat" ).
                startSparkAppIfNotRunning(4567);
    }

    @Before
    public void setUp() {
        mapper = Initialisable.getObjectMapperWithJavaDateTimeModule();

       User mockUser4 = new User.Builder("mockUser4", "4@mockmail.com", "4")
                .withUserId("mockUserId4")
                .withHeight(9999)
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

        Workout mockWorkout3 = new Workout.Builder(mockUser4, exercisesA)
                .withWorkoutId("mockId1")
                .withStartTime(Instant.parse("2019-10-04T10:15:30Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30Z"))
                .build();

        mockWorkoutJsonNode = workoutToJsonNode(mockWorkout3);

    }

    private JsonNode workoutToJsonNode(Workout workout){
        return mapper.convertValue(workout, JsonNode.class);
    }


    @Test
    public void getWorkouts() throws IOException {

        Request request = new Request.Builder()
                .url(BASE_URL + "/workouts")
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        assert responseBody != null;
        String res = responseBody.string();
        assertNotNull(res);
        assertEquals(200, response.code());

    }

    @Test
    public void postWorkout() throws IOException {

       RequestBody requestBody = RequestBody.create(mockWorkoutJsonNode.toPrettyString(), JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + "/workouts")
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        assert responseBody != null;
        String res = responseBody.string();
        assertNotNull(res);
        assertEquals(201, response.code());

    }

    @Test
    public void getWorkout() throws IOException {

        Request request = new Request.Builder()
                .url(BASE_URL + "/workouts/mockId1")
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        assert responseBody != null;
        String res = responseBody.string();
        assertNotNull(res);
        assertEquals(200, response.code());

    }

    @Test
    public void updateWorkout() throws IOException {

        ObjectNode setNode1 = mapper.createObjectNode();
        setNode1.put(Set.Fields.repetitions.name(), "11");
        ObjectNode setNode2 = mapper.createObjectNode();
        setNode2.put(Set.Fields.repetitions.name(), "22");
        ObjectNode setNode3 = mapper.createObjectNode();
        setNode3.put(Set.Fields.weight.name(), "33");
        ObjectNode setNode4 = mapper.createObjectNode();
        setNode4.put(Set.Fields.weight.name(), "44");
        ObjectNode setsNode1 = mapper.createObjectNode();
        setsNode1.replace("A", setNode1);
        setsNode1.replace("A2", setNode2);
        setsNode1.replace("A3", setNode3);
        ObjectNode setsNode2 = mapper.createObjectNode();
        setsNode2.replace("B", setNode4);
        ObjectNode exerciseNode1 = mapper.createObjectNode();
        exerciseNode1.put(Exercise.Fields.type.name(), Exercise.Type.CHINS.name());
        exerciseNode1.replace(Exercise.Fields.sets.name(), setsNode1);
        ObjectNode exerciseNode2 = mapper.createObjectNode();
        exerciseNode2.put(Exercise.Fields.type.name(), Exercise.Type.POWERCLEAN.name());
        exerciseNode2.replace(Exercise.Fields.sets.name(), setsNode2);
        ObjectNode exercisesNode = mapper.createObjectNode();
        exercisesNode.replace("1e", exerciseNode1);
        exercisesNode.replace("2e", exerciseNode2);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(Workout.Fields.exercises.name(), exercisesNode);


        RequestBody requestBody = RequestBody.create(workoutJsonNode.toPrettyString(), JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + "/workouts/mockId1")
                .put(requestBody)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        assert responseBody != null;
        String res = responseBody.string();
        System.out.println(res);
        assertEquals(200, response.code());

    }

    @Test
    public void deleteWorkout() throws IOException {


        Request request = new Request.Builder()
                .url(BASE_URL + "/workouts/mockId1")
                .delete()
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        assert responseBody != null;
        String res = responseBody.string();
        assertEquals(204, response.code());

    }

}