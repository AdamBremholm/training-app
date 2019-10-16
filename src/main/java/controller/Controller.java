package controller;

import io.javalin.http.Context;
import model.Workout;
import repository.Repository;

import java.util.List;

public class Controller {

    private final Repository repository;
    private final Context context;

    private Controller(Repository repository, Context context){
        this.context = context;
        this.repository = repository;
    }
    public static Controller getInstance(Repository repository, Context context){
        return new Controller(repository, context);
    }

    public Repository getRepository() {
        return repository;
    }
}
