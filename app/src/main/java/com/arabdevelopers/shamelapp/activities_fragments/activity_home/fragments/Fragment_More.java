package com.arabdevelopers.shamelapp.activities_fragments.activity_home.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.arabdevelopers.shamelapp.BuildConfig;
import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_about_app.AboutAppActivity;
import com.arabdevelopers.shamelapp.activities_fragments.activity_add_ads.AddAdsActivity;
import com.arabdevelopers.shamelapp.activities_fragments.activity_home.HomeActivity;
import com.arabdevelopers.shamelapp.databinding.FragmentMoreBinding;
import com.arabdevelopers.shamelapp.interfaces.Listeners;
import com.arabdevelopers.shamelapp.models.AppDataModel;
import com.arabdevelopers.shamelapp.models.UserModel;
import com.arabdevelopers.shamelapp.preferences.Preferences;
import com.arabdevelopers.shamelapp.remote.Api;
import com.arabdevelopers.shamelapp.share.Common;
import com.arabdevelopers.shamelapp.tags.Tags;

import java.io.IOException;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_More extends Fragment implements Listeners.SettingActions {

    private HomeActivity activity;
    private FragmentMoreBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel.User userModel;
    private AppDataModel appDataModel;

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
        binding.setLang(lang);
        binding.setAction(this);
        binding.tvVersion.setText(String.format("%s%s","Version : ",BuildConfig.VERSION_NAME) );
        getAppData();
    }


    private void getAppData()
    {
        ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .getSettings(lang)
                .enqueue(new Callback<AppDataModel>() {
                    @Override
                    public void onResponse(Call<AppDataModel> call, Response<AppDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {

                           appDataModel = response.body();

                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AppDataModel> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });

    }

    @Override
    public void openWhatsApp() {
        if (appDataModel!=null)
        {
            String patterns = "(\\+|00)\\d{5,}";

            if (appDataModel.getSettings().getWhatsapp().matches(patterns))
            {
                String url = "https://api.whatsapp.com/send?phone="+appDataModel.getSettings().getWhatsapp();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }else
                {
                    Toast.makeText(activity, R.string.inv_whatsapp, Toast.LENGTH_SHORT).show();
                }

        }else
            {
                Common.CreateDialogAlert(activity,getString(R.string.whats_not_av));
            }
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
        if (appDataModel!=null)
        {
            String pattern = "https?://.+\\..{2,}";
            if (appDataModel.getSettings().getTwitter().matches(pattern))
            {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(appDataModel.getSettings().getTwitter()));
                startActivity(intent);
            }else
                {
                    Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();
                }

        }else {
            Common.CreateDialogAlert(activity,getString(R.string.whats_not_av));

        }

    }

    @Override
    public void openSnapChat() {


        String pattern = "https?://.+\\..{2,}";
        if (appDataModel.getSettings().getSnapchat().matches(pattern))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(appDataModel.getSettings().getSnapchat()));
            startActivity(intent);
        }else
        {
            Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void openInstagram() {
        String pattern = "https?://.+\\..{2,}";
        if (appDataModel.getSettings().getInstagram().matches(pattern))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(appDataModel.getSettings().getInstagram()));
            startActivity(intent);
        }else
        {
            Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();
        }
    }



}
