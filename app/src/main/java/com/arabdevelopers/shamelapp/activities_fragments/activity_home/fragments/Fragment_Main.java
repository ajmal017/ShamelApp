package com.arabdevelopers.shamelapp.activities_fragments.activity_home.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_home.HomeActivity;
import com.arabdevelopers.shamelapp.activities_fragments.activity_sub_department.SubDepartmentActivity;
import com.arabdevelopers.shamelapp.adapters.MainDepartmentAdapter;
import com.arabdevelopers.shamelapp.adapters.SliderAdapter;
import com.arabdevelopers.shamelapp.databinding.FragmentMainBinding;
import com.arabdevelopers.shamelapp.models.DepartmentModel;
import com.arabdevelopers.shamelapp.models.MainDeptSliderData;
import com.arabdevelopers.shamelapp.models.SliderModel;
import com.arabdevelopers.shamelapp.preferences.Preferences;
import com.arabdevelopers.shamelapp.remote.Api;
import com.arabdevelopers.shamelapp.tags.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Main extends Fragment {
    private HomeActivity activity;
    private FragmentMainBinding binding;
    private Preferences preferences;
    private String lang;
    private SliderAdapter sliderAdapter;
    private List<SliderModel> sliderModelList;
    private TimerTask timerTask;
    private Timer timer;
    private List<DepartmentModel> departmentModelList;
    private MainDepartmentAdapter adapter;


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
        departmentModelList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        Paper.init(activity);
        lang = Paper.book().read("lang","ar");
        binding.tab.setupWithViewPager(binding.pager);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBarSlider.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        GridLayoutManager manager = new GridLayoutManager(activity,2);
        binding.recView.setLayoutManager(manager);
        adapter = new MainDepartmentAdapter(departmentModelList,activity,this);
        binding.recView.setAdapter(adapter);

        getData();

    }

    private void getData()
    {
        Api.getService(Tags.base_url)
                .getSliders_MainDepartments()
                .enqueue(new Callback<MainDeptSliderData>() {
                    @Override
                    public void onResponse(Call<MainDeptSliderData> call, Response<MainDeptSliderData> response) {
                        binding.progBar.setVisibility(View.GONE);
                        binding.progBarSlider.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (response.body()!=null&&response.body().getData()!=null)
                            {
                                updateSliderData(response.body().getData().getSlider());
                                updateDepartmentData(response.body().getData().getMain_departments());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MainDeptSliderData> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch (Exception e)
                        {
                            Log.e("Error",e.getMessage()+"__");
                        }
                    }
                });
    }

    private void updateDepartmentData(List<DepartmentModel> list) {
        departmentModelList.clear();
        departmentModelList.addAll(list);
        if (departmentModelList.size()>0)
        {
            adapter.notifyDataSetChanged();
            binding.tvNoData.setVisibility(View.GONE);
        }else
            {
                binding.tvNoData.setVisibility(View.VISIBLE);

            }

    }

    private void updateSliderData(List<SliderModel> list)
    {
        sliderModelList.clear();
        sliderModelList.addAll(list);

        if (sliderModelList.size()>0){
            binding.flSlider.setVisibility(View.VISIBLE);
            sliderAdapter = new SliderAdapter(sliderModelList,activity);
            binding.pager.setAdapter(sliderAdapter);
            startTimer();
        }else
            {
                binding.flSlider.setVisibility(View.GONE);

            }


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

    public void setItemData(DepartmentModel departmentModel) {
        Intent intent = new Intent(activity, SubDepartmentActivity.class);
        intent.putExtra("data",departmentModel);
        startActivity(intent);
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
