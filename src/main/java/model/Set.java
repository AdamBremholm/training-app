package model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonDeserialize(builder = Set.Builder.class)
public class Set implements Comparable<Set> {

    private final String setId;
    private final int repetitions;
    private final double weight;

    @JsonPOJOBuilder
    public static class Builder {

        private String setId = null;
        private int repetitions = 0;
        private double weight = 0;

        public Builder withSetId(String setId) {
            this.setId = setId;
            return this;
        }

        public Builder withRepetitions(int repetitions) {
            this.repetitions = repetitions;
            return this;
        }


        public Builder withWeight(double weight) {
            this.weight = weight;
            return this;
        }


        public Set build(){
            return new Set(this);
        }

    }

    private Set(Builder builder) {
       this.setId = generateSetIdIfNotProvided(builder);
       this.repetitions = checkPositiveInteger(builder.repetitions);
       this.weight = checkPositiveDouble(builder.weight);

    }

    private String generateSetIdIfNotProvided(Builder builder) {
        if(Optional.ofNullable(builder.setId).isPresent())
            return builder.setId;
        else
            return randomId();
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }

    public int getRepetitions() {
        return repetitions;
    }

    public double getWeight() {
        return weight;
    }

    public String getSetId() {
        return setId;
    }

    public double totalWeightPerSet(){
        return repetitions*weight;
    }

    private int checkPositiveInteger(int repetitions) {
        if (repetitions>=0)
            return repetitions;
        else
            throw new NumberFormatException("only positive numbers allowed");
    }

    private double checkPositiveDouble(double weight) {
        if (weight>=0)
            return weight;
        else
            throw new NumberFormatException("only positive numbers allowed");
    }


    @Override
    public int compareTo( Set set) {
        double result = this.getWeight()-set.getWeight();
        if(result>0)
            return 1;
        else if(result<0)
            return -1;
        else return 0;

    }

    @Override
    public String toString() {
        return "Set{" +
                "setId='" + setId + '\'' +
                ", repetitions=" + repetitions +
                ", weight=" + weight +
                '}';
    }

    public boolean fieldsEnumContainsNonComputedFieldsOfParent() {
        Field[] fields = this.getClass().getDeclaredFields();
        List<String> actualFieldNames = Reflectable.getFieldNames(fields);

        List<String> computedEnumList =
                Stream.of(ImmutableFields.values())
                        .map(Enum::name)
                        .collect(Collectors.toList());

        List<String> enumList =
                Stream.of(Set.Fields.values())
                        .map(Enum::name)
                        .collect(Collectors.toList());

        actualFieldNames.removeAll(computedEnumList);

        return actualFieldNames.containsAll(enumList);
    }

    public enum Fields {
        setId,
        repetitions,
        weight
    }

}
