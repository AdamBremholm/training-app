package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.time.Instant;
import java.util.*;

@JsonDeserialize(builder = Workout.Builder.class)
public class Workout {

    private final String workoutId;
    private final User user;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private final Instant startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private final Instant endTime;
    private final List<Exercise> exercises;
    private final Exercise heaviestExercise;
    private final int totalRepetitions;


    @JsonPOJOBuilder
    public static class Builder {
        private final User user;
        private final List<Exercise> exercises;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        private Instant startTime = null;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        private Instant endTime = null;
        private Exercise heaviestExercise = null;
        private int totalRepetitions = 0;
        private String workoutId = null;

        @JsonCreator
        public Builder(@JsonProperty("user") User user, @JsonProperty("exercises") List<Exercise> exercises){
            this.user = user;
            this.exercises = exercises;
        }

        public Builder withStartTime(Instant startTime){
            this.startTime = startTime;
            return this;
        }
        public Builder withEndTime(Instant endTime){
            this.endTime = endTime;
            return this;
        }

        public Builder withHeaviestExercise(Exercise exercise){
            this.heaviestExercise = exercise;
            return this;
        }
        public Builder withTotalRepetitions(int totalRepetitions){
            this.totalRepetitions = totalRepetitions;
            return this;
        }

        public Builder withWorkoutId(String workoutId){
            this.workoutId = workoutId;
            return this;
        }


        public Workout build(){
            return new Workout(this);
        }

    }

    public Workout(Builder builder) {
        this.user = builder.user;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.exercises = builder.exercises;
        this.workoutId = generateRandomUUIDifNotProvided(builder);
        this.heaviestExercise = calculateHeaviestExercisePerWorkoutIfNotProvided(builder);
        this.totalRepetitions = calculateTotalRepetitionsIfNotProvided(builder);

    }

    private String generateRandomUUIDifNotProvided(Builder builder) {
        if(Optional.ofNullable(builder.workoutId).isPresent())
            return builder.workoutId;
        else
            return randomId();
    }

    public Exercise calculateHeaviestExercisePerWorkoutIfNotProvided(Builder builder){
        if (Optional.ofNullable(builder.heaviestExercise).isPresent())
            return builder.heaviestExercise;
        else
            return calculateHeaviestExercisePerWorkout();
    }

    public int calculateTotalRepetitionsIfNotProvided(Builder builder){
        if (builder.totalRepetitions!=0)
            return  builder.totalRepetitions;
        else
            return calculateTotalRepetitionsPerWorkout();
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

    public String randomId(){
       return UUID.randomUUID().toString();
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public Exercise getHeaviestExercise() {
        return heaviestExercise;
    }

    public int getTotalRepetitions() {
        return totalRepetitions;
    }

    public double liftedPerWorkOut() {
        double totalLiftedWeight = 0;
        if(Optional.ofNullable(exercises).isPresent()){
          totalLiftedWeight = exercises.stream().map(Exercise::liftedPerExercise).mapToDouble(Double::doubleValue).sum();
        }
        return totalLiftedWeight;
    }

    public Exercise calculateHeaviestExercisePerWorkout() {
        if(Optional.ofNullable(exercises).isPresent())
            return exercises.stream()
                    .max(Comparator.comparing(Exercise::getHeaviestSet))
                    .orElseThrow(NoSuchElementException::new);
        else
            throw new IllegalStateException("exercises not initialized");
    }

    public int calculateTotalRepetitionsPerWorkout() {
        if(Optional.ofNullable(exercises).isPresent())
            return exercises.stream()
                    .map(Exercise::getTotalRepetitions)
                    .mapToInt(Integer::intValue)
                    .sum();
        else
            throw new IllegalStateException("exercises not initialized");
    }


}
