
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.Controller;
import controller.ControllerFactory;
import controller.Initialisable;
import static spark.Spark.*;



public class Server {


    public static void main(String[] args) {

        ObjectMapper mapper = Initialisable.getObjectMapperWithJavaDateTimeModule();
        Controller controller = ControllerFactory.getMapRepositoryController();
        Initialisable.populate(controller);

        get("/api/workouts", controller::list);
        post("/api/workouts", controller::save);
        get("/api/workouts/:workoutId", controller::get);
        put("/api/workouts/:workoutId", controller::update);
        delete("/api/workouts/:workoutId", controller::delete);

    }

}
