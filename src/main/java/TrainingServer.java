
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.Controller;
import controller.Initialisable;
import repository.MapRepository;

import java.util.HashMap;

import static model.Workout.Fields.workoutId;
import static model.User.Fields.userId;
import static spark.Spark.*;



class TrainingServer {



    public static void main(String[] args) {

        ObjectMapper mapper = Initialisable.getObjectMapperWithJavaDateTimeModule();
        Controller controller = Controller.getInstance(MapRepository.getInstance(new HashMap<>()), mapper);
        Initialisable.populate(controller);

        get("/heartbeat", controller::heartBeat);
        get("/api/workouts", controller::list);
        post("/api/workouts", controller::save);
        get("/api/workouts/:"+workoutId.name(), controller::get);
        put("/api/workouts/:"+workoutId.name(), controller::update);
        delete("/api/workouts/:"+workoutId.name(), controller::delete);
        get("/api/stats/totalWeightLifted?:"+ userId.name(), controller::totalLiftedWeightByUser);
        get("/api/stats/heaviestLift?:"+userId.name(), controller::heaviestLiftByUser);
        get("/api/stats/totalLifts?:"+userId.name(), controller::totalLiftsByUser);

    }

}
