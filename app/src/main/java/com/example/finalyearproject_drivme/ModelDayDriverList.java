package com.example.finalyearproject_drivme;

public class ModelDayDriverList {
    //declare variables
    String userID, firstName, lastName, gender;
    float priceDay, rating;

    public ModelDayDriverList(){/*empty constructor*/}

    public ModelDayDriverList(String userID, String firstName, String lastName, String gender, float priceDay, float rating) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.priceDay = priceDay;
        this.rating = rating;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public float getPriceDay() {
        return priceDay;
    }

    public void setPriceDay(float priceDay) {
        this.priceDay = priceDay;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
