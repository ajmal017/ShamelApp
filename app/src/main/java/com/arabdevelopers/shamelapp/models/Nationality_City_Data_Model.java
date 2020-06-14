package com.arabdevelopers.shamelapp.models;

import java.io.Serializable;
import java.util.List;

public class Nationality_City_Data_Model implements Serializable {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable{
        private List<String> nationals;
        private List<String> cities;

        public List<String> getNationals() {
            return nationals;
        }

        public List<String> getCities() {
            return cities;
        }
    }


}
