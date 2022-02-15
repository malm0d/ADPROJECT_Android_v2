package iss.nus.adproject_android_v2.helper;

import java.io.Serializable;

public class Comment implements Serializable {
    //Maps to CommentHelper class in Spring Boot web app

    public Comment() {

    }

    private Integer commentId;
    private String caption;
    private Integer mealEntryId;
    private String authorUsername;
    private Integer authorId;

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Integer getMealEntryId() {
        return mealEntryId;
    }

    public void setMealEntryId(Integer mealEntryId) {
        this.mealEntryId = mealEntryId;
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
        return "Comment{" +
                "commentId=" + commentId +
                ", caption='" + caption + '\'' +
                ", mealEntryId=" + mealEntryId +
                ", authorUsername='" + authorUsername + '\'' +
                ", authorId=" + authorId +
                '}';
    }
}
