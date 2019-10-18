
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.Controller;
import controller.ControllerFactory;
import controller.Initialisable;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;
import model.*;
import repository.MapRepository;

import java.util.*;

public class Server {




    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        ObjectMapper mapper = Initialisable.getObjectMapperWithJavaDateTimeModule();

        JavalinJackson.configure(mapper);



        Controller controller = ControllerFactory.getMapRepositoryController();
        Initialisable.populate(controller);


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

    }

}
