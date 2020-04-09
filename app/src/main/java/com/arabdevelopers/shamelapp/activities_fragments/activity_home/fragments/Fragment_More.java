package com.arabdevelopers.shamelapp.activities_fragments.activity_home.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.arabdevelopers.shamelapp.BuildConfig;
import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_about_app.AboutAppActivity;
import com.arabdevelopers.shamelapp.activities_fragments.activity_add_ads.AddAdsActivity;
import com.arabdevelopers.shamelapp.activities_fragments.activity_home.HomeActivity;
import com.arabdevelopers.shamelapp.databinding.DialogLanguageBinding;
import com.arabdevelopers.shamelapp.databinding.FragmentMoreBinding;
import com.arabdevelopers.shamelapp.interfaces.Listeners;
import com.arabdevelopers.shamelapp.models.UserModel;
import com.arabdevelopers.shamelapp.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_More extends Fragment implements Listeners.SettingActions {

    private HomeActivity activity;
    private FragmentMoreBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;

    public static Fragment_More newInstance() {

        return new Fragment_More();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setAction(this);
        binding.tvVersion.setText(String.format("%s%s","Version : ",BuildConfig.VERSION_NAME) );
    }


    @Override
    public void openWhatsApp() {

    }

    @Override
    public void changeLanguage() {
        CreateLangDialog();
    }

    @Override
    public void contactUs() {

    }

    @Override
    public void addAds() {
        Intent intent = new Intent(activity, AddAdsActivity.class);
        startActivity(intent);
    }

    @Override
    public void terms() {
        Intent intent = new Intent(activity, AboutAppActivity.class);
        intent.putExtra("type",1);
        startActivity(intent);
    }

    @Override
    public void privacyPolicy() {

        Intent intent = new Intent(activity, AboutAppActivity.class);
        intent.putExtra("type",2);
        startActivity(intent);
    }

    @Override
    public void logout() {
        activity.logout();
    }

    @Override
    public void openTwitter() {

    }

    @Override
    public void openSnapChat() {

    }

    @Override
    public void openInstagram() {

    }


    private void CreateLangDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .create();

        DialogLanguageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_language, null, false);
        String lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        if (lang.equals("ar")) {
            binding.rbAr.setChecked(true);
        } else {
            binding.rbEn.setChecked(true);

        }
        binding.btnCancel.setOnClickListener((v) ->
                dialog.dismiss()

        );
        binding.rbAr.setOnClickListener(view -> {
            dialog.dismiss();
            new Handler()
                    .postDelayed(() -> {
                        if (!lang.equals("ar"))
                        {
                            activity.refreshActivity("ar");
                        }
                    },200);
        });
        binding.rbEn.setOnClickListener(view -> {
            dialog.dismiss();
            new Handler()
                    .postDelayed(() -> {
                        if (!lang.equals("en"))
                        {
                            activity.refreshActivity("en");
                        }
                    },200);
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

}
