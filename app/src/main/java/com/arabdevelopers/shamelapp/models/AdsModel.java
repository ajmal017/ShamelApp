package com.arabdevelopers.shamelapp.models;

import java.io.Serializable;

public class AdsModel implements Serializable {

    private int id;
    private String name;
    private String image;
    private String department_id;
    private String sub_department_id;
    private String user_id;
    private String content;
    private String avg_rate;
    private String address;
    private String latitude;
    private String longitude;
    private String type;
    private String is_block;
    private DepartmentModel department;
    private DepartmentModel sub_department;
    private UserModel.User user;
    private User_Like user_like;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public String getSub_department_id() {
        return sub_department_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getContent() {
        return content;
    }

    public String getAvg_rate() {
        return avg_rate;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }

    public String getIs_block() {
        return is_block;
    }

    public DepartmentModel getDepartment() {
        return department;
    }

    public DepartmentModel getSub_department() {
        return sub_department;
    }

    public UserModel.User getUser() {
        return user;
    }

    public User_Like getUser_like() {
        return user_like;
    }

    public void setUser_like(User_Like user_like) {
        this.user_like = user_like;
    }

    public static class User_Like implements Serializable
    {
        private int id;
        private String user_id;
        private String advertisement_id;
        private String status;

        public int getId() {
            return id;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getAdvertisement_id() {
            return advertisement_id;
        }

        public String getStatus() {
            return status;
        }
    }

}
