package repository;

import model.Exercise;
import model.Set;
import model.Workout;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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
       return Optional.ofNullable(workouts).orElseThrow(IllegalStateException::new);
    }

    @Override
    public Workout save(Workout workout) {
        Workout nonNullWorkOut = Optional.ofNullable(workout).orElseThrow(IllegalArgumentException::new);
        Optional.ofNullable(workouts).ifPresent(workouts -> workouts.add(nonNullWorkOut));
        return nonNullWorkOut;
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
       return workouts.stream()
                .filter((workout) -> userId.equals(workout.getUser().getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public int size() {
       return workouts.size();
    }

    @Override
    public double totalLiftedWeightByUser(String userId) {
        return findByUserId(userId)
                .stream()
                .mapToDouble(Workout::liftedPerWorkOut)
                .sum();
    }

    @Override
    public double heaviestLiftByUser(String userId) {
        return findByUserId(userId)
                .stream()
                .map(Workout::getHeaviestExercise)
                .map(Exercise::getHeaviestSet)
                .mapToDouble(Set::getWeight)
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public int totalLiftsByUser(String userId) {
        return findByUserId(userId)
                .stream()
                .mapToInt(Workout::getTotalRepetitions)
                .sum();
    }

}
