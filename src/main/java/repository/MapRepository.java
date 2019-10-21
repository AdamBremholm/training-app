package repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import model.Exercise;
import model.Set;
import model.Workout;
import model.template.TemplateWorkout;

import java.util.*;
import java.util.stream.Collectors;

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
    public Workout update(TemplateWorkout templateWorkout) {
        return null;
    }


    @Override
    public void delete(String workoutId) {
        Optional.of(workoutMap.remove(workoutId)).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<Workout> findByUserId(String userId) {
        return workoutMap
                .values()
                .stream()
                .filter((workout) -> userId.equals(workout.getUser().getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public int size() {
       return Optional.ofNullable(workoutMap).map(Map::size).orElseThrow(IllegalStateException::new);
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
