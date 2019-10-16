package model;

public class WorkoutSet {

    private int repetitions;
    private double weight;

    public WorkoutSet(int repetitions, double weight) {
        this.repetitions = repetitions;
        this.weight = weight;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public double getWeight() {
        return weight;
    }

    public double totalWeightPerSet(){
        return repetitions*weight;
    }
}
