package com.arabdevelopers.shamelapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppDataModel implements Serializable {
    private Setting settings;

    public Setting getSettings() {
        return settings;
    }

    public static class Setting implements Serializable
    {
        private String website;
        private String phones;
        private String emails;
        private String about;
        private String logo;
        private String facebook;
        private String twitter;
        private String instagram;
        private String linkedin;
        private String whatsapp;
        @SerializedName("snapchat-ghost")
        private String snapchat;
        private String termis_condition;


        public String getWebsite() {
            return website;
        }

        public String getPhones() {
            return phones;
        }

        public String getEmails() {
            return emails;
        }

        public String getAbout() {
            return about;
        }

        public String getLogo() {
            return logo;
        }

        public String getFacebook() {
            return facebook;
        }

        public String getTwitter() {
            return twitter;
        }

        public String getInstagram() {
            return instagram;
        }

        public String getLinkedin() {
            return linkedin;
        }

        public String getWhatsapp() {
            return whatsapp;
        }

        public String getSnapchat() {
            return snapchat;
        }

        public String getTermis_condition() {
            return termis_condition;
        }
    }
}
