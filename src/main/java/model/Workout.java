package model;

import java.time.Instant;

public class Workout {

    private final String workoutId;
    private final User user;
    private final Instant startTime;
    private final Instant endTime;
    private final int reps;
    private final int sets;
    private final double weight;
    private final Exercise exercise;

    public static class Builder {
        private final User user;
        private final Exercise exercise;

        private String workoutId = "";
        private Instant startTime = null;
        private Instant endTime = null;
        private int reps = 0;
        private int sets = 0;
        private double liftingWeight = 0;

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
        public Builder reps(int reps){
            this.reps = reps;
            return this;
        }
        public Builder sets(int sets){
            this.sets = sets;
            return this;
        }
        public Builder liftingWeight(double liftingWeight){
            this.liftingWeight = liftingWeight;
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
        this.reps = builder.reps;
        this.sets = builder.sets;
        this.weight = builder.liftingWeight;
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

    public int getReps() {
        return reps;
    }

    public int getSets() {
        return sets;
    }

    public double getWeight() {
        return weight;
    }

    public Exercise getExercise() {
        return exercise;
    }
}
