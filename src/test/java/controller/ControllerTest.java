package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import model.User;
import model.Workout;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import repository.MapRepository;
import spark.Request;

import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class ControllerTest {

    private MapRepository repository;
    private User mockUser1;
    private User mockUser2;
    private User mockUser3;
    private List<Workout> workouts;
    private static final double DELTA = 0.001;
    Controller controller;
    private String jsonBodySave;
    private String mockWorkOutId;

    @Before
    public void setUp() throws Exception {
        controller = ControllerFactory.getMapRepositoryController();
        Initialisable.populate(controller);
        repository = (MapRepository) controller.getRepository();
        MockitoAnnotations.initMocks(this);
        jsonBodySave = "{\"workoutId\":\"7b244503-82fd-4cf3-af08-2ffefe5a9320\",\"user\":{\"userId\":\"SKAPAD\"," +
                "\"username\":\"mrMock\",\"email\":\"mock@mockmail.com\",\"password\":\"mr\",\"weight\":80,\"height\":180}," +
                "\"startTime\":\"2019-10-03 10:15:30\",\"endTime\":\"2019-10-03 10:16:30\"," +
                "\"exercises\":[{\"liftType\":\"SQUAT\",\"sets\":[{\"repetitions\":5,\"weight\":60},{\"repetitions\":5," +
                "\"weight\":60},{\"repetitions\":5,\"weight\":60}],\"heaviestSet\":{\"repetitions\":5,\"weight\":60}," +
                "\"totalRepetitions\":15},{\"liftType\":\"BENCHPRESS\",\"sets\":[{\"repetitions\":5,\"weight\":55}," +
                "{\"repetitions\":5,\"weight\":55},{\"repetitions\":5,\"weight\":55}],\"heaviestSet\":{\"repetitions\":5," +
                "\"weight\":55},\"totalRepetitions\":15},{\"liftType\":\"DEADLIFT\",\"sets\":[{\"repetitions\":5," +
                "\"weight\":60}],\"heaviestSet\":{\"repetitions\":5,\"weight\":60},\"totalRepetitions\":5}]," +
                "\"heaviestExercise\":{\"liftType\":\"SQUAT\",\"sets\":[{\"repetitions\":5,\"weight\":60}," +
                "{\"repetitions\":5,\"weight\":60},{\"repetitions\":5,\"weight\":60}],\"heaviestSet\":{\"repetitions\":5," +
                "\"weight\":60},\"totalRepetitions\":15},\"totalRepetitions\":35}";

        mockWorkOutId = "7b244503-82fd-4cf3-af08-2ffefe5a9320";
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
        Mockito.when(mockRequest.body()).thenReturn(jsonBodySave);
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
        int index = jsonBodySave.indexOf(",");
        String toBeReplaced = jsonBodySave.substring(1, index+1);
        String replacement = "";
        String replacedString = jsonBodySave.replace(toBeReplaced, replacement);
        Mockito.when(mockRequest.body()).thenReturn(jsonBodySave);
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
    public void saveCreatesUserIdIfNoneProvided() {

        Mockito.when(mockRequest.body()).thenReturn(jsonBodySave);
        assertEquals(4, repository.size());
        try {
            String res = controller.save(mockRequest);
            assertTrue(res.contains("userId"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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
        Mockito.when(mockRequest.body()).thenReturn(jsonBodySave);
        assertEquals(4, repository.size());
        controller.save(mockRequest);
        controller.save(mockRequest);
        fail();
    }

    @Test
    public void getRetrievesObjectByWorkOutId() throws JsonProcessingException {
        Mockito.when(mockRequest.body()).thenReturn(jsonBodySave);
        controller.save(mockRequest);
        Mockito.when(mockRequest.params("workoutId")).thenReturn(mockWorkOutId);
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