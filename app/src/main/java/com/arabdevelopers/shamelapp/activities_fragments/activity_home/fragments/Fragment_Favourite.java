package com.arabdevelopers.shamelapp.activities_fragments.activity_home.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_ads_details.AdsDetailsActivity;
import com.arabdevelopers.shamelapp.activities_fragments.activity_home.HomeActivity;
import com.arabdevelopers.shamelapp.adapters.FavouriteAdapter;
import com.arabdevelopers.shamelapp.databinding.FragmentFavouriteBinding;
import com.arabdevelopers.shamelapp.models.AdsDataModel;
import com.arabdevelopers.shamelapp.models.AdsModel;
import com.arabdevelopers.shamelapp.models.LikeDislikeModel;
import com.arabdevelopers.shamelapp.models.UserModel;
import com.arabdevelopers.shamelapp.preferences.Preferences;
import com.arabdevelopers.shamelapp.remote.Api;
import com.arabdevelopers.shamelapp.share.Common;
import com.arabdevelopers.shamelapp.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Favourite extends Fragment {

    private HomeActivity activity;
    private FragmentFavouriteBinding binding;
    private Preferences preferences;
    private UserModel.User userModel;
    private String lang;
    private List<AdsModel> adsModelList;
    private FavouriteAdapter adapter;
    private int current_page = 1;
    private boolean isLoading = false;
    private LinearLayoutManager manager;
    private int selected_pos = -1;


    public static Fragment_Favourite newInstance() {
        return new Fragment_Favourite();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

    }

    private void initView() {
        adsModelList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        manager = new LinearLayoutManager(activity);
        binding.recView.setLayoutManager(manager);
        adapter = new FavouriteAdapter(activity,adsModelList,this);
        binding.recView.setAdapter(adapter);

        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastItemPos = manager.findLastCompletelyVisibleItemPosition();
                int total_item = adapter.getItemCount();
                if (dy>0&&lastItemPos==(total_item-1)&&total_item>=20&&!isLoading)
                {
                    isLoading = true;
                    adsModelList.add(null);
                    adapter.notifyItemInserted(adsModelList.size()-1);
                    int page = current_page+1;
                    loadMore(page);
                }
            }
        });
        getData();
    }


    public void getData()
    {


        Api.getService(Tags.base_url)
                .getFavourite("on",20,1,userModel.getId())
                .enqueue(new Callback<AdsDataModel>() {
                    @Override
                    public void onResponse(Call<AdsDataModel> call, Response<AdsDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful())
                        {
                            if (response.body()!=null&&response.body().getData()!=null)
                            {
                                adsModelList.clear();
                                adsModelList.addAll(response.body().getData().getData());
                                if (adsModelList.size()>0)
                                {
                                    adapter.notifyDataSetChanged();
                                    binding.tvNoData.setVisibility(View.GONE);
                                }else
                                {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AdsDataModel> call, Throwable t) {
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

    private void loadMore(int page)
    {


        Api.getService(Tags.base_url)
                .getFavourite("on",20,page,userModel.getId())
                .enqueue(new Callback<AdsDataModel>() {
                    @Override
                    public void onResponse(Call<AdsDataModel> call, Response<AdsDataModel> response) {
                        isLoading = false;
                        adsModelList.remove(adsModelList.size()-1);
                        adapter.notifyItemRemoved(adsModelList.size()-1);

                        if (response.isSuccessful())
                        {
                            if (response.body()!=null&&response.body().getData()!=null)
                            {
                                if (response.body().getData().getData().size()>0)
                                {
                                    int oldPos = adsModelList.size()-1;
                                    adsModelList.addAll(response.body().getData().getData());
                                    adapter.notifyItemRangeChanged(oldPos,adsModelList.size());
                                    current_page = response.body().getData().getCurrent_page();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AdsDataModel> call, Throwable t) {
                        try {
                            isLoading = false;
                            if (adsModelList.get(adsModelList.size()-1)==null)
                            {
                                adsModelList.remove(adsModelList.size()-1);
                                adapter.notifyItemRemoved(adsModelList.size()-1);
                            }


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
    public void setDislikeItemData(AdsModel model, int adapterPosition)
    {

        ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.show();

        Api.getService(Tags.base_url)
                .likeDislike("Bearer "+userModel.getToken(),userModel.getId(),model.getId())
                .enqueue(new Callback<LikeDislikeModel>() {
                    @Override
                    public void onResponse(Call<LikeDislikeModel> call, Response<LikeDislikeModel> response) {
                        if (response.isSuccessful())
                        {

                            adsModelList.remove(adapterPosition);
                            adapter.notifyItemRemoved(adapterPosition);

                            if (adsModelList.size()>0)
                            {
                                binding.tvNoData.setVisibility(View.GONE);
                            }else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }

                        }else
                        {
                            dialog.dismiss();
                            try {
                                Log.e("error",response.code()+"_"+response.errorBody().string());

                                if (response.code()==500)
                                {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();
                                }else
                                    {
                                        Toast.makeText(activity,getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<LikeDislikeModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
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

    public void setItemData(AdsModel model, int adapterPosition) {
        this.selected_pos = adapterPosition;
        Intent intent = new Intent(activity, AdsDetailsActivity.class);
        intent.putExtra("data",model);
        intent.putExtra("title",model.getSub_department().getName());
        startActivityForResult(intent,100);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode== Activity.RESULT_OK &&data!=null)
        {
            int action = data.getIntExtra("action",0);
            if (selected_pos!=-1)
            {

                if (action==0)
                {
                    adsModelList.remove(selected_pos);
                    adapter.notifyItemRemoved(selected_pos);

                    if (adsModelList.size()>0)
                    {
                        binding.tvNoData.setVisibility(View.GONE);
                    }else {
                        binding.tvNoData.setVisibility(View.VISIBLE);

                    }
                    selected_pos = -1;

                }



            }
        }
    }
}
