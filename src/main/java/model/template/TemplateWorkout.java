package model.template;

import com.fasterxml.jackson.annotation.JsonFormat;
import model.Exercise;
import model.User;

import java.time.Instant;
import java.util.List;

public class TemplateWorkout {

    private  String workoutId;
    private  User user;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private  Instant startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private  Instant endTime;
    private  List<Exercise> exercises;
    private  Exercise heaviestExercise;
    private  int totalRepetitions;

    public TemplateWorkout() {
    }

    public String getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(String workoutId) {
        this.workoutId = workoutId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public Exercise getHeaviestExercise() {
        return heaviestExercise;
    }

    public void setHeaviestExercise(Exercise heaviestExercise) {
        this.heaviestExercise = heaviestExercise;
    }

    public int getTotalRepetitions() {
        return totalRepetitions;
    }

    public void setTotalRepetitions(int totalRepetitions) {
        this.totalRepetitions = totalRepetitions;
    }
}
