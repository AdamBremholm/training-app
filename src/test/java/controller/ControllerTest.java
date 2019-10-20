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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import repository.MapRepository;
import spark.Request;
import spark.Response;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;

public class ControllerTest {

    private MapRepository repository;
    private Controller controller;
    private ObjectMapper mapper;
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

        Workout mockWorkout3 = new Workout.Builder(mockUser4, exercisesA)
                .withWorkoutId("7b244503-82fd-4cf3-af08-2ffefe5a9320")
                .withStartTime(Instant.parse("2019-10-04T10:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30.00Z"))
                .build();

        mockWorkoutJsonNode = workoutToJsonNode(mockWorkout3);

        mockWorkOutId = mockWorkout3.getWorkoutId();

    }

    @Mock
    Request mockRequest;

    @Mock
    Response mockResponse;




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
        try {
            controller.save(mockRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Mockito.verify(mockRequest).body();
        assertNotNull(repository.get(mockWorkOutId));
    }

    @Test
    public void saveCreatesWorkOutIdIfNoneProvided() {

        removeFieldInObjectNodes(mockWorkoutJsonNode, "workoutId");
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        assertEquals(4, repository.size());
        try {
           String res = controller.save(mockRequest);
            assertTrue(res.contains("workoutId"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertEquals(5, repository.size());

    }

    @Test
    public void saveCreatesUserIdIfNoneProvided() throws JsonProcessingException {

        removeFieldInObjectNodes(mockWorkoutJsonNode, "userId");
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        assertEquals(4, repository.size());
        String res = controller.save(mockRequest);
        assertTrue(res.contains("userId"));
        Mockito.verify(mockRequest).body();
        assertEquals(5, repository.size());

    }

    @Test(expected = JsonProcessingException.class)
    public void saveThrowsExceptionIfIncorrectJsonIsInputted() throws JsonProcessingException {
        Mockito.when(mockRequest.body()).thenReturn("");
        assertEquals(4, repository.size());
        String res = controller.save(mockRequest);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveWithExistingUserIdThrowsException() throws JsonProcessingException {
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        assertEquals(4, repository.size());
        controller.save(mockRequest);
        controller.save(mockRequest);
        fail();
    }

    @Test
    public void getRetrievesObjectByWorkOutId() throws JsonProcessingException {
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        controller.save(mockRequest);
        Mockito.when(mockRequest.params("workoutId")).thenReturn(mockWorkOutId);
        String res = null;
        res = controller.get(mockRequest);
        assertNotNull(res);
    }

    @Test (expected = NoSuchElementException.class)
    public void getThrowsNoSuchElementExceptionIfNotFound() throws JsonProcessingException {
        Mockito.when(mockRequest.body()).thenReturn(mockWorkoutJsonNode.toString());
        controller.save(mockRequest);
        Mockito.when(mockRequest.params("workoutId")).thenReturn("non-existent-workout-id");
        String res = null;
        res = controller.get(mockRequest);
        assertNotNull(res);
    }

    @Test
    public void removeFieldInWorkout(){
        assertTrue(mockWorkoutJsonNode.has(Fields.startTime.toString()));
        removeFieldInObjectNodes(mockWorkoutJsonNode, Fields.startTime.toString());
        assertFalse(mockWorkoutJsonNode.has(Fields.startTime.toString()));
    }

    @Test
    public void removeNestedFieldInWorkout(){
        assertNotNull(mockWorkoutJsonNode.findValue(Fields.email.toString()));
        removeFieldInObjectNodes(mockWorkoutJsonNode, Fields.email.toString());
        assertNull((mockWorkoutJsonNode.findValue(Fields.email.toString())));
    }

    @Test
    public void replaceNestedFieldInWorkOut1(){
        assertTrue(mockWorkoutJsonNode.has(Fields.startTime.toString()));
        replaceFieldInWorkout(mockWorkoutJsonNode, Fields.startTime.toString(), "newTime");
        assertEquals("newTime", (mockWorkoutJsonNode.findValue(Fields.startTime.toString()).asText()));
    }

    @Test
    public void replaceFieldInUser() {
        assertTrue(mockWorkoutJsonNode.get(Fields.user.toString()).has(Fields.email.toString()));
        replaceFieldInUser(mockWorkoutJsonNode, Fields.email.toString(), "hej@hotbrev");
        assertEquals("hej@hotbrev", mockWorkoutJsonNode.get(Fields.user.toString()).get(Fields.email.toString()).asText());
    }

    @Test
    public void replaceFieldInExercise() {
        assertTrue(mockWorkoutJsonNode.get(Fields.exercises.toString()).get(0).has(Fields.liftType.toString()));
        replaceFieldInExercise(mockWorkoutJsonNode, "1e", Fields.liftType.toString(), LiftType.BENCHPRESS.toString() );
        assertEquals(LiftType.BENCHPRESS.toString(), mockWorkoutJsonNode.get(Fields.exercises.toString()).get(0).get(Fields.liftType.toString()).asText());
    }

    @Test
    public void replaceFieldInSet() {
        assertEquals("5", mockWorkoutJsonNode.get(Fields.exercises.toString()).get(0).get(Fields.sets.toString()).get(0).get(Fields.repetitions.toString()).asText());
        replaceFieldInSet(mockWorkoutJsonNode, "1e", "1s", Fields.repetitions.toString(), "6" );
        assertEquals("6", mockWorkoutJsonNode.get(Fields.exercises.toString()).get(0).get(Fields.sets.toString()).get(0).get(Fields.repetitions.toString()).asText());
    }

    @Test(expected = NumberFormatException.class)
    public void replaceSetValueThrowsExceptionWhenNegative() {
        replaceFieldInSet(mockWorkoutJsonNode, "1e", "1s", Fields.repetitions.toString(), "-5");
    }

    @Test(expected = IllegalArgumentException.class)
    public void replaceValueThrowsExceptionWhenTryingToEditComputedPropertiesExercise() {
        replaceFieldInExercise(mockWorkoutJsonNode, "1e", ComputedFields.heaviestExercise.toString(), "yeee" );
        fail();
    }
    @Test(expected = IllegalArgumentException.class)
    public void replaceValueThrowsExceptionWhenTryingToEditComputedPropertiesExercise2() {
        replaceFieldInExercise(mockWorkoutJsonNode, "1e", ComputedFields.heaviestSet.toString(), "yeee" );
        fail();
    }
    @Test(expected = IllegalArgumentException.class)
    public void replaceValueThrowsExceptionWhenTryingToEditComputedPropertiesExercise3() {
        replaceFieldInExercise(mockWorkoutJsonNode, "1e", ComputedFields.totalRepetitions.toString(), "yeee" );
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void replaceValueThrowsExceptionWhenTryingToEditComputedPropertiesWorkout() {
        replaceFieldInWorkout(mockWorkoutJsonNode,  ComputedFields.heaviestExercise.toString(), "yeee" );
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void replaceValueThrowsExceptionWhenTryingToEditComputedPropertiesWorkout2() {
        replaceFieldInWorkout(mockWorkoutJsonNode,  ComputedFields.heaviestSet.toString(), "yeee" );
        fail();
    }
    @Test(expected = IllegalArgumentException.class)
    public void replaceValueThrowsExceptionWhenTryingToEditComputedPropertiesWorkout3() {
        replaceFieldInWorkout(mockWorkoutJsonNode,  ComputedFields.totalRepetitions.toString(), "yeee" );
        fail();
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

    private JsonNode workoutToJsonNode(Workout workout){ ;
        return mapper.convertValue(workout, JsonNode.class);
    }

    private void removeFieldInObjectNodes(JsonNode jsonNode, String removeField){
        if (jsonNode.has(removeField)) {
            ObjectNode object = (ObjectNode) jsonNode;
            object.remove(removeField);
        }
        for (JsonNode childNode : jsonNode) {
            if (childNode instanceof ObjectNode) {
                if (childNode.has(removeField)) {
                    ObjectNode object = (ObjectNode) childNode;
                    object.remove(removeField);
                }
            }
        }

    }

    private void replaceFieldInWorkout(JsonNode jsonNode, String workoutField, String valueToReplace){
        Optional.ofNullable(workoutField).orElseThrow(IllegalArgumentException::new);
        Optional.ofNullable(valueToReplace).orElseThrow(IllegalArgumentException::new);

        if (isComputed(workoutField)) {
            throw new IllegalArgumentException("can only update values on fields which are not computed");
        }

        if (jsonNode.hasNonNull(workoutField)) {
            ObjectNode object = (ObjectNode) jsonNode;
            object.put(workoutField, valueToReplace);
        } else
            throw new NoSuchElementException(workoutField);
    }

    private void replaceFieldInUser(JsonNode jsonNode, String userField, String valueToReplace) {
       JsonNode userNode = Optional.ofNullable(jsonNode).map(jsonNode1 -> jsonNode.get(Fields.user.toString())).orElseThrow(() -> new NoSuchElementException(Fields.user.toString()));
       if (userNode instanceof ObjectNode && userNode.has(userField)){
           ObjectNode object = (ObjectNode) userNode;
           object.put(userField, valueToReplace);
       } else
           throw new NoSuchElementException(userField);
    }



    private void replaceFieldInExercise(JsonNode jsonNode, String exerciseId, String exerciseField, String valueToReplace)  {
        Optional.ofNullable(exerciseField).orElseThrow(IllegalArgumentException::new);
        Optional.ofNullable(exerciseId).orElseThrow(IllegalArgumentException::new);
        Optional.ofNullable(valueToReplace).orElseThrow(IllegalArgumentException::new);

        if (isComputed(exerciseField)) {
            throw new IllegalArgumentException("can only update values on fields which are not computed");
        }

        JsonNode exercise = getExerciseByExerciseId(jsonNode, exerciseId);

        if(exercise instanceof ObjectNode && ((ObjectNode)exercise).has(exerciseField)){
                ((ObjectNode)exercise).put(exerciseField, valueToReplace);
            }
            else
                throw new NoSuchElementException(exerciseField);

    }

    private boolean isComputed(String exerciseField) {
        for (ComputedFields field : ComputedFields.values()) {
            if (exerciseField.equals(field.toString())) {
                return true;
            }
        }
        return false;
    }

    private void replaceFieldInSet(JsonNode jsonNode, String exerciseId, String setId, String setField, String valueToReplace){

        if(setField.equals(Fields.repetitions.toString())){
            if(!isPositiveInteger(valueToReplace))
                throw new NumberFormatException("only positive integers allowed");
        }
        else if (setField.equals(Fields.weight.toString())){
            if(!isPositiveDouble(valueToReplace))
                throw new NumberFormatException("only positive integers and decimals allowed");
        }

        JsonNode exercise = getExerciseByExerciseId(jsonNode, exerciseId);
        ArrayNode setArray = (ArrayNode) Optional.ofNullable(exercise)
                .map(jsonNode1 -> jsonNode1.get(Fields.sets.toString()))
                .filter(e -> e instanceof ArrayNode)
                .orElseThrow(() -> new NoSuchElementException(Fields.sets.toString()));

        JsonNode set;
        for (JsonNode s : setArray) {
          if(s instanceof ObjectNode && s.has(Fields.setId.toString()) && s.get(Fields.setId.toString()).asText().equals(setId) && s.has(setField)) {
              ((ObjectNode)s).put(setField, valueToReplace);
              return;
          }
        }

        throw new NoSuchElementException(setField);

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