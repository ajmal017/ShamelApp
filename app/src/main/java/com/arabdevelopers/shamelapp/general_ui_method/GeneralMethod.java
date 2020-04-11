package com.arabdevelopers.shamelapp.general_ui_method;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.arabdevelopers.shamelapp.tags.Tags;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class GeneralMethod {

    @BindingAdapter("error")
    public static void errorValidation(View view, String error) {
        if (view instanceof EditText) {
            EditText ed = (EditText) view;
            ed.setError(error);
        } else if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setError(error);


        }
    }

    @BindingAdapter("imageEndPoint")
    public static void displayImage(View view ,String imageEndPoint)
    {
        if (view instanceof CircleImageView)
        {
            CircleImageView imageView = (CircleImageView) view;
            Picasso.get().load(Tags.base_url+imageEndPoint).fit().into(imageView);
        }else if (view instanceof RoundedImageView)
        {
            RoundedImageView imageView = (RoundedImageView) view;
            Picasso.get().load(Tags.base_url+imageEndPoint).fit().into(imageView);
        }else if (view instanceof ImageView)
        {
            ImageView imageView = (ImageView) view;
            Picasso.get().load(Tags.base_url+imageEndPoint).fit().into(imageView);
        }
    }








}
