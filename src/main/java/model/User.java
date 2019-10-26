package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import utils.Reflection;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        private final String username;
        private final String email;
        private final String password;

        private double weight = 0;
        private double height = 0;
        private String userId = null;

        @JsonCreator
        public Builder(@JsonProperty("username") String username, @JsonProperty("email") String email, @JsonProperty("password") String password) {
            this.username = Optional.ofNullable(username).orElseThrow(IllegalArgumentException::new);
            this.email = Optional.ofNullable(email).orElseThrow(IllegalArgumentException::new);
            this.password = Optional.ofNullable(password).orElseThrow(IllegalArgumentException::new);
        }


        public Builder withWeight(double weight) {
            this.weight = weight;
            return this;
        }
        public Builder withHeight(double height) {
            this.height = height;
            return this;
        }
        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public User build(){
            return new User(this);
        }

    }

    private User(Builder builder) {
        this.userId = generateRandomUUIDifNotProvided(builder);
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
        this.weight = checkPositiveDouble(builder.weight);
        this.height = checkPositiveDouble(builder.height);
    }

    private String generateRandomUUIDifNotProvided(Builder builder) {
        if(Optional.ofNullable(builder.userId).isPresent())
            return builder.userId;
        else
             return randomId();
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

    private double checkPositiveDouble(double number) {
        if (number>=0)
            return number;
        else
            throw new NumberFormatException("only positive numbers allowed");
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

    public void fieldsEnumContainsNonComputedFieldsOfParent() {
        Field[] fields = this.getClass().getDeclaredFields();
        List<String> actualFieldNames = Reflection.getFieldNames(fields);

        List<String> enumList =
                Stream.of(User.Fields.values())
                        .map(Enum::name)
                        .collect(Collectors.toList());

        if(!actualFieldNames.containsAll(enumList) || !enumList.containsAll(actualFieldNames))
            throw new IllegalStateException("all fields are not present in type enums or fields present in enum which are not actual fields");
    }

    public enum Fields {

        userId,
        email,
        password,
        height,
        weight,
        username

    }

}
