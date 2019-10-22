import com.fasterxml.jackson.databind.ObjectMapper;
import controller.Controller;
import controller.Initialisable;

import okhttp3.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import java.io.IOException;


import static org.junit.Assert.assertEquals;


public class TrainingServerTest {

    private final OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = "http://localhost:4567/api";



    @BeforeClass
    public static void ensureAppIsRunning(){
        ServerSparkStarter.
                get("localhost", "/heartbeat" ).
                startSparkAppIfNotRunning(4567);
    }



    @Test
    public void getWorkout() throws IOException {

        Request request = new Request.Builder()
                .url(BASE_URL + "/workouts")
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        String res = responseBody.string();
        System.out.println(res);
        assertEquals(200, response.code());


    }

}