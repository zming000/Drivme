package com.example.finalyearproject_drivme;

public class ModelDriverList {
    String userID, firstName, lastName, gender, priceDay;
    float rating;

    public ModelDriverList(){/*empty constructor*/}

    public ModelDriverList(String userID, String firstName, String lastName, String gender, String priceDay, float rating) {
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

    public String getPriceDay() {
        return priceDay;
    }

    public void setPriceDay(String priceDay) {
        this.priceDay = priceDay;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
