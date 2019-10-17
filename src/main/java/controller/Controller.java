package controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import model.Workout;
import repository.Repository;

import java.util.List;


public class Controller {

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



    public List<Workout> list() {
        return repository.list();
    }


    public Workout save(Context context) {
        try{
            Workout workout = mapper.readValue(context.body(), Workout.class);
            return repository.save(workout);
        } catch (Exception e){
            System.out.println(e);
            context.json(e);

        }
        return null;
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
