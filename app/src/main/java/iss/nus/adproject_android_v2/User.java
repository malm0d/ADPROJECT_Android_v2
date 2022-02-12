package iss.nus.adproject_android_v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    private Integer id;
    private String name;
    private String gender;
    private String dateOfBirth;
    private String height;
    private String weight;




    public User(Integer id, String name, String gender, String dateOfBirth,
                String height, String weight) {
        super();
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.weight = weight;

    }

    public User(){

    }
    public Integer getid() {
        return id;
    }


    public String getName() {
        return name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getGender() {
        return gender;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setHeight(String height) {
        this.height = height;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }

}
