package repository;

import model.Workout;


import java.util.List;

public class ListRepository implements Repository {

    private final List<Workout> workouts;

    private ListRepository(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public static ListRepository getInstance(List<Workout> workouts){
        return new ListRepository(workouts);
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }


    @Override
    public List<Workout> list() {
        return null;
    }

    @Override
    public Workout save(Workout workout) {
        workouts.add(workout);
        return workouts.get(size()-1);
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
       return workouts.size();
    }

    @Override
    public double totalLiftedWeightByUser(String userId) {
        return workouts.stream()
                .filter((workout) -> userId.equals(workout.getUser().getUserId()))
                .map(Workout::liftedPerWorkOut)
                .mapToDouble(Double::doubleValue)
                .sum();
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
