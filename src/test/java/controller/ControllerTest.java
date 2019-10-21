package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
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
import view.JsonView;

import java.time.Instant;
import java.util.*;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static org.junit.Assert.*;

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
        controller = ControllerFactory.getMapRepositoryController();
        Initialisable.populate(controller);
        repository = (MapRepository) controller.getRepository();
        mapper = controller.getMapper();
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
        Mockito.verify(mockRequest).body();
        assertNotNull(repository.get(mockWorkOutId));
    }

    @Test
    public void saveCreatesWorkOutIdIfNoneProvided() {

        ((ObjectNode)mockWorkoutJsonNode).remove(Fields.workoutId.toString());
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        assertEquals(4, repository.size());
        String res = controller.save(mockRequest, mockResponse);
        assertTrue(res.contains("workoutId"));
        assertEquals(5, repository.size());

    }

    @Test
    public void saveCreatesUserIdIfNoneProvided() {

        ((ObjectNode)mockWorkoutJsonNode.get(Fields.user.toString())).remove(Fields.userId.toString());
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        assertEquals(4, repository.size());
        String res = controller.save(mockRequest, mockResponse);
        assertTrue(res.contains("userId"));
        Mockito.verify(mockRequest).body();
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
    public void updateComputedValuesGenerateIllegalArgumentException() throws JsonProcessingException {

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
    public void updateComputedValuesInNestedObjectsGenerateIllegalArgumentException() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ArrayNode exerciseArray = mapper.createArrayNode();
        ObjectNode exerciseNode = mapper.createObjectNode();
        ObjectNode setNote = mapper.createObjectNode();
        exerciseNode.replace(ComputedFields.heaviestSet.toString(), setNote);
        exerciseArray.add(exerciseNode);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(Fields.exercises.toString(), exerciseArray );
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        fail();
    }

    @Test (expected = IllegalArgumentException.class)
    public void updateNonExistingFieldGThrowsException() throws JsonProcessingException {

        repository.save(mockWorkout3);
        ArrayNode exerciseArray = mapper.createArrayNode();
        ObjectNode exerciseNode = mapper.createObjectNode();
        ObjectNode setNote = mapper.createObjectNode();
        exerciseNode.replace(ComputedFields.heaviestSet.toString(), setNote);
        exerciseArray.add(exerciseNode);
        ObjectNode workoutJsonNode = mapper.createObjectNode();
        workoutJsonNode.replace(Fields.exercises.toString(), exerciseArray );
        Mockito.when(mockRequest.body()).thenReturn(workoutJsonNode.toPrettyString());
        Mockito.when(mockRequest.params(Fields.workoutId.toString())).thenReturn(mockWorkout3.getWorkoutId());
        String result = controller.update(mockRequest, mockResponse);
        fail();
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

    private JsonNode workoutToJsonNode(Workout workout){ ;
        return mapper.convertValue(workout, JsonNode.class);
    }



    private boolean isComputed(String exerciseField) {
        for (ComputedFields field : ComputedFields.values()) {
            if (exerciseField.equals(field.toString())) {
                return true;
            }
        }
        return false;
    }



    @Test
    public void isPositiveDouble() {
        assertTrue(isPositiveDouble("0"));
        assertTrue(isPositiveDouble("22"));
        assertTrue(isPositiveDouble("5.05"));
        assertFalse(isPositiveDouble("-200"));
        assertTrue(isPositiveDouble("    22    "));
        assertFalse(isPositiveDouble(null));
        assertFalse(isPositiveDouble(""));
        assertFalse(isPositiveDouble("abc"));
    }

    @Test
    public void isPositiveInteger() {
        assertTrue(isPositiveInteger("0"));
        assertTrue(isPositiveInteger("22"));
        assertFalse(isPositiveInteger("5.05"));
        assertFalse(isPositiveInteger("-200"));
        assertTrue(isPositiveInteger("    22    "));
        assertFalse(isPositiveInteger(null));
        assertFalse(isPositiveInteger(""));
        assertFalse(isPositiveInteger("abc"));
    }


    public static boolean isPositiveDouble(String strNum) {
        double d = 0;
        try {
             d = Double.parseDouble(strNum.trim());
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        if (d<0){
           return false;
        }
        return true;
    }

    public static boolean isPositiveInteger(String strNum) {
        int i = 0;
        try {
            i = Integer.parseInt(strNum.trim());
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        if (i<0 || strNum.contains(",") || strNum.contains(".")){
           return false;
        }
        return true;

    }

    private JsonNode mapStringToJsonNode(String jsonString) throws JsonProcessingException {
        return mapper.readTree(jsonString);
    }

    private JsonNode getExerciseByExerciseId(JsonNode jsonNode, String exerciseId) {
        ArrayNode exerciseArray = (ArrayNode) Optional.ofNullable(jsonNode)
                .map(jsonNode1 -> jsonNode.get("exercises"))
                .filter(e -> e instanceof ArrayNode)
                .orElseThrow(() -> new NoSuchElementException("exercises"));

        for (JsonNode node : exerciseArray) {
            if(node instanceof ObjectNode && ((ObjectNode)node).get(Fields.exerciseId.toString()).asText().equals(exerciseId)) {
                return node;
            }
        }
        throw new NoSuchElementException(exerciseId);
    }


    private JsonNode replaceFieldInJsonNode(JsonNode jsonNode, String originalField, String valueToReplace) {
        if (jsonNode.has(originalField)) {
            ObjectNode object = (ObjectNode) jsonNode;
            object.put(originalField, valueToReplace);
            return null;
        }
        return jsonNode;
    }

    private void replaceFieldInJsonNodesChildren(JsonNode jsonNode, String originalField, String valueToReplace) {

        for (JsonNode childNode : jsonNode) {
            if (childNode instanceof ObjectNode) {
                if (childNode.has(originalField)) {
                    ObjectNode object = (ObjectNode) childNode;
                    object.put(originalField, valueToReplace);
                } else {
                    replaceFieldInJsonNodesChildren(childNode, originalField, valueToReplace);
                }
            }
        }
    }
}