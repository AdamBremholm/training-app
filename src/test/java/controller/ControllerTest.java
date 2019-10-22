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

        Set setA = new Set.Builder().withRepetitions(5).withWeight(60).withSetId("1s").build();
        Set setB = new Set.Builder().withRepetitions(5).withWeight(55).withSetId("2s").build();
        Set setC = new Set.Builder().withRepetitions(5).withWeight(60).withSetId("3s").build();

        Exercise squats = new Exercise.Builder(LiftType.SQUAT, Arrays.asList(setA, setA, setA)).withExerciseId("1e").build();
        Exercise benchPress = new Exercise.Builder(LiftType.BENCHPRESS, Arrays.asList(setB, setB, setB)).withExerciseId("2e").build();
        Exercise deadLift = new Exercise.Builder(LiftType.DEADLIFT, Collections.singletonList(setC)).withExerciseId("3e").build();

        List<Exercise> exercisesA = Arrays.asList(squats, benchPress, deadLift);

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

        ((ObjectNode)mockWorkoutJsonNode).remove(Fields.workoutId.toString());
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        assertEquals(4, repository.size());
        String res = controller.save(mockRequest, mockResponse);
        assertTrue(res.contains(Fields.workoutId.toString()));
        assertEquals(5, repository.size());

    }

    @Test
    public void saveCreatesUserIdIfNoneProvided() {

        ((ObjectNode)mockWorkoutJsonNode.get(Fields.user.toString())).remove(Fields.userId.toString());
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

        mapper = null;
        controller = Controller.getInstance(MapRepository.getInstance(new HashMap<>()), mapper);
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        argCaptor = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(mockResponse).status((Integer) argCaptor.capture());
        Mockito.when(mockRequest.params("workoutId")).thenReturn("non-existent-workout-id");
        controller.get(mockRequest, mockResponse);
        assertEquals(HTTP_NOT_FOUND, argCaptor.getValue());
    }


    @Test
    public void updateWorkoutId() throws JsonProcessingException {
        repository.save(mockWorkout3);
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put(Fields.workoutId.toString(), "1234");
        Mockito.when(mockRequest.body()).thenReturn(jsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        JsonNode jsonNodeRes = mapStringToJsonNode(result);
        assertEquals(jsonNodeRes.get(Fields.workoutId.toString()).asText(), "1234");
    }

    @Test
    public void updateStartTime() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put(Fields.startTime.toString(), "2019-10-03 10:15:30");
        Mockito.when(mockRequest.body()).thenReturn(jsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        JsonNode jsonNodeRes = mapStringToJsonNode(result);
        assertEquals("2019-10-03 10:15:30", jsonNodeRes.get(Fields.startTime.toString()).asText());
    }

    @Test
    public void updateEndTime() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put(Fields.endTime.toString(), "2019-10-13 10:15:30");
        Mockito.when(mockRequest.body()).thenReturn(jsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        JsonNode jsonNodeRes = mapStringToJsonNode(result);
        assertEquals("2019-10-13 10:15:30", jsonNodeRes.get(Fields.endTime.toString()).asText());
    }

    @Test (expected = IllegalArgumentException.class)
    public void updateEndTimeCantBeBeforeStartTime() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put(Fields.endTime.toString(), "2017-10-13 10:15:30");
        Mockito.when(mockRequest.body()).thenReturn(jsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        JsonNode jsonNodeRes = mapStringToJsonNode(result);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNonExistingFieldInRootObjectThrowsException() {

        repository.save(mockWorkout3);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.put("non-existing-field", "28" );
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn(mockWorkout3.getWorkoutId());
        Mockito.doThrow(IllegalArgumentException.class).when(mockResponse).status(HTTP_BAD_REQUEST);
        String result = controller.update(mockRequest, mockResponse);
        fail();
    }

    @Test
    public void updateUserObjectInWorkout() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ObjectNode userJsonNode = mapper.createObjectNode();
        userJsonNode.put(Fields.email.toString(), "gurkan@gmail.com");
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(Fields.user.toString(), userJsonNode);
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        JsonNode jsonNodeRes = mapStringToJsonNode(result);
        assertEquals("gurkan@gmail.com", jsonNodeRes.get(Fields.user.toString()).get(Fields.email.toString()).asText());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserObjectInWorkoutUnknownFieldNameInRequestThrowsException() {

        repository.save(mockWorkout3);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.put("johnny", "gurkan@gmail.com");
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn(mockWorkout3.getWorkoutId());
        Mockito.doThrow(IllegalArgumentException.class).when(mockResponse).status(HTTP_BAD_REQUEST);
        controller.update(mockRequest, mockResponse);
    }


    @Test
    public void updateMultipleValuesInNestedObjectInWorkout() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ObjectNode userJsonNode = mapper.createObjectNode();
        userJsonNode.put(Fields.email.toString(), "gurkan@gmail.com");
        userJsonNode.put(Fields.weight.toString(), "60.53");
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(Fields.user.toString(), userJsonNode);
        workoutJsonNode.put(Fields.workoutId.toString(), "123");
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        JsonNode jsonNodeRes = mapStringToJsonNode(result);
        assertEquals("gurkan@gmail.com", jsonNodeRes.get(Fields.user.toString()).get(Fields.email.toString()).asText());
        assertEquals("60.53", jsonNodeRes.get(Fields.user.toString()).get(Fields.weight.toString()).asText());
        assertEquals("123", jsonNodeRes.get(Fields.workoutId.toString()).asText());
    }

    @Test (expected = IllegalArgumentException.class)
    public void updateComputedValuesGenerateIllegalArgumentException() {

        repository.save(mockWorkout3);
        ObjectNode exerciseNode = mapper.createObjectNode();
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(ComputedFields.heaviestExercise.toString(), exerciseNode );
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        fail();

    }

    @Test (expected = IllegalArgumentException.class)
    public void updateComputedValuesInNestedObjectsGenerateIllegalArgumentException() {

        repository.save(mockWorkout3);
        ArrayNode exerciseArray = mapper.createArrayNode();
        ObjectNode exerciseNode = mapper.createObjectNode();
        ObjectNode setNode = mapper.createObjectNode();
        exerciseNode.replace(ComputedFields.heaviestSet.toString(), setNode);
        exerciseArray.add(exerciseNode);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(Fields.exercises.toString(), exerciseArray );
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        fail();
    }



    @Test(expected = NoSuchElementException.class )
    public void whenDeletedItemIsRemovedFromStorageTryToGetThrowsNoSuchElementException() {
        repository.save(mockWorkout3);
        Workout beforeResult = repository.get(mockWorkOutId);
        assertNotNull(beforeResult);
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn(mockWorkOutId);
        String result = controller.delete(mockRequest, mockResponse);
        repository.get(mockWorkOutId);
        fail();
    }

    @Test
    public void deleteWhenNoMatchGenerates404(){
        repository.save(mockWorkout3);
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn("a-workoutId-noOne-has");
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