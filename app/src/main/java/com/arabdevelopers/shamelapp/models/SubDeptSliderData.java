package com.arabdevelopers.shamelapp.models;

import java.io.Serializable;
import java.util.List;

public class SubDeptSliderData implements Serializable {

    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable{
        private List<SliderModel> slider;
        private List<DepartmentModel> sub_departments;

        public List<SliderModel> getSlider() {
            return slider;
        }

        public List<DepartmentModel> getSub_departments() {
            return sub_departments;
        }
    }
}
