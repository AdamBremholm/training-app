package model;

public class User {

    private final String userId;
    private final String username;
    private final String email;
    private final String password;
    private final double weight;
    private final double height;


    public static class Builder {

        private String userId;
        private final String username;
        private final String email;
        private final String password;
        private double weight;
        private double height;

        public Builder(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        public Builder weight(double weight) {
            this.weight = weight;
            return this;
        }
        public Builder height(double height) {
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
}
