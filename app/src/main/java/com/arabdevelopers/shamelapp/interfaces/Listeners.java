package com.arabdevelopers.shamelapp.interfaces;
public interface Listeners {
    interface BackListener
    {
        void back();
    }
    interface LoginListener{

        void skip();
        void validate();
    }

    interface SignUpListener{

        void openSheet();
        void closeSheet();
        void onMaleClicked();
        void onFemaleClicked();
        void checkDataValid();
        void checkReadPermission();
        void checkCameraPermission();
    }

    interface SettingActions
    {
        void openWhatsApp();
        void addAds();
        void terms();
        void privacyPolicy();
        void logout();
        void openTwitter();
        void openSnapChat();
        void openInstagram();

    }

    interface AddAdsListener{

        void openSheet();
        void closeSheet();
        void checkDataValid();
        void checkReadPermission();
        void checkCameraPermission();
    }


    interface AdDetailsActions
    {
        void like_dislike();
        void info();
        void map();
        void call();
        void call2();
        void whatApp();
        void facebook();
        void twitter();
        void instagram();

    }


}
