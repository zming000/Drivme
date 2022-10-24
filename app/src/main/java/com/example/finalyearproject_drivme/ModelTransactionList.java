package com.example.finalyearproject_drivme;

public class ModelTransactionList {
    String transAmount, transDateTime, transType, orderID;

    public ModelTransactionList() {/*empty constructor*/
    }

    public ModelTransactionList(String transAmount, String transDateTime, String transType, String orderID) {
        this.transAmount = transAmount;
        this.transDateTime = transDateTime;
        this.transType = transType;
        this.orderID = orderID;
    }

    public String getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(String transAmount) {
        this.transAmount = transAmount;
    }

    public String getTransDateTime() {
        return transDateTime;
    }

    public void setTransDateTime(String transDateTime) {
        this.transDateTime = transDateTime;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
}
