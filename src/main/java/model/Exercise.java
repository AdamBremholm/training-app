package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonDeserialize(builder = Exercise.Builder.class)
public class Exercise implements Reflectable {

    private String exerciseId;
    private final Type type;
    private final Map<String, Set> sets;
    private final Set heaviestSet;
    private final int totalRepetitions;



    @JsonPOJOBuilder
    public static class Builder {

        private final Type type;
        private final Map<String, Set> sets;

        private String exerciseId = null;
        private int totalRepetitions = 0;
        private Set heaviestSet = null;

        @JsonCreator
        public Builder(@JsonProperty("type") Type type, @JsonProperty("sets") Map<String, Set> sets) {
            this.type = Optional.ofNullable(type).orElseThrow(IllegalArgumentException::new);
            this.sets = Optional.ofNullable(sets).orElseThrow(IllegalArgumentException::new);

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
        this.type = builder.type;
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

    public Type getType() {
        return type;
    }

    public Map<String, Set> getSets() {
        return sets;
    }

    public double liftedPerExercise() {
        double totalLiftedWeight = 0;
        if(Optional.ofNullable(sets).isPresent()){
            totalLiftedWeight = sets.values().stream().map(Set::totalWeightPerSet).mapToDouble(Double::doubleValue).sum();
        }
        return totalLiftedWeight;
    }

    private Set calculateHeaviestLiftedSet() {
        return sets.values().stream()
                .max(Comparator.comparing(Set::getWeight))
                .orElseThrow(NoSuchElementException::new);
    }

    private int calculateTotalRepetitions() {
            return sets.values().stream()
                    .map(Set::getRepetitions)
                    .mapToInt(Integer::intValue).sum();

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
                ", type=" + type +
                ", sets=" + sets +
                ", heaviestSet=" + heaviestSet +
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
                Stream.of(Exercise.Fields.values())
                        .map(Enum::name)
                        .collect(Collectors.toList());

        actualFieldNames.removeAll(computedEnumList);

        return actualFieldNames.containsAll(enumList);
    }

    public static enum Type {
        SQUAT,
        BENCHPRESS,
        DEADLIFT,
        POWERCLEAN,
        PRESS,
        CHINS;

    }

    public static enum Fields {
                sets,
                set,
                exerciseId,
                type;
    }


}
