package controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.ComputedFields;
import model.Workout;
import model.template.TemplateExercise;
import model.template.TemplateUser;
import model.template.TemplateWorkout;
import repository.Repository;
import spark.Request;
import spark.Response;
import view.JsonView;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.net.HttpURLConnection.*;


public class Controller implements Initialisable {


    private final Repository repository;
    private final ObjectMapper mapper;
    private static final String IN_PARAM_NULL = "one of the methods in parameters is null";
    private static final String APPLICATION_JSON = "application/json";


    private Controller(Repository repository, ObjectMapper mapper){
        this.repository = repository;
        this.mapper = mapper;
    }
    public static Controller getInstance(Repository repository, ObjectMapper mapper){
       if (Optional.ofNullable(repository).isPresent() && Optional.ofNullable(mapper).isPresent())
        return new Controller(repository, mapper);
       else throw new IllegalArgumentException(IN_PARAM_NULL);
    }

    public Repository getRepository() { return repository;
    }


    private Workout mapBodyToWorkout(Request request) throws JsonProcessingException {
        return mapper.readValue(request.body(), Workout.class);
    }

    private TemplateWorkout mapRequestBodyToTemplateWorkout(Request request) throws JsonProcessingException {
        return mapper.readValue(request.body(), TemplateWorkout.class);
    }

    private TemplateWorkout mapStringToTemplate(String workoutString) throws JsonProcessingException {
        return mapper.readValue(workoutString, TemplateWorkout.class);
    }

    private TemplateWorkout updateTemplateWorkout(TemplateWorkout templateWorkout, TemplateWorkout requestData) throws IllegalArgumentException {

        Optional<TemplateWorkout> optionalRequestWorkout = Optional.ofNullable(requestData);
        optionalRequestWorkout.map(TemplateWorkout::getWorkoutId).ifPresent(templateWorkout::setWorkoutId);
        optionalRequestWorkout.map(TemplateWorkout::getEndTime).ifPresent(templateWorkout::setEndTime);
        optionalRequestWorkout.map(TemplateWorkout::getStartTime).ifPresent(templateWorkout::setStartTime);
     //   optionalRequestWorkout.map(TemplateWorkout::getExercises).ifPresent(templateWorkout::setExercises);

        optionalRequestWorkout.map(TemplateWorkout::getExercises).ifPresent((exerciseList)-> updateTemplateExercises(templateWorkout.getExercises(), exerciseList));


        updateTemplateUser(templateWorkout.getUser(), requestData.getUser());
        validateTemplateWorkout(templateWorkout);
        return templateWorkout;
    }

    private void updateTemplateExercises(List<TemplateExercise> templateExercises, List<TemplateExercise> requestExercises) {



    }

    private void updateTemplateUser(TemplateUser templateUser, TemplateUser requestUser) {

        Optional<TemplateUser> optionalRequestUser = Optional.ofNullable(requestUser);

        optionalRequestUser.map(TemplateUser::getEmail).ifPresent(templateUser::setEmail);
        optionalRequestUser.map(TemplateUser::getPassword).ifPresent(templateUser::setPassword);
        optionalRequestUser.map(TemplateUser::getUserId).ifPresent(templateUser::setUserId);
        optionalRequestUser.map(TemplateUser::getUsername).ifPresent(templateUser::setUsername);
        optionalRequestUser.map(TemplateUser::getHeight).filter((height)-> height !=0).ifPresent(templateUser::setHeight);
        optionalRequestUser.map(TemplateUser::getWeight).filter((weight)-> weight !=0).ifPresent(templateUser::setWeight);

    }

    private void validateTemplateWorkout(TemplateWorkout templateWorkout) throws IllegalArgumentException{
        ifEndTimeBeforeStartTimeThrowException(templateWorkout.getStartTime(), templateWorkout.getEndTime());
    }

    private void validateRequestData(JsonNode requestData) {

        final String COMPUTED_VALUE = " is a computed value, it cant be set";
        JsonNode jsonNode;
        for (ComputedFields cf : ComputedFields.values()) {
            jsonNode = requestData.findValue(cf.toString());
            Optional.ofNullable(jsonNode).ifPresent(s -> {
                throw new IllegalArgumentException(cf.toString() + COMPUTED_VALUE);
            });
        }
    }

    private void ifEndTimeBeforeStartTimeThrowException(Instant startTime, Instant endTime) {
        if (endTime.isBefore(startTime))
            throw new IllegalArgumentException("endTime cannot be before startTime");
    }

    private TemplateWorkout mapToTemplate(Workout workout) throws JsonProcessingException {
        String res = JsonView.workoutAsJson(Optional.ofNullable(workout).orElseThrow(IllegalArgumentException::new), mapper);
        return mapStringToTemplate(Optional.ofNullable(res).orElseThrow(IllegalArgumentException::new));
    }

    private Workout mapTemplateToWorkout(TemplateWorkout updatedTemplateWorkout) throws JsonProcessingException {
        String result = JsonView.templateWorkoutAsJson(updatedTemplateWorkout, mapper);
        System.out.println(result);
        return mapper.readValue(result, Workout.class);
    }

    private JsonNode mapStringToJsonNode(String jsonString) throws JsonProcessingException {
        return mapper.readTree(jsonString);
    }


    public String list(Request request, Response response) throws JsonProcessingException {
        response.status(200);
        response.type(APPLICATION_JSON);
        return JsonView.workoutListAsJson(repository.list(), mapper);
    }


    public String save(Request request, Response response)  {
        Workout workout;
        try {
            workout = mapBodyToWorkout(request);
            response.status(HTTP_CREATED);
            response.type(APPLICATION_JSON);
            return JsonView.workoutAsJson(repository.save(workout), mapper);
        } catch (JsonProcessingException e) {
            response.status(HTTP_BAD_REQUEST);
            return e.toString();
        }
    }


    public String get(Request request, Response response)  {

        try {
            String workoutId = Optional.ofNullable(request.params(Workout.Fields.workoutId.toString())).orElseThrow(() -> new IllegalArgumentException("need workoutId in url"));
            response.status(HTTP_OK);
            response.type(APPLICATION_JSON);
            return JsonView.workoutAsJson(repository.get(workoutId), mapper);
        } catch (JsonProcessingException jpe) {
            response.status(HTTP_BAD_REQUEST);
            return jpe.toString();
        } catch (NoSuchElementException nse){
            response.status(HTTP_NOT_FOUND);
            return nse.toString();
        } catch (IllegalArgumentException iae){
            response.status(HTTP_BAD_REQUEST);
            return iae.toString();
        }

    }

    public String update(Request request, Response response) throws JsonProcessingException {
        try {
            String workoutId = Optional.ofNullable(request.params(Workout.Fields.workoutId.toString())).orElseThrow(IllegalArgumentException::new);
            Workout oldWorkout = repository.get(workoutId);
            TemplateWorkout templateWorkout = mapToTemplate(oldWorkout);
            JsonNode jsonNode = mapStringToJsonNode(request.body());
            validateRequestData(jsonNode);
            TemplateWorkout requestData = mapRequestBodyToTemplateWorkout(request);
            TemplateWorkout updatedTemplateWorkout = updateTemplateWorkout(templateWorkout, requestData);
            Workout updatedWorkout = mapTemplateToWorkout(updatedTemplateWorkout);
            Workout result = repository.update(workoutId, updatedWorkout);
            response.status(HTTP_OK);
            response.type(APPLICATION_JSON);
            return JsonView.workoutAsJson(result, mapper);
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
            response.status(HTTP_NO_CONTENT);
            response.type(APPLICATION_JSON);
            String workoutId = Optional.ofNullable(request.params(Workout.Fields.workoutId.toString())).orElseThrow(IllegalArgumentException::new);
            repository.delete(workoutId);
            return "";
        }  catch (NoSuchElementException nse){
            response.status(HTTP_NOT_FOUND);
            return nse.toString();
        }  catch (IllegalArgumentException iae){
            response.status(HTTP_BAD_REQUEST);
            return iae.toString();
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
