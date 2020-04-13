package com.arabdevelopers.shamelapp.models;

import java.io.Serializable;
import java.util.List;

public class MainDeptSubDeptDataModel implements Serializable {

    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public static class Data implements Serializable
    {
        private int id;
        private String name;
        private String icon;
        private String background;
        private String image;
        private String type_department;
        private String type;

        public Data() {
        }

        public Data(int id, String name) {
            this.id = id;
            this.name = name;
        }

        private List<DepartmentModel> sub_departments;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getIcon() {
            return icon;
        }

        public String getBackground() {
            return background;
        }

        public String getImage() {
            return image;
        }

        public String getType_department() {
            return type_department;
        }

        public String getType() {
            return type;
        }

        public List<DepartmentModel> getSub_departments() {
            return sub_departments;
        }
    }
}
