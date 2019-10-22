
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.Controller;
import controller.Initialisable;
import repository.MapRepository;

import java.util.HashMap;

import static spark.Spark.*;



public class TrainingServer {


    public static void main(String[] args) {

        ObjectMapper mapper = Initialisable.getObjectMapperWithJavaDateTimeModule();
        Controller controller = Controller.getInstance(MapRepository.getInstance(new HashMap<>()), mapper);
        Initialisable.populate(controller);

        get("/heartbeat", ((request, response) -> {
            response.status(200);
            return "";
        }));

        get("/api/workouts", controller::list);
        post("/api/workouts", controller::save);
        get("/api/workouts/:workoutId", controller::get);
        put("/api/workouts/:workoutId", controller::update);
        delete("/api/workouts/:workoutId", controller::delete);

    }

}
