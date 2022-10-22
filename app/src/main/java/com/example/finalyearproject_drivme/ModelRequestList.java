package com.example.finalyearproject_drivme;

public class ModelRequestList {
    //declare variables
    String orderID, touristID, driverID, meetDate, meetTime, locality, orderStatus, tripOption;
    float priceDriver, total;

    public ModelRequestList() {/*empty constructor*/}

    public ModelRequestList(String orderID, String touristID, String driverID, String meetDate, String meetTime, String locality, String orderStatus, String tripOption, float priceDriver, float total) {
        this.orderID = orderID;
        this.touristID = touristID;
        this.driverID = driverID;
        this.meetDate = meetDate;
        this.meetTime = meetTime;
        this.locality = locality;
        this.orderStatus = orderStatus;
        this.tripOption = tripOption;
        this.priceDriver = priceDriver;
        this.total = total;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getTouristID() {
        return touristID;
    }

    public void setTouristID(String touristID) {
        this.touristID = touristID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getMeetDate() {
        return meetDate;
    }

    public void setMeetDate(String meetDate) {
        this.meetDate = meetDate;
    }

    public String getMeetTime() {
        return meetTime;
    }

    public void setMeetTime(String meetTime) {
        this.meetTime = meetTime;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getTripOption() {
        return tripOption;
    }

    public void setTripOption(String tripOption) {
        this.tripOption = tripOption;
    }

    public float getPriceDriver() {
        return priceDriver;
    }

    public void setPriceDriver(float priceDriver) {
        this.priceDriver = priceDriver;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
