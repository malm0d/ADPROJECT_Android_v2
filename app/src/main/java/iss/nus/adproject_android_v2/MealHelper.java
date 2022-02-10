package iss.nus.adproject_android_v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MealHelper implements Serializable {

    private String id;
    private String imageURL;
    private String title;
    private boolean visibility;
    private String trackScore;
    private String timeStamp;
    private String description;
    private Goal goal;
    private boolean flagged;
    private String feeling;
    private List<Object> likers;
    private List<Object> comments;


    public MealHelper(String id, String imageURL, String title, boolean visibility, String trackScore, String timeStamp, String description, Goal goal, boolean flagged, String feeling, List<Object> likers, List<Object> comments) {
        this.id = id;
        this.imageURL = imageURL;
        this.title = title;
        this.visibility = visibility;
        this.trackScore = trackScore;
        this.timeStamp = timeStamp;
        this.description = description;
        this.goal = goal;
        this.flagged = flagged;
        this.feeling = feeling;
        this.likers = likers;
        this.comments = comments;
    }

    public MealHelper() {

    }

    public Goal getGoal() {
        return goal;
    }

    public String getId() {
        return id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getTitle() {
        return title;
    }

    public String getTrackScore() {
        return trackScore;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getDescription() {
        return description;
    }

    public boolean isVisibility() {
        return visibility;
    }
}
