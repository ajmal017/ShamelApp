package com.arabdevelopers.shamelapp.models;

import java.io.Serializable;

public class DepartmentModel implements Serializable {

    private int id;
    private String name;
    private String icon;
    private String background;
    private String image;
    private String type_department;
    private String type;


    public DepartmentModel() {
    }

    public DepartmentModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public String getIcon() {
        return icon;
    }
}
