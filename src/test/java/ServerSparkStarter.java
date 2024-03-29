import java.net.HttpURLConnection;
import java.net.URL;


public class ServerSparkStarter extends SparkStarter {

    private static ServerSparkStarter starter;
    private final String host;
    private final String heartBeatPath;

    private ServerSparkStarter(String host, String heartBeatPath){
        this.host = host;
        this.heartBeatPath = heartBeatPath;


    }

    public static ServerSparkStarter get(String host, String heartBeatPath){

        if(ServerSparkStarter.starter==null) {
            ServerSparkStarter.starter = new ServerSparkStarter(host, heartBeatPath);
        }
        return ServerSparkStarter.starter;
    }

    public boolean isRunning(){

        try{
            int serverPort = 4567;
            HttpURLConnection con = (HttpURLConnection)new URL("http",host, serverPort, heartBeatPath).openConnection();
            return con.getResponseCode() != 200;
        }catch(Exception e){
            return true;
        }

    }

    @Override
    public void startServer() {
        // I sometimes use the main method
        // it is harder to start and stop reliably
        String[] args = {};
        TrainingServer.main(args);
        //final CompendiumDevAppsForSpark server = CompendiumDevAppsForSpark.runLocally(4567);
    }


}