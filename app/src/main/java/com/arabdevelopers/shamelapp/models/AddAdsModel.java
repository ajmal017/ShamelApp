package com.arabdevelopers.shamelapp.models;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.arabdevelopers.shamelapp.R;

public class AddAdsModel extends BaseObservable {

    private Uri image;
    private String name;
    private String details;
    private int main_dept_id;
    private int sub_dept_id;
    private String address;
    private String nationality;
    private String city;
    private double lat;
    private double lng;

    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();
    public ObservableField<String> error_address = new ObservableField<>();


    public boolean isDataValid(Context context)
    {
        Log.e("image",image.toString());
        Log.e("name",name);
        Log.e("details",details);
        Log.e("main_dept_id",main_dept_id+"_");
        Log.e("sub_dept_id",sub_dept_id+"_");
        Log.e("address",address);
        Log.e("nationality",nationality);
        Log.e("city",city);
        Log.e("lat",lat+"-");
        Log.e("lng",lng+"_");


        if (image!=null&&
                !name.isEmpty()&&
                !details.isEmpty()&&
                main_dept_id!=0&&
                sub_dept_id!=0&&
                !address.isEmpty()&&
                !nationality.isEmpty()&&
                !city.isEmpty()&&
                lat!=0.0&&
                lng!=0.0
        ){
            error_name.set(null);
            error_details.set(null);
            error_address.set(null);

            return true;
        }else {

            if (image==null)
            {
                Toast.makeText(context, R.string.ch_ad_img, Toast.LENGTH_SHORT).show();
            }

            if (name.isEmpty())
            {
                error_name.set(context.getString(R.string.field_required));
            }else {
                error_name.set(null);
            }

            if (main_dept_id==0)
            {
                Toast.makeText(context, R.string.ch_main_dept, Toast.LENGTH_SHORT).show();
            }

            if (sub_dept_id==0)
            {
                Toast.makeText(context, R.string.ch_sub_dpt, Toast.LENGTH_SHORT).show();
            }


            if (nationality.isEmpty())
            {
                Toast.makeText(context, R.string.ch_nat, Toast.LENGTH_SHORT).show();
            }

            if (city.isEmpty())
            {
                Toast.makeText(context, R.string.ch_city, Toast.LENGTH_SHORT).show();
            }

            if (details.isEmpty())
            {
                error_details.set(context.getString(R.string.field_required));
            }else {
                error_details.set(null);
            }

            if (address.isEmpty())
            {
                error_address.set(context.getString(R.string.field_required));
            }else {
                error_address.set(null);
            }



            return false;
        }
    }

    public AddAdsModel() {
        setImage(null);
        setName("");
        setDetails("");
        setAddress("");
        setCity("");
        setNationality("");
        setLat(0.0);
        setLng(0.0);
        setMain_dept_id(0);
        setSub_dept_id(0);
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
        notifyPropertyChanged(BR.details);

    }

    public int getMain_dept_id() {
        return main_dept_id;
    }

    public void setMain_dept_id(int main_dept_id) {
        this.main_dept_id = main_dept_id;
    }

    public int getSub_dept_id() {
        return sub_dept_id;
    }

    public void setSub_dept_id(int sub_dept_id) {
        this.sub_dept_id = sub_dept_id;
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);

    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
