package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.*;
import model.Set;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import repository.JPARepository;
import repository.MapRepository;
import spark.Request;
import spark.Response;

import static model.Workout.Fields.*;
import static model.User.Fields.*;
import static model.Exercise.Type.*;


import java.time.Instant;
import java.util.*;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class ControllerJPARepositoryTest {

    private Controller controller;
    private ObjectMapper mapper;
    private Workout mockWorkout3;
    private static final double DELTA = 0.001;
    private JsonNode mockWorkoutJsonNode;
    private String mockWorkOutId;

    @Mock
    Request mockRequest;

    @Mock
    Response mockResponse;

    @Mock
    JPARepository mockRepository;

    @Captor
    ArgumentCaptor argCaptor;

    @Mock
    Workout mockWorkout;




    @Before
    public void setUp() {
        initMocks(this);
        mapper = Initialisable.getObjectMapperWithJavaDateTimeModule();
        controller = Controller.getInstance(mockRepository, mapper);


        User mockUser4 = new User.Builder("mockUser4", "4@mockmail.com", "4")
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
        sets2.put(setB.getSetId(), setB);
        sets2.put(setB2.getSetId(), setB2);
        sets2.put(setB3.getSetId(), setB3);

        Map<String, Set> sets3 = new NoOverWriteMap<>();
        sets3.put(setC.getSetId(), setC);

        Exercise squats = new Exercise.Builder(SQUAT, sets1).withExerciseId("1e").build();
        Exercise benchPress = new Exercise.Builder(BENCHPRESS, sets2).withExerciseId("2e").build();
        Exercise deadLift = new Exercise.Builder(DEADLIFT, sets3).withExerciseId("3e").build();

        Map<String, Exercise> exercisesA = new HashMap<>();
        exercisesA.put(squats.getExerciseId(), squats);
        exercisesA.put(benchPress.getExerciseId(), benchPress);
        exercisesA.put(deadLift.getExerciseId(), deadLift);

        mockWorkout3 = new Workout.Builder(mockUser4, exercisesA)
                .withWorkoutId("7b244503-82fd-4cf3-af08-2ffefe5a9320")
                .withStartTime(Instant.parse("2019-10-04T10:15:30Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30Z"))
                .build();

        mockWorkoutJsonNode = workoutToJsonNode(mockWorkout3);
        mockWorkOutId = mockWorkout3.getWorkoutId();

    }



    @Test
    public void getInstanceReturnsNewObject() {
        controller = Controller.getInstance(JPARepository.getInstance(), Initialisable.getObjectMapperWithJavaDateTimeModule());
        assertEquals(JPARepository.class, controller.getRepository().getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInstanceReturnsThrowsIllegalArgumentExceptionIfRepositoryIsNull() {
        controller = Controller.getInstance(null, Initialisable.getObjectMapperWithJavaDateTimeModule());
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInstanceReturnsThrowsIllegalArgumentExceptionIfMapperIsNull() {
        controller = Controller.getInstance(JPARepository.getInstance(), null);
        fail();
    }


    @Test
    public void list() {
        String result = null;
        when(mockRepository.list()).thenReturn(Collections.singletonList(mockWorkout3));
        try {
            result = controller.list(mockRequest, mockResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertNotNull(result);
    }


    @Test
    public void saveConvertsJsonBodyToObjectAndInputsIntoRepository() {
        when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        when(mockRepository.get(anyString())).thenReturn(mockWorkout3);
        assertNotNull(mockRepository.get(mockWorkOutId));
    }

    @Test
    public void saveCreatesWorkOutIdIfNoneProvided() {

        when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        when(mockRepository.save(any())).thenReturn(mockWorkout3);
        String res = controller.save(mockRequest, mockResponse);
        System.out.println(res);
        assertTrue(res.contains(workoutId.name()));


    }

    @Test
    public void saveCreatesUserIdIfNoneProvided() {

        ((ObjectNode) mockWorkoutJsonNode.get(user.name())).remove(userId.name());
        when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        when(mockRepository.save(any())).thenReturn(mockWorkout3);
        String res = controller.save(mockRequest, mockResponse);
        assertTrue(res.contains(userId.name()));
        verify(mockRequest).body();
    }

    @Test
    public void saveThrowsExceptionIfIncorrectJsonIsInputted() {
        when(mockRequest.body()).thenReturn("");
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
       controller.save(mockRequest, mockResponse);
        assertEquals(HTTP_BAD_REQUEST, argCaptor.getValue());

    }

    @Test(expected = IllegalArgumentException.class)
    public void saveWithExistingUserIdThrowsException() {
        when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        when(mockRepository.save(any())).thenReturn(mockWorkout3);
        controller.save(mockRequest, mockResponse);
        when(mockRepository.save(any())).thenThrow(IllegalArgumentException.class);
        controller.save(mockRequest, mockResponse);
        fail();
    }

    @Test
    public void getRetrievesObjectByWorkOutId() {
        when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        controller.save(mockRequest, mockResponse);
        when(mockRequest.params(workoutId.name())).thenReturn(mockWorkOutId);
        when(mockRepository.get(mockWorkOutId)).thenReturn(mockWorkout3);
        String res;
        res = controller.get(mockRequest, mockResponse);
        assertNotNull(res);
    }

    @Test
    public void getThrowsNoSuchElementExceptionIfNotFound() {

        when(mockRequest.params(workoutId.name())).thenReturn("non-existent-workout-id");
        when(mockRepository.get("non-existent-workout-id")).thenThrow(NoSuchElementException.class);
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        controller.get(mockRequest, mockResponse);
        assertEquals(HTTP_NOT_FOUND, argCaptor.getValue());
    }

    @Test
    public void getThrowsIllegalArgumentExceptionIfNoWorkoutIdIsProvidedInUrl() {
        when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        controller.save(mockRequest, mockResponse);
        when(mockRequest.params("noCorrectWorkoutIdField")).thenReturn("non-existent-workout-id");
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
       controller.get(mockRequest, mockResponse);
        assertEquals(HTTP_BAD_REQUEST, argCaptor.getValue());
    }

    @Test
    public void getThrowsJsonProcessingErrorWhenItCannotMapToJson() {

        controller = Controller.getInstance(MapRepository.getInstance(new HashMap<>()), mapper);
        when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        when(mockRequest.params(workoutId.name())).thenReturn("non-existent-workout-id");
        mapper = null;
        controller.get(mockRequest, mockResponse);
        assertEquals(HTTP_NOT_FOUND, argCaptor.getValue());
    }


    @Test
    public void updateWorkoutIdThrowsIllegalArgumentException() {
       when(mockRepository.save(any())).thenReturn(mockWorkout3);
        mockRepository.save(mockWorkout3);
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put(workoutId.name(), "1234");
        when(mockRequest.body()).thenReturn(jsonNode.toPrettyString());
        when(mockRequest.params(workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        controller.update(mockRequest, mockResponse);
        assertEquals(HTTP_BAD_REQUEST, argCaptor.getValue());

    }

    @Test
    public void updateStartTime() throws JsonProcessingException {
        when(mockRepository.save(any())).thenReturn(mockWorkout3);
        when(mockRepository.get(anyString())).thenReturn(mockWorkout3);
        mockRepository.save(mockWorkout3);
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put(startTime.name(), "2019-10-03 10:15:30");
        when(mockRequest.body()).thenReturn(jsonNode.toPrettyString());

        when(mockRequest.params(workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());

        User resultUser = new User.Builder("a", "b", "c")
                .withUserId("mockUserId4")
                .build();
        Set setA = new Set.Builder().withRepetitions(5).withWeight(60).withSetId("A").build();
        Map<String, Set> sets1 = new NoOverWriteMap<>();
        sets1.put(setA.getSetId(), setA);
        Exercise benchPress = new Exercise.Builder(BENCHPRESS, sets1).withExerciseId("2e").build();
        Map<String, Exercise> exercisesA = new HashMap<>();
        exercisesA.put(benchPress.getExerciseId(), benchPress);
        Workout resultWorkout = new Workout.Builder(resultUser, exercisesA)
                .withWorkoutId(mockWorkout3.getWorkoutId())
                .withStartTime(Instant.parse("2019-10-03T10:15:30Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30Z"))
                .build();

        when(mockRepository.update(anyString(), any())).thenReturn(resultWorkout);
        String result = controller.update(mockRequest, mockResponse);
        System.out.println(result);
        JsonNode jsonNodeRes = mapStringToJsonNode(result);
        assertEquals("2019-10-03 10:15:30",jsonNodeRes.get(startTime.name()).asText() );
    }


    @Test
    public void updateEndTimeCantBeBeforeStartTime() {
        mockRepository.save(mockWorkout3);
        when(mockRepository.get(anyString())).thenReturn(mockWorkout3);
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put(endTime.name(), "2017-10-13 10:15:30");
        when(mockRequest.body()).thenReturn(jsonNode.toPrettyString());
        when(mockRequest.params(workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        controller.update(mockRequest, mockResponse);
        assertEquals(HTTP_BAD_REQUEST, argCaptor.getValue());

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNonExistingFieldInRootObjectThrowsException() {
        mockRepository.save(mockWorkout3);
        when(mockRepository.get(anyString())).thenReturn(mockWorkout3);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.put("non-existing-field", "28");
        when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        when(mockRequest.params(workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        Mockito.doThrow(IllegalArgumentException.class).when(mockResponse).status(HTTP_BAD_REQUEST);
        controller.update(mockRequest, mockResponse);
        fail();
    }

    @Test
    public void updateUserObjectInWorkout() throws JsonProcessingException {

        User resultUser = new User.Builder("a", "gurkan@gmail.com", "c")
                .withUserId("mockUserId4")
                .build();
        Set setA = new Set.Builder().withRepetitions(5).withWeight(60).withSetId("A").build();
        Map<String, Set> sets1 = new NoOverWriteMap<>();
        sets1.put(setA.getSetId(), setA);
        Exercise benchPress = new Exercise.Builder(BENCHPRESS, sets1).withExerciseId("2e").build();
        Map<String, Exercise> exercisesA = new HashMap<>();
        exercisesA.put(benchPress.getExerciseId(), benchPress);
        Workout resultWorkout = new Workout.Builder(resultUser, exercisesA)
                .withWorkoutId(mockWorkout3.getWorkoutId())
                .withStartTime(Instant.parse("2019-10-03T10:15:30Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30Z"))
                .build();

        mockRepository.save(mockWorkout3);
        when(mockRepository.get(anyString())).thenReturn(mockWorkout3);
        when(mockRepository.update(anyString(), any())).thenReturn(resultWorkout);
        ObjectNode userJsonNode = mapper.createObjectNode();
        userJsonNode.put(email.name(), "gurkan@gmail.com");
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(user.name(), userJsonNode);
        when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        when(mockRequest.params(workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        JsonNode jsonNodeRes = mapStringToJsonNode(result);
        assertEquals("gurkan@gmail.com", jsonNodeRes.get(user.name()).get(email.name()).asText());
    }


    @Test(expected = IllegalArgumentException.class)
    public void updateUserObjectInWorkoutUnknownFieldNameInRequestThrowsException() {
        mockRepository.save(mockWorkout3);
        when(mockRepository.get(anyString())).thenReturn(mockWorkout3);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.put("johnny", "gurkan@gmail.com");
        when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        when(mockRequest.params(workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        Mockito.doThrow(IllegalArgumentException.class).when(mockResponse).status(HTTP_BAD_REQUEST);

        controller.update(mockRequest, mockResponse);
    }


    @Test
    public void updateMultipleValuesInNestedObjectInWorkout() throws JsonProcessingException {

        User resultUser = new User.Builder("a", "gurkan@gmail.com", "c")
                .withUserId("mockUserId4")
                .withWeight(60.53)
                .build();
        Set setA = new Set.Builder().withRepetitions(5).withWeight(60).withSetId("A").build();
        Map<String, Set> sets1 = new NoOverWriteMap<>();
        sets1.put(setA.getSetId(), setA);
        Exercise benchPress = new Exercise.Builder(BENCHPRESS, sets1).withExerciseId("2e").build();
        Map<String, Exercise> exercisesA = new HashMap<>();
        exercisesA.put(benchPress.getExerciseId(), benchPress);
        Workout resultWorkout = new Workout.Builder(resultUser, exercisesA)
                .withWorkoutId(mockWorkout3.getWorkoutId())
                .withStartTime(Instant.parse("2019-10-03T10:15:30Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30Z"))
                .build();

        mockRepository.save(mockWorkout3);
        when(mockRepository.get(anyString())).thenReturn(mockWorkout3);
        ObjectNode userJsonNode = mapper.createObjectNode();
        userJsonNode.put(email.name(), "gurkan@gmail.com");
        userJsonNode.put(weight.name(), "60.53");
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(user.name(), userJsonNode);
        when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        when(mockRequest.params(workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        when(mockRepository.update(anyString(), any())).thenReturn(resultWorkout);
        String result = controller.update(mockRequest, mockResponse);
        JsonNode jsonNodeRes = mapStringToJsonNode(result);
        assertEquals("gurkan@gmail.com", jsonNodeRes.get(user.name()).get(email.name()).asText());
        assertEquals("60.53", jsonNodeRes.get(user.name()).get(weight.name()).asText());
    }

    @Test
    public void updateMultipleValuesInNestedExercisesInWorkout() {

        when(mockRepository.save(any())).thenReturn(mockWorkout3);
        mockRepository.save(mockWorkout3);
        when(mockRepository.get(anyString())).thenReturn(mockWorkout3);

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
        exerciseNode1.put(Exercise.Fields.type.name(), CHINS.name());
        exerciseNode1.replace(Exercise.Fields.sets.name(), setsNode1);
        ObjectNode exerciseNode2 = mapper.createObjectNode();
        exerciseNode2.put(Exercise.Fields.type.name(), POWERCLEAN.name());
        exerciseNode2.replace(Exercise.Fields.sets.name(), setsNode2);
        ObjectNode exercisesNode = mapper.createObjectNode();
        exercisesNode.replace("1e", exerciseNode1);
        exercisesNode.replace("2e", exerciseNode2);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(exercises.name(), exercisesNode);

        when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        when(mockRequest.params(workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());


        User resultUser = new User.Builder("a", "gurkan@gmail.com", "c")
                .withUserId("mockUserId4")
                .build();
        Set setA = new Set.Builder().withRepetitions(11).withWeight(60).withSetId("A").build();
        Set setA2 = new Set.Builder().withRepetitions(22).withWeight(60).withSetId("A2").build();
        Set setA3 = new Set.Builder().withRepetitions(5).withWeight(33).withSetId("A3").build();
        Set setB = new Set.Builder().withRepetitions(5).withWeight(44).withSetId("B").build();
        Map<String, Set> sets1 = new NoOverWriteMap<>();
        sets1.put(setA.getSetId(), setA);
        sets1.put(setA2.getSetId(), setA2);
        sets1.put(setA3.getSetId(), setA3);
        Map<String, Set> sets2 = new NoOverWriteMap<>();
        sets2.put(setB.getSetId(), setB);

        Exercise chins = new Exercise.Builder(CHINS, sets1).withExerciseId("1e").build();
        Exercise powerClean = new Exercise.Builder(POWERCLEAN, sets2).withExerciseId("2e").build();
        Map<String, Exercise> exercisesA = new HashMap<>();
        exercisesA.put(chins.getExerciseId(), chins);
        exercisesA.put(powerClean.getExerciseId(), powerClean);

        Workout resultWorkout = new Workout.Builder(resultUser, exercisesA)
                .withWorkoutId(mockWorkout3.getWorkoutId())
                .withStartTime(Instant.parse("2019-10-03T10:15:30Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30Z"))
                .build();


        when(mockRepository.update(anyString(), any())).thenReturn(resultWorkout);
        String result = controller.update(mockRequest, mockResponse);

        when(mockRepository.get(anyString())).thenReturn(resultWorkout);
        assertEquals(CHINS, mockRepository.get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getType());
        assertEquals(POWERCLEAN, mockRepository.get(mockWorkout3.getWorkoutId()).getExercises().get("2e").getType());
        assertEquals(11, mockRepository.get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A").getRepetitions());
        assertEquals(22, mockRepository.get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A2").getRepetitions());
        assertEquals(33, mockRepository.get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A3").getWeight(), DELTA);
        assertEquals(44, mockRepository.get(mockWorkout3.getWorkoutId()).getExercises().get("2e").getSets().get("B").getWeight(), DELTA);

        assertTrue(result.contains("11"));
        assertTrue(result.contains("22"));
        assertTrue(result.contains("33"));
        assertTrue(result.contains("44"));
        assertTrue(result.contains(CHINS.name()));
        assertTrue(result.contains(POWERCLEAN.name()));
    }


    @Test
    public void ifNoIdFoundNothingUpdated() {

        when(mockRepository.save(any())).thenReturn(mockWorkout3);
        mockRepository.save(mockWorkout3);

        ObjectNode setNode1 = mapper.createObjectNode();
        setNode1.put(Set.Fields.repetitions.name(), "11");
        ObjectNode setNode2 = mapper.createObjectNode();
        setNode2.put(Set.Fields.repetitions.name(), "22");
        ObjectNode setNode3 = mapper.createObjectNode();
        setNode3.put(Set.Fields.weight.name(), "33");
        ObjectNode setNode4 = mapper.createObjectNode();
        setNode4.put(Set.Fields.weight.name(), "44");
        ObjectNode setsNode1 = mapper.createObjectNode();
        setsNode1.replace("WrongId", setNode1);
        setsNode1.replace("WrongId", setNode2);
        setsNode1.replace("WrongId3", setNode3);
        ObjectNode setsNode2 = mapper.createObjectNode();
        setsNode2.replace("WrongId", setNode4);
        ObjectNode exerciseNode1 = mapper.createObjectNode();
        exerciseNode1.put(Exercise.Fields.type.name(), CHINS.name());
        exerciseNode1.put(Exercise.Fields.exerciseId.name(), "NewValue");
        exerciseNode1.replace(Exercise.Fields.sets.name(), setsNode1);
        ObjectNode exerciseNode2 = mapper.createObjectNode();
        exerciseNode2.replace(Exercise.Fields.sets.name(), setsNode2);
        ObjectNode exercisesNode = mapper.createObjectNode();
        exercisesNode.replace("WrongId", exerciseNode1);
        exercisesNode.replace("2e", exerciseNode2);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(exercises.name(), exercisesNode);

        when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        when(mockRequest.params(workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());

        when(mockRepository.update(anyString(), any())).thenReturn(mockWorkout3);
        when(mockRepository.get(anyString())).thenReturn(mockWorkout3);
       controller.update(mockRequest, mockResponse);

        assertEquals(SQUAT, mockRepository.get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getType());
        assertEquals(BENCHPRESS, mockRepository.get(mockWorkout3.getWorkoutId()).getExercises().get("2e").getType());
        assertEquals(5, mockRepository.get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A").getRepetitions());
        assertEquals(5, mockRepository.get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A2").getRepetitions());
        assertEquals(60, mockRepository.get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A3").getWeight(), DELTA);
        assertEquals(55, mockRepository.get(mockWorkout3.getWorkoutId()).getExercises().get("2e").getSets().get("B").getWeight(), DELTA);

    }

    @Test
    public void updateComputedValuesGenerateIllegalArgumentException() {

        mockRepository.save(mockWorkout3);
        ObjectNode exerciseNode = mapper.createObjectNode();
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(ImmutableFields.heaviestExercise.name(), exerciseNode);
        when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        when(mockRequest.params(workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        when(mockRepository.get(anyString())).thenReturn(mockWorkout3);
        when(mockRepository.update(anyString(), any())).thenReturn(mockWorkout3);
       controller.update(mockRequest, mockResponse);
        assertEquals(HTTP_BAD_REQUEST, argCaptor.getValue());
    }

    @Test
    public void updateComputedValuesInNestedObjectsGenerateIllegalArgumentException() {

        mockRepository.save(mockWorkout3);
        ArrayNode exerciseArray = mapper.createArrayNode();
        ObjectNode exerciseNode = mapper.createObjectNode();
        ObjectNode setNode = mapper.createObjectNode();
        exerciseNode.replace(ImmutableFields.heaviestSet.name(), setNode);
        exerciseArray.add(exerciseNode);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(exercises.name(), exerciseArray);
        when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        when(mockRequest.params(workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        when(mockRepository.get(anyString())).thenReturn(mockWorkout3);
        when(mockRepository.update(anyString(), any())).thenReturn(mockWorkout3);
        controller.update(mockRequest, mockResponse);
        assertEquals(HTTP_BAD_REQUEST, argCaptor.getValue());

    }


    @Test(expected = NoSuchElementException.class)
    public void whenDeletedItemIsRemovedFromStorageTryToGetThrowsNoSuchElementException() {
        mockRepository.save(mockWorkout3);
        when(mockRepository.get(anyString())).thenReturn(mockWorkout3);
        Workout beforeResult = mockRepository.get(mockWorkOutId);
        assertNotNull(beforeResult);
        when(mockRequest.params(workoutId.name())).thenReturn(mockWorkOutId);
        controller.delete(mockRequest, mockResponse);
        when(mockRepository.get(anyString())).thenThrow(NoSuchElementException.class);
        mockRepository.get(mockWorkOutId);
        fail();
    }

    @Test
    public void deleteWhenNoMatchGenerates404() {
        mockRepository.save(mockWorkout3);
        when(mockRequest.params(workoutId.name())).thenReturn("a-workoutId-noOne-has");
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        doThrow(NoSuchElementException.class).when(mockRepository).delete(anyString());
        controller.delete(mockRequest, mockResponse);
        assertEquals(HTTP_NOT_FOUND, argCaptor.getValue());
    }

    @Test
    public void deleteWhenNoWorkOutIdFieldExistsGeneratesBadRequest() {
        mockRepository.save(mockWorkout3);
        when(mockRequest.params(workoutId.name())).thenReturn(null);
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        controller.delete(mockRequest, mockResponse);
        assertEquals(HTTP_BAD_REQUEST, argCaptor.getValue());
    }


    @Test
    public void findByUserId() {
        when(mockRepository.findByUserId(anyString())).thenReturn(Arrays.asList(mockWorkout, mockWorkout));
        assertEquals(2, controller.findByUserId("mockUserId").size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByUserIdWithNull() {
        controller.findByUserId(null);
        fail();
    }

    @Test
    public void size() {
        when(mockRepository.size()).thenReturn(4);
        assertEquals(4, controller.size());
    }

    @Test
    public void totalLiftedWeightByUser() {
        when(mockRepository.findByUserId(anyString())).thenReturn(Arrays.asList(mockWorkout, mockWorkout, mockWorkout));
        assertEquals(3990, controller.totalLiftedWeightByUser(mockRequest, mockResponse), DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void totalLiftedWeightByUserNull() {
        controller.totalLiftedWeightByUser(mockRequest, mockResponse);
        fail();
    }

    @Test
    public void heaviestLiftByUser() {
        when(mockRepository.heaviestLiftByUser(anyString())).thenReturn(60d);
        assertEquals(60, controller.heaviestLiftByUser(mockRequest, mockResponse), DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void heaviestLiftByUserNull() {
        controller.heaviestLiftByUser(mockRequest, mockResponse);
    }

    @Test
    public void totalLiftsByUser() {
        when(mockRepository.totalLiftsByUser(anyString())).thenReturn(76);
        assertEquals(76, controller.totalLiftsByUser(mockRequest, mockResponse));
    }

    @Test(expected = IllegalArgumentException.class)
    public void totalLiftsByUserNull() {
        assertEquals(76, controller.totalLiftsByUser(mockRequest, mockResponse));
    }

    private JsonNode workoutToJsonNode(Workout workout) {
        return mapper.convertValue(workout, JsonNode.class);
    }


    private JsonNode mapStringToJsonNode(String jsonString) throws JsonProcessingException {
        return mapper.readTree(jsonString);
    }
}