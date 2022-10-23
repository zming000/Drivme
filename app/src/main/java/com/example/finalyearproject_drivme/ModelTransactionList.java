package com.example.finalyearproject_drivme;

public class ModelTransactionList {
    String transAmount, transDateTime, transType;

    public ModelTransactionList() {/*empty constructor*/
    }

    public ModelTransactionList(String transAmount, String transDateTime, String transType) {
        this.transAmount = transAmount;
        this.transDateTime = transDateTime;
        this.transType = transType;
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
}
