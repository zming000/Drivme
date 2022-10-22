package com.example.finalyearproject_drivme;

public class ModelRatingList {
    //declare variables
    String orderID, rateCompliment, rateComment, tripOption;
    float rateStar, total;

    public ModelRatingList() {/*empty constructor*/
    }

    public ModelRatingList(String orderID, String rateCompliment, String rateComment, String tripOption, float rateStar, float total) {
        this.orderID = orderID;
        this.rateCompliment = rateCompliment;
        this.rateComment = rateComment;
        this.tripOption = tripOption;
        this.rateStar = rateStar;
        this.total = total;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getRateCompliment() {
        return rateCompliment;
    }

    public void setRateCompliment(String rateCompliment) {
        this.rateCompliment = rateCompliment;
    }

    public String getRateComment() {
        return rateComment;
    }

    public void setRateComment(String rateComment) {
        this.rateComment = rateComment;
    }

    public String getTripOption() {
        return tripOption;
    }

    public void setTripOption(String tripOption) {
        this.tripOption = tripOption;
    }

    public float getRateStar() {
        return rateStar;
    }

    public void setRateStar(float rateStar) {
        this.rateStar = rateStar;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
