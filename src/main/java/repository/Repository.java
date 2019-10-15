package repository;

import model.Workout;

import java.util.List;

public interface Repository {

    List<Workout> list();
    Workout save(Workout workout);
    Workout get(String userId);
    Workout update(Workout workout);
    void delete(String workoutId);
    List<Workout> findByUserId(String userId);
    int size();

}
