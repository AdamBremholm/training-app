import spark.Spark;

abstract class SparkStarter {

    private int sparkPort = 4567;

    protected abstract boolean isRunning();
    protected abstract void startServer();

    public void startSparkAppIfNotRunning(int expectedPort){


        sparkPort = expectedPort;


        try {

            System.out.println("Checking if running for integration tests");

            if(!isRunning()) {

                System.out.println("Not running - starting");

                startServer();

                System.out.println("Running spark to start");

            }
        }catch(IllegalStateException e){
            e.printStackTrace();
            System.out.println("TODO: Investigate - " + e.getMessage());
        }

        try{
            sparkPort = Spark.port();
        }catch(Exception e){
            System.out.println("Warning: could not get actual Spark port");
        }

        waitForServerToRun();
    }



    private void waitForServerToRun() {
        int tries = 10;
        while(tries>0) {
            if(!isRunning()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }else{
                return;
            }
            tries --;
        }

        System.out.println("Warning: Server might not have started");
    }

}