package repository;

import model.Workout;
import model.template.TemplateWorkout;

import java.util.List;

public interface Repository {

    List<Workout> list();
    Workout save(Workout workout);
    Workout get(String workoutId);
    Workout update(TemplateWorkout templateWorkout);
    void delete(String workoutId);
    List<Workout> findByUserId(String userId);
    int size();
    double totalLiftedWeightByUser(String userId);
    double heaviestLiftByUser(String userId);
    int totalLiftsByUser(String userId);



}
