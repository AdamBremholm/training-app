package model;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Exercise {

    private final ExerciseType exerciseType;
    private final List<ExerciseSet> exerciseSets;
    private final ExerciseSet heaviestSet;

    public Exercise(ExerciseType exerciseType, List<ExerciseSet> exerciseSets) {
        this.exerciseType = exerciseType;
        this.exerciseSets = exerciseSets;
        this.heaviestSet = getHeaviestLiftedSet();
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public List<ExerciseSet> getExerciseSets() {
        return exerciseSets;
    }

    public double liftedPerExercise() {
        double totalLiftedWeight = 0;
        if(Optional.ofNullable(exerciseSets).isPresent()){
            totalLiftedWeight = exerciseSets.stream().map(ExerciseSet::totalWeightPerSet).mapToDouble(Double::doubleValue).sum();
        }
        return totalLiftedWeight;
    }

    private ExerciseSet getHeaviestLiftedSet() {
        if (Optional.ofNullable(exerciseSets).isPresent())
        return exerciseSets.stream().max(Comparator.comparing(ExerciseSet::getWeight)).orElseThrow(NoSuchElementException::new);
        else throw new IllegalStateException("exerciseSets not Initialized");
    }

    public ExerciseSet getHeaviestSet() {
        return heaviestSet;
    }
}
