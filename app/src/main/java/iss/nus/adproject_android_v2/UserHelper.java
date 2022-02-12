package iss.nus.adproject_android_v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserHelper implements Serializable {

    private Integer userId;
    private String username;
    private String name;
    private String profilePic;

    public UserHelper() { };

    public UserHelper(Integer userId, String username, String name, String profilePic) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.profilePic = profilePic;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
