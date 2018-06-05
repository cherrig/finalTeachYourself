package project.teachyourself.model.bodies;

import com.google.gson.annotations.SerializedName;

public class BodySave {
    private String email;

    @SerializedName("title")
    private String category;
    private int score;

    @SerializedName("questions")
    private int questionNumber;

    @SerializedName("timeAvg")
    private float avgResponseTime;

    public BodySave(String email, String category, int score, int questionNumber, float avgResponseTime) {
        this.email = email;
        this.category = category;
        this.score = score;
        this.questionNumber = questionNumber;
        this.avgResponseTime = avgResponseTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public float getAvgResponseTime() {
        return avgResponseTime;
    }

    public void setAvgResponseTime(float avgResponseTime) {
        this.avgResponseTime = avgResponseTime;
    }
}
