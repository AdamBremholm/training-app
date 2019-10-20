package controller;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Workout;
import repository.Repository;
import spark.Request;
import spark.Response;
import view.JsonView;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;


public class Controller implements Initialisable {

    private static final int HTTP_NOT_FOUND = 404;
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

    public ObjectMapper getMapper() {
        return mapper;
    }

    public Workout mapBodyToWorkout(Request request) throws JsonProcessingException {
        return mapper.readValue(request.body(), Workout.class);
    }


    public String list(Request request, Response response) throws JsonProcessingException {
        response.status(200);
        response.type("application/json");
        return JsonView.workoutListAsJson(repository.list(), mapper);
    }


    public String save(Request request, Response response)  {
        Workout workout = null;
        try {
            workout = mapBodyToWorkout(request);
            response.status(201);
            response.type("application/json");
            return JsonView.workoutAsJson(repository.save(workout), mapper);
        } catch (JsonProcessingException e) {
            response.status(HTTP_BAD_REQUEST);
            return e.toString();
        }
    }


    public String get(Request request, Response response)  {

        try {
            response.status(200);
            response.type("application/json");
            String workoutId = Optional.ofNullable(request.params("workoutId")).orElseThrow(IllegalArgumentException::new);
            return JsonView.workoutAsJson(repository.get(workoutId), mapper);
        } catch (JsonProcessingException jpe) {
            response.status(HTTP_BAD_REQUEST);
            return jpe.toString();
        } catch (NoSuchElementException nse){
            response.status(HTTP_NOT_FOUND);
            return nse.toString();
        }

    }

    public String update(Request request, Response response)  {
        try {
            response.status(200);
            response.type("application/json");
            return JsonView.workoutAsJson(repository.update(null), mapper);
        } catch (JsonProcessingException jpe) {
            response.status(HTTP_BAD_REQUEST);
            return jpe.toString();
        } catch (NoSuchElementException nse){
            response.status(HTTP_NOT_FOUND);
            return nse.toString();
        }
    }


    public String delete(Request request, Response response)  {
        try {
            response.status(204);
            response.type("application/json");
            String workoutId = Optional.ofNullable(request.params("workoutId")).orElseThrow(IllegalArgumentException::new);
            repository.delete(workoutId);
            return "";
        }  catch (NoSuchElementException nse){
            response.status(HTTP_NOT_FOUND);
            return nse.toString();
        }

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
