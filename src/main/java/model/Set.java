package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class Set implements Comparable<Set> {

    private final int repetitions;
    private final double weight;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Set(@JsonProperty("repetitions") int repetitions, @JsonProperty("weight") double weight) {
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


    @Override
    public int compareTo(@NotNull Set set) {
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
                "repetitions=" + repetitions +
                ", weight=" + weight +
                '}';
    }
}
