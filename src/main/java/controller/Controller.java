package controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import model.Exercise;
import model.User;
import model.Workout;
import model.template.TemplateExercise;
import model.template.TemplateSet;
import model.template.TemplateUser;
import model.template.TemplateWorkout;
import repository.Repository;
import spark.Request;
import spark.Response;
import view.HtmlView;
import view.JsonView;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.net.HttpURLConnection.*;


public class Controller {


    private final Repository repository;
    private final ObjectMapper mapper;
    private static final String IN_PARAM_NULL = "one of the methods in parameters is null";
    private static final String APPLICATION_JSON = "application/json";
    private static final String TEXT_HTML = "text/html";


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
        optionalRequestWorkout.map(TemplateWorkout::getExercises).ifPresent((requestExercises)-> updateTemplateExercises(templateWorkout.getExercises(), requestExercises));

        if(Optional.ofNullable(templateWorkout.getUser()).isPresent() && optionalRequestWorkout.map(TemplateWorkout::getUser).isPresent())
         updateTemplateUser(templateWorkout.getUser(), requestData.getUser());

        validateTemplateWorkout(templateWorkout);
        return templateWorkout;
    }

    private void updateTemplateExercises(Map<String, TemplateExercise> templateExercises, Map<String, TemplateExercise> requestExercises) {


            requestExercises.forEach((exerciseId, requestExercise) -> findTemplateExerciseByExerciseId(exerciseId, templateExercises).ifPresent(targetExercise -> {

                 Optional.ofNullable(requestExercise).map(TemplateExercise::getType)
                         .ifPresent(targetExercise::setType);

                Optional.ofNullable(requestExercise).map(TemplateExercise::getSets)
                        .ifPresent(requestSets -> requestSets.forEach((setId, requestTemplateSet) -> findTemplateSetBySetId(setId, targetExercise.getSets())
                                        .ifPresent(targetSet -> {

                                            Optional.of(requestTemplateSet.getRepetitions())
                                                    .filter(value -> value > 0).ifPresent(targetSet::setRepetitions);

                                            Optional.of(requestTemplateSet.getWeight())
                                                    .filter(value -> value > 0).ifPresent(targetSet::setWeight);

                                        })));
             }));
            }



    private Optional<TemplateExercise> findTemplateExerciseByExerciseId(String exerciseId, Map<String, TemplateExercise> templateExercises){
    if(Optional.ofNullable(templateExercises).isPresent() && templateExercises.containsKey(exerciseId))
        return Optional.ofNullable(templateExercises.get(exerciseId));

    return Optional.empty();
    }

    private Optional<TemplateSet> findTemplateSetBySetId(String setId, Map<String, TemplateSet> templateSets){
        if(Optional.ofNullable(templateSets).isPresent() && templateSets.containsKey(setId))
            return Optional.ofNullable(templateSets.get(setId));

        return Optional.empty();
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

    private void validateRequestData(JsonNode requestData) throws IllegalArgumentException {

        final String IMMUTABLE_FIELD = " is an immutable field, remove it from request ";
        JsonNode jsonNode;
        for (Workout.ImmutableFields imF : Workout.ImmutableFields.values()) {
            jsonNode = requestData.findValue(imF.toString());
            Optional.ofNullable(jsonNode).ifPresent(s -> {
                throw new IllegalArgumentException(imF.toString() + IMMUTABLE_FIELD);
            });
        }
        for (Exercise.ImmutableFields imF : Exercise.ImmutableFields.values()) {
            jsonNode = requestData.findValue(imF.toString());
            Optional.ofNullable(jsonNode).ifPresent(s -> {
                throw new IllegalArgumentException(imF.toString() + IMMUTABLE_FIELD);
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
        return mapper.readValue(result, Workout.class);
    }

    private JsonNode mapStringToJsonNode(String jsonString) throws JsonProcessingException {
        return mapper.readTree(jsonString);
    }

    @SuppressWarnings("SameReturnValue")
    public String heartBeat(Request request, Response response) {
        response.status(200);
        response.type(APPLICATION_JSON);
        return "heartbeat";
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
        } catch (JsonProcessingException | IllegalArgumentException jpe) {
            response.status(HTTP_BAD_REQUEST);
            return jpe.toString();
        } catch (NoSuchElementException nse){
            response.status(HTTP_NOT_FOUND);
            return nse.toString();
        }

    }

    public String update(Request request, Response response) {
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
        } catch (JsonProcessingException | IllegalArgumentException jpe) {
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


    List<Workout> findByUserId(String userId) {
        return repository.findByUserId(Optional.ofNullable(userId).orElseThrow(IllegalArgumentException::new));
    }


    int size() {
        return repository.size();
    }


    public String totalLiftedWeightByUser(Request request, Response response) {
        try {
            String userId = Optional.ofNullable(request.params(User.Fields.userId.name())).orElseThrow(IllegalArgumentException::new);
            response.status(HTTP_OK);
            response.type(TEXT_HTML);
            return HtmlView.printHtml(repository.totalLiftedWeightByUser(userId), "total lifted weight by user");

        } catch (IllegalArgumentException iae){
            response.status(HTTP_BAD_REQUEST);
            return iae.toString();
        }


    }


    public String heaviestLiftByUser(Request request, Response response) {

        try {
            String userId = Optional.ofNullable(request.params(User.Fields.userId.name())).orElseThrow(IllegalArgumentException::new);
            response.status(HTTP_OK);
            response.type(TEXT_HTML);
            return HtmlView.printHtml(repository.heaviestLiftByUser(userId), "heaviest lift by user");

        } catch (NoSuchElementException nse){
            response.status(HTTP_NOT_FOUND);
            return nse.toString();
        }
        catch (IllegalArgumentException iae){
            response.status(HTTP_BAD_REQUEST);
            return iae.toString();
        }
    }


    public String totalLiftsByUser(Request request, Response response) {

        try {
            String userId = Optional.ofNullable(request.params(User.Fields.userId.name())).orElseThrow(IllegalArgumentException::new);
            response.status(HTTP_OK);
            response.type(TEXT_HTML);
            return HtmlView.printHtml(repository.totalLiftsByUser(userId), "total lifts by user");

        } catch (IllegalArgumentException iae){
            response.status(HTTP_BAD_REQUEST);
            return iae.toString();
        }
    }

}
