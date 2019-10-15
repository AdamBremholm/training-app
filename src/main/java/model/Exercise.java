package model;

public class Exercise {


    public Exercise(String name, TrainingType type) {
        this.name = name;
        this.type = type;
    }

    private final String name;
    private final TrainingType type;

    public String getName() {
        return name;
    }

    public TrainingType getType() {
        return type;
    }
}
