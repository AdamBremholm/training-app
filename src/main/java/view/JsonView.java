package view;

import io.javalin.http.Context;
import model.Workout;

import java.util.List;

public class JsonView {

    public static void displayListAsJson(List<Workout> result, Context context, int statusCode){
        context.json(result).status(statusCode);
    }

    public static void displayWorkoutAsJson(Workout result, Context context, int statusCode){
        context.json(result).status(statusCode);
    }
}
