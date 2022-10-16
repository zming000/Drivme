package com.example.finalyearproject_drivme;

public class ModelRequestList {
    String orderID, touristID, driverID, startDate, time, locality, orderStatus;
    float priceDay, total;

    public ModelRequestList() {/*empty constructor*/}

    public ModelRequestList(String orderID, String touristID, String driverID, String startDate, String time, String locality, String orderStatus, float priceDay, float total) {
        this.orderID = orderID;
        this.touristID = touristID;
        this.driverID = driverID;
        this.startDate = startDate;
        this.time = time;
        this.locality = locality;
        this.orderStatus = orderStatus;
        this.priceDay = priceDay;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public float getPriceDay() {
        return priceDay;
    }

    public void setPriceDay(float priceDay) {
        this.priceDay = priceDay;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
