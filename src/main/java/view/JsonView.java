package view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Workout;
import model.template.TemplateWorkout;

import java.util.List;

public class JsonView {

    public static String workoutListAsJson(List<Workout> result, ObjectMapper mapper) throws JsonProcessingException {
       return mapper.writeValueAsString(result);
    }

    public static String workoutAsJson(Workout result, ObjectMapper mapper) throws JsonProcessingException {
        return mapper.writeValueAsString(result);
    }

    public static String templateWorkoutAsJson(TemplateWorkout result, ObjectMapper mapper) throws JsonProcessingException {
        return mapper.writeValueAsString(result);
    }
}
