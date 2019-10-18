package view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Workout;

import java.util.List;

public class JsonView {

    public static String displayListAsJson(List<Workout> result, ObjectMapper mapper) throws JsonProcessingException {
       return mapper.writeValueAsString(result);
    }

    public static String displayWorkoutAsJson(Workout result, ObjectMapper mapper) throws JsonProcessingException {
        return mapper.writeValueAsString(result);
    }
}
