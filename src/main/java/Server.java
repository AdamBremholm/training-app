
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.Controller;
import controller.ControllerFactory;
import controller.Initialisable;
import static spark.Spark.*;
import model.*;
import spark.Spark;


public class Server {




    public static void main(String[] args) {



        ObjectMapper mapper = Initialisable.getObjectMapperWithJavaDateTimeModule();
        Controller controller = ControllerFactory.getMapRepositoryController();
        Initialisable.populate(controller);



        get("/api/workouts", (request, response) -> {
            response.status(200);
            response.type("application/json");
            return controller.list(request);
        });



/*
        app.get("/api/workouts", controller::list);

        app.post("/api/workouts", ctx -> {
            // some code
          Workout res = controller.save(ctx);
           ctx.json(res).status(201);
        });
        app.exception(JsonProcessingException.class, (e, ctx) -> {
            ctx.result(e.getMessage()).status(501);
        });
        app.exception(Exception.class, (e, ctx) -> {
            ctx.result(e.getMessage()).status(500);
        });

 */

    }

}
