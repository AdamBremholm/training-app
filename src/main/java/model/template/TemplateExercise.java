package model.template;


import java.util.Map;

public class TemplateExercise {

    private String exerciseId;
    private Type type;
    private Map<String, TemplateSet> sets;
    private TemplateSet heaviestSet;
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

    public Map<String, TemplateSet> getSets() {
        return sets;
    }

    public void setSets(Map<String, TemplateSet> sets) {
        this.sets = sets;
    }

    public TemplateSet getHeaviestSet() {
        return heaviestSet;
    }

    public void setHeaviestSet(TemplateSet heaviestSet) {
        this.heaviestSet = heaviestSet;
    }

    public int getTotalRepetitions() {
        return totalRepetitions;
    }

    public void setTotalRepetitions(int totalRepetitions) {
        this.totalRepetitions = totalRepetitions;
    }

    public enum  Type {
        SQUAT,
        BENCHPRESS,
        DEADLIFT,
        POWERCLEAN,
        PRESS,
        CHINS
    }
}
