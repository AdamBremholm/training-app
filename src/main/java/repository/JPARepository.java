package repository;


import model.Workout;

import java.util.List;

public class JPARepository implements Repository {

    @Override
    public List<Workout> list() {
        return null;
    }

    @Override
    public Workout save(Workout workout) {
        return null;
    }

    @Override
    public Workout get(String userId) {
        return null;
    }

    @Override
    public Workout update(Workout workout) {
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
    public int totalLiftsByUser(String userid) {
        return 0;
    }
}
