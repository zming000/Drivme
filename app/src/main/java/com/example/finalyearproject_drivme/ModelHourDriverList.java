package com.example.finalyearproject_drivme;

public class ModelHourDriverList {
    //declare variables
    String userID, firstName, lastName, gender;
    float priceHour, rating;

    public ModelHourDriverList() {/*empty constructor*/
    }

    public ModelHourDriverList(String userID, String firstName, String lastName, String gender, float priceHour, float rating) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.priceHour = priceHour;
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

    public float getPriceHour() {
        return priceHour;
    }

    public void setPriceHour(float priceHour) {
        this.priceHour = priceHour;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
