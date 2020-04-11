package com.arabdevelopers.shamelapp.models;

import java.io.Serializable;
import java.util.List;

public class AdsDataModel implements Serializable {

    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable
    {
        private int current_page;
        private int last_page;
        private List<AdsModel> data;

        public int getCurrent_page() {
            return current_page;
        }

        public int getLast_page() {
            return last_page;
        }

        public List<AdsModel> getData() {
            return data;
        }
    }
}
