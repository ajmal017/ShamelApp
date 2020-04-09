package com.arabdevelopers.shamelapp.activities_fragments.activity_home.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_home.HomeActivity;
import com.arabdevelopers.shamelapp.adapters.SliderAdapter;
import com.arabdevelopers.shamelapp.databinding.FragmentMainBinding;
import com.arabdevelopers.shamelapp.models.SliderModel;
import com.arabdevelopers.shamelapp.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class Fragment_Main extends Fragment {
    private HomeActivity activity;
    private FragmentMainBinding binding;
    private Preferences preferences;
    private String lang;
    private SliderAdapter sliderAdapter;
    private List<SliderModel> sliderModelList;
    private TimerTask timerTask;
    private Timer timer;


    public static Fragment_Main newInstance() {
        return new Fragment_Main();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        sliderModelList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        Paper.init(activity);
        lang = Paper.book().read("lang","ar");
        binding.tab.setupWithViewPager(binding.pager);
        sliderAdapter = new SliderAdapter(sliderModelList,activity);
        binding.pager.setAdapter(sliderAdapter);


    }

    private void updateSliderData()
    {

    }

    private void startTimer()
    {
        if (sliderModelList.size()>1)
        {
            timer = new Timer();
            timerTask = new MyTimerTask();
            timer.scheduleAtFixedRate(timerTask,6000,6000);
        }


    }

    private void stopTimer(){


        if (timer!=null&&timerTask!=null)
        {
            timerTask.cancel();
            timer.purge();
            timer.cancel();;
        }

    }


    private class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            activity.runOnUiThread(()->{
                if (binding.pager.getCurrentItem()<binding.pager.getAdapter().getCount()-1)
                {
                    binding.pager.setCurrentItem(binding.pager.getCurrentItem()+1,true);

                }else
                {
                    binding.pager.setCurrentItem(0,true);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();
    }
}
