package com.facepp.demo;

/**
 * Created by zwj on 2017/9/18.
 */

public class FaceCompareData {

    /**
     * confidence : 96.065
     * request_id : 1505713886,431c0e1a-70c9-46ae-a2db-1e202620c23f
     * time_used : 487
     * thresholds : {"1e-3":62.327,"1e-5":73.975,"1e-4":69.101}
     */

    private double confidence;
    private String request_id;
    private int time_used;
    private ThresholdsBean thresholds;

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public int getTime_used() {
        return time_used;
    }

    public void setTime_used(int time_used) {
        this.time_used = time_used;
    }

    public ThresholdsBean getThresholds() {
        return thresholds;
    }

    public void setThresholds(ThresholdsBean thresholds) {
        this.thresholds = thresholds;
    }

    public static class ThresholdsBean {

    }
}
