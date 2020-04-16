package com.arabdevelopers.shamelapp.activities_fragments.activity_home;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_home.fragments.Fragment_Favourite;
import com.arabdevelopers.shamelapp.activities_fragments.activity_home.fragments.Fragment_Main;
import com.arabdevelopers.shamelapp.activities_fragments.activity_home.fragments.Fragment_More;
import com.arabdevelopers.shamelapp.activities_fragments.activity_home.fragments.Fragment_Profile;
import com.arabdevelopers.shamelapp.activities_fragments.activity_login.LoginActivity;
import com.arabdevelopers.shamelapp.activities_fragments.activity_notification.NotificationActivity;
import com.arabdevelopers.shamelapp.databinding.ActivityHomeBinding;
import com.arabdevelopers.shamelapp.databinding.DialogLanguageBinding;
import com.arabdevelopers.shamelapp.language.Language;
import com.arabdevelopers.shamelapp.models.NotificationCountModel;
import com.arabdevelopers.shamelapp.models.UserModel;
import com.arabdevelopers.shamelapp.preferences.Preferences;
import com.arabdevelopers.shamelapp.remote.Api;
import com.arabdevelopers.shamelapp.share.Common;
import com.arabdevelopers.shamelapp.tags.Tags;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private FragmentManager fragmentManager;
    private Fragment_Main fragment_main;
    private Fragment_Favourite fragment_favourite;
    private Fragment_More fragment_more;
    private Fragment_Profile fragment_profile;
    private Preferences preferences;
    private UserModel.User userModel;


    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));}




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();
        if (savedInstanceState == null) {

            displayFragmentMain();
        }


    }

    private void initView() {
        fragmentManager = getSupportFragmentManager();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        setUpBottomNavigation();
        binding.flLanguage.setOnClickListener(view -> createLangDialog());
        binding.flNotification.setOnClickListener(view -> {
            if (userModel!=null)
            {
                readNotificationCount();
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
            }else
                {
                    Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up));
                }

        });
        getNotificationCount();

    }

    private void setUpBottomNavigation() {

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getResources().getString(R.string.home), R.drawable.ic_home);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getResources().getString(R.string.favourite), R.drawable.ic_heart);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getResources().getString(R.string.profile), R.drawable.ic_user);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getResources().getString(R.string.more), R.drawable.ic_more);

        binding.ahBottomNav.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        binding.ahBottomNav.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        binding.ahBottomNav.setTitleTextSizeInSp(13, 13);
        binding.ahBottomNav.setForceTint(true);
        binding.ahBottomNav.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        binding.ahBottomNav.setInactiveColor(ContextCompat.getColor(this, R.color.white));

        binding.ahBottomNav.addItem(item1);
        binding.ahBottomNav.addItem(item2);
        binding.ahBottomNav.addItem(item3);
        binding.ahBottomNav.addItem(item4);

        updateBottomNavigationPosition(0);

        binding.ahBottomNav.setOnTabSelectedListener((position, wasSelected) -> {
            switch (position) {
                case 0:
                    displayFragmentMain();
                    break;
                case 1:

                    if (userModel!=null)
                    {
                        displayFragmentFavourite();

                    }else
                    {
                        Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up));
                    }


                    break;
                case 2:
                    if (userModel!=null)
                    {
                        displayFragmentProfile();

                    }else
                        {
                            Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up));
                        }


                    break;
                case 3:
                    displayFragmentMore();

                    break;

            }
            return false;
        });


    }

    private void updateBottomNavigationPosition(int pos) {

        binding.ahBottomNav.setCurrentItem(pos, false);

    }

    public void displayFragmentMain() {
        try {
            if (fragment_main == null) {
                fragment_main = Fragment_Main.newInstance();
            }

            if (fragment_favourite != null && fragment_favourite.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_favourite).commit();
            }
            if (fragment_more != null && fragment_more.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_more).commit();
            }
            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }
            if (fragment_main.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_main).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_main, "fragment_main").addToBackStack("fragment_main").commit();

            }
            binding.setTitle(getString(R.string.home));
            updateBottomNavigationPosition(0);
        } catch (Exception e) {
        }

    }

    private void displayFragmentFavourite() {
        try {
            if (fragment_favourite == null) {
                fragment_favourite = Fragment_Favourite.newInstance();
            }
            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_more != null && fragment_more.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_more).commit();
            }
            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }
            if (fragment_favourite.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_favourite).commit();
                fragment_favourite.getData();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_favourite, "fragment_favourite").addToBackStack("fragment_favourite").commit();

            }
            binding.setTitle(getString(R.string.favourite));
            updateBottomNavigationPosition(1);
        } catch (Exception e) {
        }

    }

    private void displayFragmentProfile() {
        try {
            if (fragment_profile == null) {
                fragment_profile = Fragment_Profile.newInstance();
            }
            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_favourite != null && fragment_favourite.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_favourite).commit();
            }

            if (fragment_more != null && fragment_more.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_more).commit();
            }
            if (fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_profile).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_profile, "fragment_profile").addToBackStack("fragment_profile").commit();

            }
            binding.setTitle(getString(R.string.profile));
            updateBottomNavigationPosition(2);
        } catch (Exception e) {
        }
    }

    private void displayFragmentMore() {
        try {
            if (fragment_more == null) {
                fragment_more = Fragment_More.newInstance();
            }
            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_favourite != null && fragment_favourite.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_favourite).commit();
            }

            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }
            if (fragment_more.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_more).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_more, "fragment_more").addToBackStack("fragment_more").commit();

            }
            binding.setTitle(getString(R.string.more));

            updateBottomNavigationPosition(3);
        } catch (Exception e) {
        }
    }


    public void refreshActivity(String lang) {
        Paper.book().write("lang", lang);
        Language.setNewLocale(this, lang);
        new Handler()
                .postDelayed(() -> {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }, 1050);


    }

    @Override
    public void onBackPressed() {
        if (fragment_main != null && fragment_main.isAdded() && fragment_main.isVisible()) {
            if (userModel==null)
            {
                navigateToSignInActivity();
            }else
                {
                    finish();
                }
        } else {
            displayFragmentMain();
        }
    }

    public void logout()
    {
        if (userModel!=null)
        {
            ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
            dialog.show();
            Api.getService(Tags.base_url)
                    .logout("Bearer "+userModel.getToken(),userModel.getId())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            dialog.dismiss();
                            if (response.isSuccessful())
                            {
                                preferences.clear(HomeActivity.this);
                                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                if (manager!=null)
                                {
                                    manager.cancel(Tags.not_tag,Tags.not_id);
                                }
                                navigateToSignInActivity();


                            }else
                            {
                                dialog.dismiss();
                                try {
                                    Log.e("error",response.code()+"__"+response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() ==500)
                                {
                                    Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                }else
                                    {
                                        Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            try {
                               dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage() + "__");

                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }catch (Exception e)
                            {
                                Log.e("Error",e.getMessage()+"__");
                            }
                        }
                    });
        }else
            {
                navigateToSignInActivity();
            }


    }

    private void getNotificationCount()
    {
        Api.getService(Tags.base_url)
                .getNotificationCount("Bearer "+userModel.getToken(),userModel.getId())
                .enqueue(new Callback<NotificationCountModel>() {
                    @Override
                    public void onResponse(Call<NotificationCountModel> call, Response<NotificationCountModel> response) {
                        if (response.isSuccessful())
                        {
                            binding.setNotCount(response.body().getCount());
                        }else
                        {
                            try {
                                Log.e("errorNotCode",response.code()+"__"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() ==500)
                            {
                                Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }else
                            {
                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationCountModel> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch (Exception e)
                        {
                            Log.e("Error",e.getMessage()+"__");
                        }
                    }
                });
    }

    private void readNotificationCount()
    {
        Api.getService(Tags.base_url)
                .readNotification("Bearer "+userModel.getToken(),userModel.getId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful())
                        {
                            binding.setNotCount(0);
                        }else
                        {
                            try {
                                Log.e("error",response.code()+"__"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() ==500)
                            {
                                Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }else
                            {
                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch (Exception e)
                        {
                            Log.e("Error",e.getMessage()+"__");
                        }
                    }
                });
    }


    private void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void createLangDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();

        DialogLanguageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_language, null, false);
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
                            refreshActivity("ar");
                        }
                    },200);
        });
        binding.rbEn.setOnClickListener(view -> {
            dialog.dismiss();
            new Handler()
                    .postDelayed(() -> {
                        if (!lang.equals("en"))
                        {
                            refreshActivity("en");
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
