package iss.nus.adproject_android_v2.helper;

import java.io.Serializable;
import java.time.LocalDateTime;


public class BlogEntry implements Serializable {
    private Integer id;
    private String imageURL;

    private boolean visibility;
    private String title;
    private String description;
    private LocalDateTime timeStamp;

    private boolean likedByActiveUser;
    private boolean flaggedByActiveUser;
    private int numberOfLikes;
    private String authorUsername;
    private Integer authorId;

    public BlogEntry() {


    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isLikedByActiveUser() {
        return likedByActiveUser;
    }

    public void setLikedByActiveUser(boolean likedByActiveUser) {
        this.likedByActiveUser = likedByActiveUser;
    }

    public boolean isFlaggedByActiveUser() {
        return flaggedByActiveUser;
    }

    public void setFlaggedByActiveUser(boolean flaggedByActiveUser) {
        this.flaggedByActiveUser = flaggedByActiveUser;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "BlogEntry{" +
                "id=" + id +
                ", imageURL='" + imageURL + '\'' +
                ", visibility=" + visibility +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", timeStamp=" + timeStamp +
                ", likedByActiveUser=" + likedByActiveUser +
                ", flaggedByActiveUser=" + flaggedByActiveUser +
                ", numberOfLikes=" + numberOfLikes +
                ", authorUsername='" + authorUsername + '\'' +
                ", authorId=" + authorId +
                '}';
    }
}