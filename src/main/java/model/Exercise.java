package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


public class Exercise  {

    private final LiftType liftType;
    private final List<Set> sets;
    private final Set heaviestSet;
    private final int totalRepetitions;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Exercise(@JsonProperty("liftType") LiftType liftType, @JsonProperty("sets") List<Set> sets) {
        this.liftType = liftType;
        this.sets = sets;
        this.heaviestSet = calculateHeaviestLiftedSet();
        this.totalRepetitions = calculateTotalRepetitions();
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
                "liftType=" + liftType +
                ", sets=" + sets +
                ", heaviestSet=" + heaviestSet +
                ", totalRepetitions=" + totalRepetitions +
                '}';
    }
}
