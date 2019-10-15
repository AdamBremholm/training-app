package controller;

import io.javalin.http.Context;
import repository.Repository;

public class Controller {

    private Repository repository;
    private Context context;

    private Controller(Repository repository, Context context){
        this.context = context;
        this.repository = repository;
    }
    public static Controller getInstance(Repository repository, Context context){
        return new Controller(repository, context);
    }


}
