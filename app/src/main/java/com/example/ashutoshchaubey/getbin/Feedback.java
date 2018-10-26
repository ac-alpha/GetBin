package com.example.ashutoshchaubey.getbin;

/**
 * Created by ashutoshchaubey on 16/03/18.
 */

public class Feedback {

    public String feedback;
    public String activityName;

    public Feedback(String feedback) {
        this.feedback = feedback;
    }

    public Feedback(String feedback, String activityName) {
        this.feedback = feedback;
        this.activityName = activityName;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
