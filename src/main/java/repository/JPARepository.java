package repository;


import model.Workout;
import model.template.TemplateWorkout;

import java.util.List;

public class JPARepository implements Repository {

    private JPARepository(){

    }
    public static JPARepository getInstance(){
        return new JPARepository();
    }

    @Override
    public List<Workout> list() {
        return null;
    }

    @Override
    public Workout save(Workout workout) {
        return null;
    }

    @Override
    public Workout get(String workoutId) {
        return null;
    }

    @Override
    public Workout update(TemplateWorkout templateWorkout) {
        return null;
    }

    @Override
    public void delete(String workoutId) {

    }

    @Override
    public List<Workout> findByUserId(String userId) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public double totalLiftedWeightByUser(String userId) {
        return 0;
    }

    @Override
    public double heaviestLiftByUser(String userId) {
        return 0;
    }

    @Override
    public int totalLiftsByUser(String userId) {
        return 0;
    }
}
