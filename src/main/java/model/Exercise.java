package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.*;

@JsonDeserialize(builder = Exercise.Builder.class)
public class Exercise  {

    private String exerciseId;
    private final LiftType liftType;
    private final List<Set> sets;
    private final Set heaviestSet;
    private final int totalRepetitions;


    @JsonPOJOBuilder
    public static class Builder {

        private final LiftType liftType;
        private final List<Set> sets;

        private String exerciseId = null;
        private int totalRepetitions = 0;
        private Set heaviestSet = null;

        @JsonCreator
        public Builder(@JsonProperty("liftType") LiftType liftType, @JsonProperty("sets") List<Set> sets) {
            this.liftType = liftType;
            this.sets = sets;

        }

        public Builder withExerciseId(String exerciseId) {
           this.exerciseId = exerciseId;
            return this;
        }

        public Builder withTotalRepetitions(int totalRepetitions) {
            this.totalRepetitions = totalRepetitions;
            return this;
        }

        public Builder withHeaviestSet(Set heaviestSet) {
            this.heaviestSet = heaviestSet;
            return this;
        }


        public Exercise build(){
            return new Exercise(this);
        }

    }

    public Exercise(Builder builder) {
        this.exerciseId = generateRandomUUIDifNotProvided(builder);
        this.liftType = builder.liftType;
        this.sets = builder.sets;
        this.totalRepetitions = calculateTotalRepetitions();
        this.heaviestSet = calculateHeaviestLiftedSet();
    }



    private String generateRandomUUIDifNotProvided(Builder builder) {
        if(Optional.ofNullable(builder.exerciseId).isPresent())
            return builder.exerciseId;
        else
            return randomId();
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public LiftType getLiftType() {
        return liftType;
    }

    public List<Set> getSets() {
        return sets;
    }

    public double liftedPerExercise() {
        double totalLiftedWeight = 0;
        if(Optional.ofNullable(sets).isPresent()){
            totalLiftedWeight = sets.stream().map(Set::totalWeightPerSet).mapToDouble(Double::doubleValue).sum();
        }
        return totalLiftedWeight;
    }

    private Set calculateHeaviestLiftedSet() {
        if (Optional.ofNullable(sets).isPresent())
        return sets.stream()
                .max(Comparator.comparing(Set::getWeight))
                .orElseThrow(NoSuchElementException::new);
        else throw new IllegalStateException("sets not initialized");
    }

    private int calculateTotalRepetitions() {
        if (Optional.ofNullable(sets).isPresent()){
            return sets.stream()
                    .map(Set::getRepetitions)
                    .mapToInt(Integer::intValue).sum();
        }
        else throw new IllegalStateException("sets not initialized");
    }

    public Set getHeaviestSet() {
        return heaviestSet;
    }

    public int getTotalRepetitions() {
        return totalRepetitions;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "exerciseId='" + exerciseId + '\'' +
                ", liftType=" + liftType +
                ", sets=" + sets +
                ", heaviestSet=" + heaviestSet +
                ", totalRepetitions=" + totalRepetitions +
                '}';
    }
}
