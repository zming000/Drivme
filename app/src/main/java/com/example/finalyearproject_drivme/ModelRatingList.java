package com.example.finalyearproject_drivme;

public class ModelRatingList {
    String orderID, rateCompliment, rateComment;
    float rateStar, total;

    public ModelRatingList() {/*empty constructor*/
    }

    public ModelRatingList(String orderID, String rateCompliment, String rateComment, float rateStar, float total) {
        this.orderID = orderID;
        this.rateCompliment = rateCompliment;
        this.rateComment = rateComment;
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
