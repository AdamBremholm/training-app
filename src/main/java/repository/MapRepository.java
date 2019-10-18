package repository;

import model.Workout;

import java.util.*;

public class MapRepository implements Repository {


    public final Map<String, Workout> workoutMap;

    private MapRepository(Map<String, Workout> workoutMap) {
        this.workoutMap = workoutMap;
    }

    public static MapRepository getInstance(Map<String, Workout> workoutMap){
        return new MapRepository(workoutMap);
    }

    public Map<String, Workout> getWorkoutMap() {
        return workoutMap;
    }

    @Override
    public List<Workout> list() {
        return new ArrayList<Workout>(workoutMap.values());
    }

    @Override
    public Workout save(Workout workout) {
        Workout nonNullWorkOut = Optional.ofNullable(workout).orElseThrow(IllegalArgumentException::new);
        Optional.ofNullable(workoutMap).ifPresent(workoutMap -> {
            if(!workoutMap.containsKey(workout.getWorkoutId()))
            workoutMap.put(nonNullWorkOut.getWorkoutId(), nonNullWorkOut);
            else
                throw new IllegalArgumentException("The key (workoutId) already exists in the database.");
        });
        return get(nonNullWorkOut.getWorkoutId());
    }

    @Override
    public Workout get(String workoutId) {
       return Optional.ofNullable(workoutMap).map(workoutMap -> workoutMap.get(workoutId)).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Workout update(Workout workout) {
     return null;
    }

    @Override
    public void delete(String workoutId) {
        Optional.of(workoutMap.remove(workoutId)).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<Workout> findByUserId(String userId) {
        return null;
    }

    @Override
    public int size() {
       return Optional.ofNullable(workoutMap).map(Map::size).orElseThrow(IllegalStateException::new);
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
