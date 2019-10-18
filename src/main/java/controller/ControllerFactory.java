package controller;

import repository.JPARepository;
import repository.ListRepository;
import repository.MapRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class ControllerFactory {

    public static Controller getListRepositoryController(){
        return Controller.getInstance(ListRepository.getInstance(new ArrayList<>()), Initialisable.getObjectMapperWithJavaDateTimeModule());
    }

    public static Controller getMapRepositoryController(){
        return Controller.getInstance(MapRepository.getInstance(new HashMap<>()), Initialisable.getObjectMapperWithJavaDateTimeModule());
    }

    public static Controller getJPARepositoryController(){
        return Controller.getInstance(JPARepository.getInstance(), Initialisable.getObjectMapperWithJavaDateTimeModule());
    }
}
