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

public class ControllerTest {

    private MapRepository repository;
    private Controller controller;
    private ObjectMapper mapper;
    private Workout mockWorkout3;
    private User mockUser1;
    private User mockUser2;
    private User mockUser3;
    private List<Workout> workouts;
    private static final double DELTA = 0.001;

    JsonNode mockWorkoutJsonNode;

    private String jsonBodySave2;
    private String jsonBodyUpdate1;
    private String jsonBodyUpdate2;
    private String jsonBodyUpdate3;
    private String jsonBodyUpdate4;
    private String mockWorkOutId;


    @Before
    public void setUp() {

        mapper = Initialisable.getObjectMapperWithJavaDateTimeModule();
        controller = Controller.getInstance(MapRepository.getInstance(new HashMap<>()), mapper);
        Initialisable.populate(controller);
        repository = (MapRepository) controller.getRepository();

        MockitoAnnotations.initMocks(this);

        User mockUser4 = new User.Builder("mockUser4", "4@mockmail.com", "4")
                .withUserId("mockUserId4")
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

    private JsonNode workoutToJsonNode(Workout workout){ ;
        return mapper.convertValue(workout, JsonNode.class);
    }


    private JsonNode mapStringToJsonNode(String jsonString) throws JsonProcessingException {
        return mapper.readTree(jsonString);
    }


}