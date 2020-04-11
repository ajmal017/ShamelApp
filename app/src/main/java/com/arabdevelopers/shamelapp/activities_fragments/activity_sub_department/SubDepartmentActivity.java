package com.arabdevelopers.shamelapp.activities_fragments.activity_sub_department;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_ads.AdsActivity;
import com.arabdevelopers.shamelapp.adapters.SliderAdapter;
import com.arabdevelopers.shamelapp.adapters.SubDepartmentAdapter;
import com.arabdevelopers.shamelapp.databinding.ActivitySubDepartmentBinding;
import com.arabdevelopers.shamelapp.interfaces.Listeners;
import com.arabdevelopers.shamelapp.language.Language;
import com.arabdevelopers.shamelapp.models.DepartmentModel;
import com.arabdevelopers.shamelapp.models.SliderModel;
import com.arabdevelopers.shamelapp.models.SubDeptSliderData;
import com.arabdevelopers.shamelapp.remote.Api;
import com.arabdevelopers.shamelapp.tags.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubDepartmentActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivitySubDepartmentBinding binding;
    private String lang;
    private SliderAdapter sliderAdapter;
    private DepartmentModel departmentModel;
    private List<SliderModel> sliderModelList;
    private TimerTask timerTask;
    private Timer timer;
    private List<DepartmentModel> departmentModelList;
    private SubDepartmentAdapter adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sub_department);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null){

            departmentModel = (DepartmentModel) intent.getSerializableExtra("data");
        }
    }


    private void initView()
    {
        sliderModelList = new ArrayList<>();
        departmentModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setTitle(departmentModel.getName());
        binding.setLang(lang);
        binding.tab.setupWithViewPager(binding.pager);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBarSlider.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        GridLayoutManager manager = new GridLayoutManager(this,2);
        binding.recView.setLayoutManager(manager);
        adapter = new SubDepartmentAdapter(departmentModelList,this);
        binding.recView.setAdapter(adapter);

        getData();
    }


    private void getData()
    {
        Api.getService(Tags.base_url)
                .getSliders_SubDepartments(departmentModel.getId())
                .enqueue(new Callback<SubDeptSliderData>() {
                    @Override
                    public void onResponse(Call<SubDeptSliderData> call, Response<SubDeptSliderData> response) {
                        binding.progBar.setVisibility(View.GONE);
                        binding.progBarSlider.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (response.body()!=null&&response.body().getData()!=null)
                            {
                                updateSliderData(response.body().getData().getSlider());
                                updateSubDepartmentData(response.body().getData().getSub_departments());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SubDeptSliderData> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(SubDepartmentActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SubDepartmentActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch (Exception e)
                        {
                            Log.e("Error",e.getMessage()+"__");
                        }
                    }
                });
    }

    private void updateSubDepartmentData(List<DepartmentModel> list) {
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
        if (list!=null)
        {
            sliderModelList.clear();
            sliderModelList.addAll(list);

            if (sliderModelList.size()>0){
                binding.flSlider.setVisibility(View.VISIBLE);
                sliderAdapter = new SliderAdapter(sliderModelList,SubDepartmentActivity.this);
                binding.pager.setAdapter(sliderAdapter);
                startTimer();
            }else
            {
                binding.flSlider.setVisibility(View.GONE);

            }
        }else {
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

    public void setItemData(DepartmentModel subDepartmentModel) {
        Intent intent = new Intent(this, AdsActivity.class);
        intent.putExtra("data",subDepartmentModel);
        startActivity(intent);
    }


    private class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            runOnUiThread(()->{
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
    public void back() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
