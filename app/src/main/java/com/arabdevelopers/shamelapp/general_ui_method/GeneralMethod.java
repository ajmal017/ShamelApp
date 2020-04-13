package com.arabdevelopers.shamelapp.general_ui_method;

import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.arabdevelopers.shamelapp.R;
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
        String path = Tags.IMAGE_URL+imageEndPoint;
        if (view instanceof CircleImageView)
        {
            CircleImageView imageView = (CircleImageView) view;
            Picasso.with(view.getContext()).load(Uri.parse(path)).fit().into(imageView);
        }else if (view instanceof RoundedImageView)
        {
            RoundedImageView imageView = (RoundedImageView) view;
            Picasso.with(view.getContext()).load(Uri.parse(path)).fit().into(imageView);
        }else if (view instanceof ImageView)
        {

            ImageView imageView = (ImageView) view;
            Picasso.with(view.getContext()).load(Uri.parse(path)).fit().into(imageView);
        }
    }

    @BindingAdapter("imageProfile")
    public static void displayImageProfile(View view ,String imageEndPoint)
    {
        String path = Tags.IMAGE_URL+imageEndPoint;
        if (view instanceof CircleImageView)
        {
            CircleImageView imageView = (CircleImageView) view;
            Picasso.with(view.getContext()).load(Uri.parse(path)).placeholder(R.drawable.user_avatar).into(imageView);
        }else if (view instanceof RoundedImageView)
        {
            RoundedImageView imageView = (RoundedImageView) view;
            Picasso.with(view.getContext()).load(Uri.parse(path)).placeholder(R.drawable.user_avatar).into(imageView);
        }else if (view instanceof ImageView)
        {

            ImageView imageView = (ImageView) view;
            Picasso.with(view.getContext()).load(Uri.parse(path)).placeholder(R.drawable.user_avatar).into(imageView);
        }
    }








}
