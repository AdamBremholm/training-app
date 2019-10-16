package model;

public class ExerciseSet {

    private final int repetitions;
    private final double weight;

    public ExerciseSet(int repetitions, double weight) {
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
