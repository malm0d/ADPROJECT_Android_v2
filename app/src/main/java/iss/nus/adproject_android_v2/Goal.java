package iss.nus.adproject_android_v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Goal implements Serializable {
    private Integer id;
    private String goalDescription;
    private String totalMealCount;
    private String targetCount;

    private String status;
    private String startDate;
    private String endDate;

    private User author;
    public Goal(int id, String goalDescription, String totalMealCount, String targetCount) {
        this.id = id;
        this.goalDescription = goalDescription;
        this.totalMealCount = totalMealCount;
        this.targetCount = targetCount;
    }

    public Goal() {
    }


    public Goal(int id, String goalDescription, String totalMealCount, String targetCount, String status,String startDate,String endDate,User author) {
        this.id = id;
        this.goalDescription = goalDescription;
        this.totalMealCount = totalMealCount;
        this.targetCount = targetCount;
        this.author = author;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Integer getId() {
        return id;
    }

    public String getGoalDescription() {
        return goalDescription;
    }

    public String getTotalMealCount() {
        return totalMealCount;
    }

    public String getTargetCount() {
        return targetCount;
    }
    public String getStatus(){
        return status;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public User getAuthor() {
        return author;
    }
}

