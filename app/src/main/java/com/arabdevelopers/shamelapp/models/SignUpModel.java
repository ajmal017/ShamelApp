package com.arabdevelopers.shamelapp.models;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.arabdevelopers.shamelapp.BR;
import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.tags.Tags;

public class SignUpModel extends BaseObservable {
    private String name;
    private String phone_code;
    private String phone;
    private String gender;
    public ObservableField<String> error_name = new ObservableField<>();


    public boolean isDataValid(Context context)
    {
        if (!name.isEmpty()){
            error_name.set(null);
            return true;
        }else
            {
                error_name.set(context.getString(R.string.field_required));
                return false;
            }
    }
    public SignUpModel() {
        gender = Tags.male;
        setName("");

    }



    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);

    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
