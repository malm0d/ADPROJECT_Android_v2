package iss.nus.adproject_android_v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Goal implements Serializable {
    private String id;
    private String goalDescription;
    private String totalMealCount;
    private String targetCount;

    public Goal(String id, String goalDescription, String totalMealCount, String targetCount) {
        this.id = id;
        this.goalDescription = goalDescription;
        this.totalMealCount = totalMealCount;
        this.targetCount = targetCount;
    }

    public Goal() {
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
}
