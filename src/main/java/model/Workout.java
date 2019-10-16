package model;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Workout {

    private final String workoutId;
    private final User user;
    private final Instant startTime;
    private final Instant endTime;
    private final List<Exercise> exercises;

    public static class Builder {
        private final User user;

        private String workoutId = "";
        private Instant startTime = null;
        private Instant endTime = null;
        private List<Exercise> exercises = null;
        private List<ExerciseSet> exerciseSets = null;

        public Builder(User user){
            this.user = user;
        }
        public Builder workoutId(String workoutId){
            this.workoutId = workoutId;
            return this;
        }
        public Builder startTime(Instant startTime){
            this.startTime = startTime;
            return this;
        }
        public Builder endTime(Instant endTime){
            this.endTime = endTime;
            return this;
        }
        public Builder exercises(List<Exercise> exercises){
            this.exercises = exercises;
            return this;
        }

        public Workout build(){
            return new Workout(this, exercises);
        }

    }

    public Workout(Builder builder, List<Exercise> exercises) {
        this.workoutId = builder.workoutId;
        this.user = builder.user;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.exercises = exercises;
    }

    public String getWorkoutId() {
        return workoutId;
    }

    public User getUser() {
        return user;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public double liftedPerWorkOut() {
        double totalLiftedWeight = 0;
        if(Optional.ofNullable(exercises).isPresent()){
          totalLiftedWeight = exercises.stream().map(Exercise::liftedPerExercise).mapToDouble(Double::doubleValue).sum();
        }
        return totalLiftedWeight;
    }

    public ExerciseSet heaviestSet() {
       if(Optional.ofNullable(exercises).isPresent())
       return exercises.stream()
               .map(Exercise::getHeaviestSet)
               .max(Comparator.comparing(ExerciseSet::getWeight))
               .orElseThrow(NoSuchElementException::new);
       else throw new IllegalStateException("exercises not initialized");
    }


}
