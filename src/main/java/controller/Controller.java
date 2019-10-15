package controller;

import io.javalin.http.Context;
import model.Workout;
import repository.Repository;

import java.util.List;

public class Controller implements Repository {

    private final Repository repository;
    private final Context context;

    private Controller(Repository repository, Context context){
        this.context = context;
        this.repository = repository;
    }
    public static Controller getInstance(Repository repository, Context context){
        return new Controller(repository, context);
    }


    @Override
    public List<Workout> list() {
       return repository.list();
    }

    @Override
    public Workout save(Workout workout) {
        return repository.save(workout);
    }

    @Override
    public Workout get(String userId) {
        return repository.get(userId);
    }

    @Override
    public Workout update(Workout workout) {
        return repository.update(workout);
    }

    @Override
    public void delete(String workoutId) {
         repository.delete(workoutId);
    }

    @Override
    public List<Workout> findByUserId(String userId) {
       return repository.findByUserId(userId);
    }

    @Override
    public int size() {
        return repository.size();
    }
}
