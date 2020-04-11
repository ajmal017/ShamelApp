package com.arabdevelopers.shamelapp.models;

import java.io.Serializable;

public class SliderModel implements Serializable {
    private int id;
    private String image;
    private String department_id;
    private String slider_type;


    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public String getSlider_type() {
        return slider_type;
    }
}
