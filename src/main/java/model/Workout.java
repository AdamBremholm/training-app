package model;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class Workout {

    private final String workoutId;
    private final User user;
    private final Instant startTime;
    private final Instant endTime;
    private final List<WorkoutSet> workoutSets;
    private final Exercise exercise;

    public static class Builder {
        private final User user;
        private final Exercise exercise;

        private String workoutId = "";
        private Instant startTime = null;
        private Instant endTime = null;
        private List<WorkoutSet> workoutSets = null;

        public Builder(User user, Exercise exercise){
            this.user = user;
            this.exercise = exercise;
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

        public Builder workoutSets(List<WorkoutSet> workoutSets){
            this.workoutSets = workoutSets;
            return this;
        }

        public Workout build(){
            return new Workout(this);
        }

    }

    public Workout(Builder builder) {
        this.workoutId = builder.workoutId;
        this.user = builder.user;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.workoutSets = builder.workoutSets;
        this.exercise = builder.exercise;
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

    public List<WorkoutSet> getWorkoutSets() {
        return workoutSets;
    }

    public Exercise getExercise() {
        return exercise;
    }
    public double liftedPerWorkOut() {
        double totalLiftedWeight = 0;
        if(Optional.ofNullable(workoutSets).isPresent()){
          totalLiftedWeight = workoutSets.stream().map(WorkoutSet::totalWeightPerSet).mapToDouble(Double::doubleValue).sum();
        }
        return totalLiftedWeight;
    }

}
