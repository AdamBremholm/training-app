package model.template;

import model.Exercise;
import model.Set;

import java.util.List;

public class TemplateExercise {

    private String exerciseId;
    private Type type;
    private  List<TemplateSet> sets;
    private  Set heaviestSet;
    private  int totalRepetitions;

    public TemplateExercise() {
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<TemplateSet> getSets() {
        return sets;
    }

    public void setSets(List<TemplateSet> sets) {
        this.sets = sets;
    }

    public Set getHeaviestSet() {
        return heaviestSet;
    }

    public void setHeaviestSet(Set heaviestSet) {
        this.heaviestSet = heaviestSet;
    }

    public int getTotalRepetitions() {
        return totalRepetitions;
    }

    public void setTotalRepetitions(int totalRepetitions) {
        this.totalRepetitions = totalRepetitions;
    }

    public static enum  Type {
        SQUAT,
        BENCHPRESS,
        DEADLIFT,
        POWERCLEAN,
        PRESS,
        CHINS;
    }
}
