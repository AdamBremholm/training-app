package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        Set setA = new Set(5, 60);
        Set setB = new Set(5, 55);
        Set setC = new Set(5, 60);

        Exercise squats = new Exercise(LiftType.SQUAT, Arrays.asList(setA, setA, setA));
        Exercise benchPress = new Exercise(LiftType.BENCHPRESS, Arrays.asList(setB, setB, setB));
        Exercise deadLift = new Exercise(LiftType.DEADLIFT, Collections.singletonList(setC));

        List<Exercise> exercisesA = Arrays.asList(squats, benchPress, deadLift);

        Workout mockWorkout3 = new Workout.Builder(mockUser4, exercisesA)
                .withWorkoutId("7b244503-82fd-4cf3-af08-2ffefe5a9320")
                .withStartTime(Instant.parse("2019-10-04T10:15:30.00Z"))
                .withEndTime(Instant.parse("2019-10-04T10:16:30.00Z"))
                .build();

        mockWorkoutJsonNode = workoutToJsonNode(mockWorkout3);

        mockWorkOutId = mockWorkout3.getWorkoutId();

    }

    public JsonNode workoutToJsonNode(Workout workout){
       return mapper.convertValue(workout, JsonNode.class);
            }

     public JsonNode removeField(JsonNode jsonNode, String removeField){
         for (JsonNode childNode : jsonNode) {
             if (childNode instanceof ObjectNode) {
                 if (childNode.has(removeField)) {
                     ObjectNode object = (ObjectNode) childNode;
                     object.remove(removeField);
                 }
             }
         }
         return jsonNode;
     }

    public JsonNode replaceField(JsonNode jsonNode, String originalField, String valueToReplace){
        for (JsonNode childNode : jsonNode) {
            if (childNode instanceof ObjectNode) {
                if (childNode.has(originalField)) {
                    ObjectNode object = (ObjectNode) childNode;
                    object.put(originalField, valueToReplace);
                }
            }
        }
        return jsonNode;
    }



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
            result = controller.list();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertNotNull(result);
    }

    @Mock
    Request mockRequest;

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

        removeField(mockWorkoutJsonNode, "workoutId");
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

        removeField(mockWorkoutJsonNode, "userId");
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