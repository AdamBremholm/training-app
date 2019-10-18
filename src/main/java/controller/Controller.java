package controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import model.Workout;
import repository.Repository;
import view.JsonView;

import java.util.List;


public class Controller implements Initialisable {

    private final Repository repository;
    private final ObjectMapper mapper;


    private Controller(Repository repository, ObjectMapper mapper){
        this.repository = repository;
        this.mapper = mapper;
    }
    public static Controller getInstance(Repository repository, ObjectMapper mapper){
        return new Controller(repository, mapper);
    }

    public Repository getRepository() {
        return repository;
    }

    public Workout mapBodyToWorkout(Context context) throws JsonProcessingException {
        return mapper.readValue(context.body(), Workout.class);
    }


    public void list(Context context) {
       JsonView.displayListAsJson(repository.list(), context, 200);
    }


    public Workout save(Context context) throws JsonProcessingException {
        Workout workout = mapBodyToWorkout(context);
        return repository.save(workout);
}


    public Workout get(String userId) {
        return null;
    }


    public Workout update(Workout workout) {
        return null;
    }


    public void delete(String workoutId) {

    }


    public List<Workout> findByUserId(String userId) {
        return null;
    }


    public int size() {
        return 0;
    }


    public double totalLiftedWeightByUser(String userId) {
        return 0;
    }


    public double heaviestLiftByUser(String userId) {
        return 0;
    }


    public int totalLiftsByUser(String userId) {
        return 0;
    }
}
