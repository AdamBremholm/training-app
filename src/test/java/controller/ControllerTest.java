package controller;

import model.User;
import model.Workout;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import repository.Repository;

import java.util.List;
import static org.junit.Assert.*;

public class ControllerTest {

    private Repository repository;
    private User mockUser1;
    private User mockUser2;
    private User mockUser3;
    private List<Workout> workouts;
    private static final double DELTA = 0.001;
    Controller controller;


    @Before
    public void setUp() throws Exception {
        controller = ControllerFactory.getMapRepositoryController();
        Initialisable.populate(controller);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getInstance() {
    }

    @Test
    public void getRepository() {
    }



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