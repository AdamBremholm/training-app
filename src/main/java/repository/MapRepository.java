package repository;

import model.Workout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        Optional.ofNullable(workoutMap).ifPresent(workoutMap -> workoutMap.put(nonNullWorkOut.getWorkoutId(), nonNullWorkOut));
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
