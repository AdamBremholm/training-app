package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonDeserialize(builder = Workout.Builder.class)
public class Workout implements Reflectable {

    private final String workoutId;
    private final User user;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private final Instant startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private final Instant endTime;
    private final Map<String, Exercise> exercises;
    private final Exercise heaviestExercise;
    private final int totalRepetitions;


    @JsonPOJOBuilder
    public static class Builder {
        private final User user;
        private final Map<String, Exercise> exercises;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        private Instant startTime = null;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        private Instant endTime = null;
        private Exercise heaviestExercise = null;
        private int totalRepetitions = 0;
        private String workoutId = null;

        @JsonCreator
        public Builder(@JsonProperty("user") User user, @JsonProperty("exercises")Map<String, Exercise> exercises){
            this.user = Optional.ofNullable(user).orElseThrow(IllegalArgumentException::new);
            this.exercises = Optional.ofNullable(exercises).orElseThrow(IllegalArgumentException::new);
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
        ifEndTimeBeforeStartTimeThrowException(builder.startTime, builder.endTime);
        this.user = builder.user;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.exercises = builder.exercises;
        this.workoutId = generateRandomUUIDifNotProvided(builder);
        this.heaviestExercise = calculateHeaviestExercisePerWorkout();
        this.totalRepetitions = calculateTotalRepetitionsPerWorkout();

    }

    private void ifEndTimeBeforeStartTimeThrowException(Instant startTime, Instant endTime) {
        if(Optional.ofNullable(startTime).isPresent() && Optional.ofNullable(endTime).isPresent()) {
            if (endTime.isBefore(startTime))
                throw new IllegalArgumentException("endTime cannot be before startTime");
        }
    }

    private String generateRandomUUIDifNotProvided(Builder builder) {
        if(Optional.ofNullable(builder.workoutId).isPresent())
            return builder.workoutId;
        else
            return randomId();
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

    public Map<String, Exercise> getExercises() {
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
          totalLiftedWeight = exercises.values().stream().map(Exercise::liftedPerExercise).mapToDouble(Double::doubleValue).sum();
        }
        return totalLiftedWeight;
    }

    public Exercise calculateHeaviestExercisePerWorkout() {
            return exercises.values().stream()
                    .max(Comparator.comparing(Exercise::getHeaviestSet))
                    .orElseThrow(NoSuchElementException::new);
    }

    public int calculateTotalRepetitionsPerWorkout() {
            return exercises.values().stream()
                    .map(Exercise::getTotalRepetitions)
                    .mapToInt(Integer::intValue)
                    .sum();
    }


    @Override
    public String toString() {
        return "Workout{" +
                "workoutId='" + workoutId + '\'' +
                ", user=" + user +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", exercises=" + exercises +
                ", heaviestExercise=" + heaviestExercise +
                ", totalRepetitions=" + totalRepetitions +
                '}';
    }

    @Override
    public boolean fieldsEnumContainsNonComputedFieldsOfParent(Reflectable reflectable, EnumSet computedFields) {
        Field[] fields = reflectable.getClass().getDeclaredFields();
        List<String> actualFieldNames = Reflectable.getFieldNames(fields);

        List<String> computedEnumList =
                Stream.of(ImmutableFields.values())
                        .map(Enum::name)
                        .collect(Collectors.toList());

        List<String> enumList =
                Stream.of(Workout.Fields.values())
                        .map(Enum::name)
                        .collect(Collectors.toList());

        actualFieldNames.removeAll(computedEnumList);
        actualFieldNames.add(Fields.workoutId.name());

        return actualFieldNames.containsAll(enumList);
    }

    public static enum Fields {

        exercises,
        user,
        workoutId,
        startTime,
        endTime,
    }
}
