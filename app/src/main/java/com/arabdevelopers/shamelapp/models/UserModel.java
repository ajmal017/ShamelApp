package com.arabdevelopers.shamelapp.models;

import java.io.Serializable;

public class UserModel implements Serializable {

    private User data;

    public User getData() {
        return data;
    }

    public static class User implements Serializable
    {
        private String name;
        private String phone_code;
        private String phone;
        private String image;
        private String gender;
        private String type;
        private int id;
        private String token;
        private String facebook;
        private String instagram;
        private String whats_up;
        private String twitter;
        private String other_phone;



        public String getName() {
            return name;
        }

        public String getPhone_code() {
            return phone_code;
        }

        public String getPhone() {
            return phone;
        }

        public String getImage() {
            return image;
        }

        public String getGender() {
            return gender;
        }

        public String getType() {
            return type;
        }

        public int getId() {
            return id;
        }

        public String getToken() {
            return token;
        }

        public String getFacebook() {
            return facebook;
        }

        public String getInstagram() {
            return instagram;
        }

        public String getWhats_up() {
            return whats_up;
        }

        public String getTwitter() {
            return twitter;
        }

        public String getOther_phone() {
            return other_phone;
        }
    }

}
