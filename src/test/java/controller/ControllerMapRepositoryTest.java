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
import repository.MapRepository;
import spark.Request;
import spark.Response;

import java.time.Instant;
import java.util.*;

import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class ControllerMapRepositoryTest {

    private MapRepository repository;
    private Controller controller;
    private ObjectMapper mapper;
    private Workout mockWorkout3;
    private static final double DELTA = 0.001;
    JsonNode mockWorkoutJsonNode;
    private String mockWorkOutId;
    private User mockUser4;


    @Before
    public void setUp() {

        mapper = Initialisable.getObjectMapperWithJavaDateTimeModule();
        controller = Controller.getInstance(MapRepository.getInstance(new HashMap<>()), mapper);
        Initialisable.populate(controller);
        repository = (MapRepository) controller.getRepository();

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

        mockWorkoutJsonNode = workoutToJsonNode(mockWorkout3);

        mockWorkOutId = mockWorkout3.getWorkoutId();



    }

    @Mock
    Request mockRequest;

    @Mock
    Response mockResponse;


    @Captor
    ArgumentCaptor argCaptor;



    @Test
    public void getInstanceReturnsNewObject() {
        controller = Controller.getInstance(MapRepository.getInstance(new HashMap<>()), Initialisable.getObjectMapperWithJavaDateTimeModule());
        assertEquals(MapRepository.class, controller.getRepository().getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInstanceReturnsThrowsIllegalArgumentExceptionIfRepositoryIsNull() {
        controller = Controller.getInstance(null, Initialisable.getObjectMapperWithJavaDateTimeModule());
       fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInstanceReturnsThrowsIllegalArgumentExceptionIfMapperIsNull() {
        controller = Controller.getInstance(MapRepository.getInstance(new HashMap<>()), null);
        fail();
    }


    @Test
    public void list() {
        String result = null;
        try {
            result = controller.list(mockRequest, mockResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertNotNull(result);
    }



    @Test
    public void saveConvertsJsonBodyToObjectAndInputsIntoRepository() {
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        controller.save(mockRequest, mockResponse);
        verify(mockRequest).body();
        assertNotNull(repository.get(mockWorkOutId));
    }

    @Test
    public void saveCreatesWorkOutIdIfNoneProvided() {

        ((ObjectNode)mockWorkoutJsonNode).remove(Workout.Fields.workoutId.name());
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        assertEquals(4, repository.size());
        String res = controller.save(mockRequest, mockResponse);
        assertTrue(res.contains(Workout.Fields.workoutId.name()));
        assertEquals(5, repository.size());

    }

    @Test
    public void saveCreatesUserIdIfNoneProvided() {

        ((ObjectNode)mockWorkoutJsonNode.get(Workout.Fields.user.name())).remove(User.Fields.userId.name());
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        assertEquals(4, repository.size());
        String res = controller.save(mockRequest, mockResponse);
        assertTrue(res.contains("userId"));
        verify(mockRequest).body();
        assertEquals(5, repository.size());

    }

    @Test
    public void saveThrowsExceptionIfIncorrectJsonIsInputted()  {
        Mockito.when(mockRequest.body()).thenReturn("");
        assertEquals(4, repository.size());
        String res = controller.save(mockRequest, mockResponse);
        assertEquals(4, repository.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveWithExistingUserIdThrowsException() {
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        assertEquals(4, repository.size());
        controller.save(mockRequest, mockResponse);
        controller.save(mockRequest, mockResponse);
        fail();
    }

    @Test
    public void getRetrievesObjectByWorkOutId()  {
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        controller.save(mockRequest, mockResponse);
        Mockito.when(mockRequest.params("workoutId")).thenReturn(mockWorkOutId);
        String res = null;
        res = controller.get(mockRequest, mockResponse);
        assertNotNull(res);
    }

    @Test
    public void getThrowsNoSuchElementExceptionIfNotFound()  {
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        controller.save(mockRequest, mockResponse);
        Mockito.when(mockRequest.params("workoutId")).thenReturn("non-existent-workout-id");
        String res = null;
        res = controller.get(mockRequest, mockResponse);
        assertEquals("java.util.NoSuchElementException", res);
    }

    @Test
    public void getThrowsIllegalArgumentExceptionIfNoWorkoutIdIsProvidedInUrl()  {
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        controller.save(mockRequest, mockResponse);
        Mockito.when(mockRequest.params("noCorrectWorkoutIdField")).thenReturn("non-existent-workout-id");
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        String res = controller.get(mockRequest, mockResponse);
        assertEquals(HTTP_BAD_REQUEST, argCaptor.getValue());
    }

    @Test
    public void getThrowsJsonProcessingErrorWhenItCannotMapToJson()  {

        controller = Controller.getInstance(MapRepository.getInstance(new HashMap<>()), mapper);
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn("non-existent-workout-id");
        mapper = null;
        controller.get(mockRequest, mockResponse);
        assertEquals(HTTP_NOT_FOUND, argCaptor.getValue());
    }


    @Test
    public void updateWorkoutIdThrowsIllegalArgumentException() throws JsonProcessingException {
        repository.save(mockWorkout3);
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put(Workout.Fields.workoutId.name(), "1234");
        Mockito.when(mockRequest.body()).thenReturn(jsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        String result = controller.update(mockRequest, mockResponse);
        assertEquals(HTTP_BAD_REQUEST, argCaptor.getValue());

    }

    @Test
    public void updateStartTime() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put(Workout.Fields.startTime.name(), "2019-10-03 10:15:30");
        Mockito.when(mockRequest.body()).thenReturn(jsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        JsonNode jsonNodeRes = mapStringToJsonNode(result);
        assertEquals("2019-10-03 10:15:30", jsonNodeRes.get(Workout.Fields.startTime.name()).asText());
    }

    @Test
    public void updateEndTime() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put(Workout.Fields.endTime.name(), "2019-10-13 10:15:30");
        Mockito.when(mockRequest.body()).thenReturn(jsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        JsonNode jsonNodeRes = mapStringToJsonNode(result);
        assertEquals("2019-10-13 10:15:30", jsonNodeRes.get(Workout.Fields.endTime.name()).asText());
    }

    @Test
    public void updateEndTimeCantBeBeforeStartTime() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put(Workout.Fields.endTime.name(), "2017-10-13 10:15:30");
        Mockito.when(mockRequest.body()).thenReturn(jsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        String result = controller.update(mockRequest, mockResponse);
        assertEquals(HTTP_BAD_REQUEST, argCaptor.getValue());

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNonExistingFieldInRootObjectThrowsException() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.put("non-existing-field", "28" );
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        Mockito.doThrow(IllegalArgumentException.class).when(mockResponse).status(HTTP_BAD_REQUEST);
        String result = controller.update(mockRequest, mockResponse);
        fail();
    }

    @Test
    public void updateUserObjectInWorkout() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ObjectNode userJsonNode = mapper.createObjectNode();
        userJsonNode.put(User.Fields.email.name(), "gurkan@gmail.com");
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(Workout.Fields.user.name(), userJsonNode);
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        JsonNode jsonNodeRes = mapStringToJsonNode(result);
        assertEquals("gurkan@gmail.com", jsonNodeRes.get(Workout.Fields.user.name()).get(User.Fields.email.name()).asText());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserObjectInWorkoutUnknownFieldNameInRequestThrowsException() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.put("johnny", "gurkan@gmail.com");
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        Mockito.doThrow(IllegalArgumentException.class).when(mockResponse).status(HTTP_BAD_REQUEST);
        controller.update(mockRequest, mockResponse);
    }


    @Test
    public void updateMultipleValuesInNestedObjectInWorkout() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ObjectNode userJsonNode = mapper.createObjectNode();
        userJsonNode.put(User.Fields.email.name(), "gurkan@gmail.com");
        userJsonNode.put(User.Fields.weight.name(), "60.53");
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(Workout.Fields.user.name(), userJsonNode);
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        JsonNode jsonNodeRes = mapStringToJsonNode(result);
        assertEquals("gurkan@gmail.com", jsonNodeRes.get(Workout.Fields.user.name()).get(User.Fields.email.name()).asText());
        assertEquals("60.53", jsonNodeRes.get(Workout.Fields.user.name()).get(User.Fields.weight.name()).asText());
    }

    @Test
    public void updateMultipleValuesInNestedExercisesInWorkout()  {

        repository.save(mockWorkout3);

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

        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());

        String result = controller.update(mockRequest, mockResponse);
        assertEquals(Exercise.Type.CHINS, repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getType());
        assertEquals(Exercise.Type.POWERCLEAN, repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("2e").getType());
        assertEquals(11, repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A").getRepetitions());
        assertEquals(22, repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A2").getRepetitions());
        assertEquals(33, repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A3").getWeight(), DELTA);
        assertEquals(44, repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("2e").getSets().get("B").getWeight(), DELTA);
        assertTrue(result.contains("11"));
        assertTrue(result.contains("22"));
        assertTrue(result.contains("33"));
        assertTrue(result.contains("44"));
        assertTrue(result.contains(Exercise.Type.CHINS.name()));
        assertTrue(result.contains(Exercise.Type.POWERCLEAN.name()));
    }

    @Test
    public void newSetIdAndExerciseIdAreIgnored()  {

        repository.save(mockWorkout3);

        ObjectNode setNode1 = mapper.createObjectNode();
        setNode1.put(Set.Fields.repetitions.name(), "11");
        ObjectNode setNode2 = mapper.createObjectNode();
        setNode2.put(Set.Fields.setId.name(), "NewValue");
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
        exerciseNode1.put(Exercise.Fields.exerciseId.name(), "NewValue");
        exerciseNode1.replace(Exercise.Fields.sets.name(), setsNode1);
        ObjectNode exerciseNode2 = mapper.createObjectNode();
        exerciseNode2.put(Exercise.Fields.type.name(), Exercise.Type.POWERCLEAN.name());
        exerciseNode2.replace(Exercise.Fields.sets.name(), setsNode2);
        ObjectNode exercisesNode = mapper.createObjectNode();
        exercisesNode.replace("1e", exerciseNode1);
        exercisesNode.replace("2e", exerciseNode2);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(Workout.Fields.exercises.name(), exercisesNode);

        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());

        String result = controller.update(mockRequest, mockResponse);

        assertEquals("A2", repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A2").getSetId());
        assertEquals("1e", repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getExerciseId());
        assertEquals(22, repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A2").getRepetitions());

    }

    @Test
    public void ifNoIdFoundNothingUpdated()  {

        repository.save(mockWorkout3);

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
        exerciseNode1.put(Exercise.Fields.type.name(), Exercise.Type.CHINS.name());
        exerciseNode1.put(Exercise.Fields.exerciseId.name(), "NewValue");
        exerciseNode1.replace(Exercise.Fields.sets.name(), setsNode1);
        ObjectNode exerciseNode2 = mapper.createObjectNode();
        exerciseNode2.replace(Exercise.Fields.sets.name(), setsNode2);
        ObjectNode exercisesNode = mapper.createObjectNode();
        exercisesNode.replace("WrongId", exerciseNode1);
        exercisesNode.replace("2e", exerciseNode2);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(Workout.Fields.exercises.name(), exercisesNode);

        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());

        String result = controller.update(mockRequest, mockResponse);


        assertEquals(Exercise.Type.SQUAT, repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getType());
        assertEquals(Exercise.Type.BENCHPRESS, repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("2e").getType());
        assertEquals(5, repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A").getRepetitions());
        assertEquals(5, repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A2").getRepetitions());
        assertEquals(60, repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("1e").getSets().get("A3").getWeight(), DELTA);
        assertEquals(55, repository.getWorkoutMap().get(mockWorkout3.getWorkoutId()).getExercises().get("2e").getSets().get("B").getWeight(), DELTA);

    }

    @Test
    public void updateComputedValuesGenerateIllegalArgumentException() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ObjectNode exerciseNode = mapper.createObjectNode();
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(ImmutableFields.heaviestExercise.name(), exerciseNode );
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        String result = controller.update(mockRequest, mockResponse);
        assertEquals(HTTP_BAD_REQUEST, argCaptor.getValue());
    }

    @Test
    public void updateComputedValuesInNestedObjectsGenerateIllegalArgumentException() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ArrayNode exerciseArray = mapper.createArrayNode();
        ObjectNode exerciseNode = mapper.createObjectNode();
        ObjectNode setNode = mapper.createObjectNode();
        exerciseNode.replace(ImmutableFields.heaviestSet.name(), setNode);
        exerciseArray.add(exerciseNode);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(Workout.Fields.exercises.name(), exerciseArray );
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkout3.getWorkoutId());
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        String result = controller.update(mockRequest, mockResponse);
        assertEquals(HTTP_BAD_REQUEST, argCaptor.getValue());

    }

    @Test
    public void updateWithNoCorrectWorkoutId()  {

        repository.save(mockWorkout3);
        ArrayNode exerciseArray = mapper.createArrayNode();
        ObjectNode exerciseNode = mapper.createObjectNode();
        ObjectNode setNode = mapper.createObjectNode();
        exerciseNode.replace(ImmutableFields.heaviestSet.name(), setNode);
        exerciseArray.add(exerciseNode);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(Workout.Fields.exercises.name(), exerciseArray );
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn("unknown-id");
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        String result = controller.update(mockRequest, mockResponse);
        assertEquals(HTTP_NOT_FOUND, argCaptor.getValue());


    }



    @Test(expected = NoSuchElementException.class )
    public void whenDeletedItemIsRemovedFromStorageTryToGetThrowsNoSuchElementException() {
        repository.save(mockWorkout3);
        Workout beforeResult = repository.get(mockWorkOutId);
        assertNotNull(beforeResult);
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(mockWorkOutId);
        String result = controller.delete(mockRequest, mockResponse);
        repository.get(mockWorkOutId);
        fail();
    }

    @Test
    public void deleteWhenNoMatchGenerates404(){
        repository.save(mockWorkout3);
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn("a-workoutId-noOne-has");
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        String result = controller.delete(mockRequest, mockResponse);
        assertEquals(HTTP_NOT_FOUND, argCaptor.getValue());
    }

    @Test
    public void deleteWhenNoWorkOutIdFieldExistsGeneratesBadRequest(){
        repository.save(mockWorkout3);
        Mockito.when(mockRequest.params(Workout.Fields.workoutId.name())).thenReturn(null);
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        String result = controller.delete(mockRequest, mockResponse);
        assertEquals(HTTP_BAD_REQUEST, argCaptor.getValue());
    }



    @Test
    public void findByUserId() {
        assertEquals(2, controller.findByUserId("mockUserId").size());
    }

    @Test (expected = IllegalArgumentException.class)
    public void findByUserIdWithNull() {
        controller.findByUserId(null);
        fail();
    }

    @Test
    public void size() {
        assertEquals(4, controller.size());
    }

    @Test
    public void totalLiftedWeightByUser() {
        assertEquals(3990, controller.totalLiftedWeightByUser("mockUserId"), DELTA);
    }

    @Test (expected = IllegalArgumentException.class)
    public void totalLiftedWeightByUserNull() {
        controller.totalLiftedWeightByUser(null);
        fail();
    }

    @Test
    public void heaviestLiftByUser() {
        assertEquals(60, controller.heaviestLiftByUser("mockUserId"), DELTA);
    }

    @Test (expected = IllegalArgumentException.class)
    public void heaviestLiftByUserNull() {
        controller.heaviestLiftByUser(null);
    }

    @Test
    public void totalLiftsByUser() {
        assertEquals(76, controller.totalLiftsByUser("mockUserId"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void totalLiftsByUserNull() {
        assertEquals(76, controller.totalLiftsByUser(null));
    }

    private JsonNode workoutToJsonNode(Workout workout){ ;
        return mapper.convertValue(workout, JsonNode.class);
    }


    private JsonNode mapStringToJsonNode(String jsonString) throws JsonProcessingException {
        return mapper.readTree(jsonString);
    }


}