package controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Workout;
import repository.Repository;
import spark.Request;
import view.JsonView;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


public class Controller implements Initialisable {

    private final Repository repository;
    private final ObjectMapper mapper;
    private static final String IN_PARAM_NULL = "one of the methods in parameters is null";


    private Controller(Repository repository, ObjectMapper mapper){
        this.repository = repository;
        this.mapper = mapper;
    }
    public static Controller getInstance(Repository repository, ObjectMapper mapper){
       if (Optional.ofNullable(repository).isPresent() && Optional.ofNullable(mapper).isPresent())
        return new Controller(repository, mapper);
       else throw new IllegalArgumentException(IN_PARAM_NULL);
    }

    public Repository getRepository() {
        return repository;
    }

    public Workout mapBodyToWorkout(Request request) throws JsonProcessingException {
        return mapper.readValue(request.body(), Workout.class);
    }


    public String list() throws JsonProcessingException {
      return JsonView.workoutListAsJson(repository.list(), mapper);
    }


    public String save(Request request) throws JsonProcessingException {
        Workout workout = mapBodyToWorkout(request);
        return JsonView.workoutAsJson(repository.save(workout), mapper);
}


    public String get(Request request) throws JsonProcessingException, NoSuchElementException {
       String workoutId = Optional.ofNullable(request.params("workoutId")).orElseThrow(IllegalArgumentException::new);
       return JsonView.workoutAsJson(repository.get(workoutId), mapper);
    }


    public Workout update(Request request) throws JsonProcessingException {
        return null;
    }


    public void delete(String workoutId) throws JsonProcessingException, NoSuchElementException {
        repository.delete(workoutId);
    }


    public List<Workout> findByUserId(String userId) {
        return null;
    }


    public int size() {
        return 0;
    }


    public double totalLiftedWeightByUser(String userId) {
        return 0;
    }


    public double heaviestLiftByUser(String userId) {
        return 0;
    }


    public int totalLiftsByUser(String userId) {
        return 0;
    }
}
