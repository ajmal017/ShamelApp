package com.arabdevelopers.shamelapp.models;

import java.io.Serializable;

public class DepartmentModel implements Serializable {

    private int id;
    private String name;
    private String background;
    private String image;
    private String type_department;
    private String type;

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
}
