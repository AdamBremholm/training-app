package model.template;

import com.fasterxml.jackson.annotation.JsonFormat;


import java.time.Instant;
import java.util.List;

public class TemplateWorkout {

    private  String workoutId;
    private  TemplateUser user;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private  Instant startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private  Instant endTime;
    private  List<TemplateExercise> exercises;
    private  TemplateExercise heaviestExercise;
    private  int totalRepetitions;

    public TemplateWorkout() {
    }

    public String getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(String workoutId) {
        this.workoutId = workoutId;
    }

    public TemplateUser getUser() {
        return user;
    }

    public void setUser(TemplateUser user) {
        this.user = user;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public List<TemplateExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<TemplateExercise> exercises) {
        this.exercises = exercises;
    }

    public TemplateExercise getHeaviestExercise() {
        return heaviestExercise;
    }

    public void setHeaviestExercise(TemplateExercise heaviestExercise) {
        this.heaviestExercise = heaviestExercise;
    }

    public int getTotalRepetitions() {
        return totalRepetitions;
    }

    public void setTotalRepetitions(int totalRepetitions) {
        this.totalRepetitions = totalRepetitions;
    }
}
