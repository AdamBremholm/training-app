package model.template;

import model.LiftType;
import model.Set;

import java.util.List;

public class TemplateExercise {

    private String exerciseId;
    private  LiftType liftType;
    private  List<Set> sets;
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

    public LiftType getLiftType() {
        return liftType;
    }

    public void setLiftType(LiftType liftType) {
        this.liftType = liftType;
    }

    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
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
}
