
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.Controller;
import utils.Init;
import repository.MapRepository;

import java.util.HashMap;

import static model.Workout.Fields.workoutId;
import static model.User.Fields.userId;
import static spark.Spark.*;



class TrainingServer {



    public static void main(String[] args) {

        ObjectMapper mapper = Init.getObjectMapperWithJavaDateTimeModule();
        Controller controller = Controller.getInstance(MapRepository.getInstance(new HashMap<>()), mapper);
        Init.populate(controller);

        get("/heartbeat", controller::heartBeat);
        get("/api/workouts", controller::list);
        post("/api/workouts", controller::save);
        get("/api/workouts/:"+workoutId.name(), controller::get);
        put("/api/workouts/:"+workoutId.name(), controller::update);
        delete("/api/workouts/:"+workoutId.name(), controller::delete);
        get("/api/stats/totalweightlifted/:"+ userId.name(), controller::totalLiftedWeightByUser);
        get("/api/stats/heaviestlift/:"+userId.name(), controller::heaviestLiftByUser);
        get("/api/stats/totallifts/:"+userId.name(), controller::totalLiftsByUser);

    }

}
