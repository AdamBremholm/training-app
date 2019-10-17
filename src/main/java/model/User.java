package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.UUID;

@JsonDeserialize(builder = User.Builder.class)
public class User {

    private final String userId;
    private final String username;
    private final String email;
    private final String password;
    private final double weight;
    private final double height;

    @JsonPOJOBuilder
    public static class Builder {

        private String userId;
        private final String username;
        private final String email;
        private final String password;
        private double weight;
        private double height;

        @JsonCreator
        public Builder(@JsonProperty("username") String username, @JsonProperty("email") String email, @JsonProperty("password") String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }
        public Builder withWeight(double weight) {
            this.weight = weight;
            return this;
        }
        public Builder withHeight(double height) {
            this.height = height;
            return this;
        }
        public User build(){
            return new User(this);
        }

    }

    public User(Builder builder) {
        this.userId = builder.userId;
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
        this.weight = builder.weight;
        this.height = builder.height;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public String randomId(){
        return UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", weight=" + weight +
                ", height=" + height +
                '}';
    }
}
