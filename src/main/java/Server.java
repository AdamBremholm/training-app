
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.Controller;
import controller.ControllerFactory;
import controller.Initialisable;
import static spark.Spark.*;
import model.*;
import spark.Spark;

import java.util.NoSuchElementException;


public class Server {


    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;


    public static void main(String[] args) {



        ObjectMapper mapper = Initialisable.getObjectMapperWithJavaDateTimeModule();
        Controller controller = ControllerFactory.getMapRepositoryController();
        Initialisable.populate(controller);



        get("/api/workouts", (request, response) -> {
                response.status(200);
                response.type("application/json");
                return controller.list();
        });

        post("/api/workouts", (request, response) -> {
            try {
                response.status(201);
                response.type("application/json");
                return controller.save(request);
            } catch (JsonParseException jpe) {
                response.status(HTTP_BAD_REQUEST);
                return jpe.toString();
            }
        });

        get("/api/workouts/:workoutId", (request, response) -> {
            try {
                response.status(200);
                response.type("application/json");
                return controller.get(request);
            } catch (JsonParseException jpe) {
                response.status(HTTP_BAD_REQUEST);
                return jpe.toString();
            } catch (NoSuchElementException nse){
                response.status(HTTP_NOT_FOUND);
                return nse.toString();
            }
        });

        put("/api/workouts/:workoutId", (request, response) -> {
            try {
                response.status(200);
                response.type("application/json");
                return controller.update(request);
            } catch (JsonParseException jpe) {
                response.status(HTTP_BAD_REQUEST);
                return jpe.toString();
            } catch (NoSuchElementException nse){
                response.status(HTTP_NOT_FOUND);
                return nse.toString();
            }
        });

        delete("/api/workouts/:workoutId", (request, response) -> {
            try {
                response.status(204);
                response.type("application/json");
                controller.delete(request.params("workoutId"));
                return "";
            } catch (JsonParseException jpe) {
                response.status(HTTP_BAD_REQUEST);
                return jpe.toString();
            } catch (NoSuchElementException nse){
                response.status(HTTP_NOT_FOUND);
                return nse.toString();
            }
        });




    }

}
