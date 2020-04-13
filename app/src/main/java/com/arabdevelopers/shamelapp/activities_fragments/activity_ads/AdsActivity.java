package com.arabdevelopers.shamelapp.activities_fragments.activity_ads;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_ads_details.AdsDetailsActivity;
import com.arabdevelopers.shamelapp.adapters.AdsAdapter;
import com.arabdevelopers.shamelapp.databinding.ActivityAdsBinding;
import com.arabdevelopers.shamelapp.interfaces.Listeners;
import com.arabdevelopers.shamelapp.language.Language;
import com.arabdevelopers.shamelapp.models.AdsDataModel;
import com.arabdevelopers.shamelapp.models.AdsModel;
import com.arabdevelopers.shamelapp.models.DepartmentModel;
import com.arabdevelopers.shamelapp.models.LikeDislikeModel;
import com.arabdevelopers.shamelapp.models.UserModel;
import com.arabdevelopers.shamelapp.preferences.Preferences;
import com.arabdevelopers.shamelapp.remote.Api;
import com.arabdevelopers.shamelapp.tags.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdsActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityAdsBinding binding;
    private String lang;
    private DepartmentModel departmentModel;
    private List<AdsModel> adsModelList;
    private AdsAdapter adapter;
    private int current_page = 1;
    private boolean isLoading = false;
    private UserModel.User userModel;
    private Preferences preferences;
    private LinearLayoutManager manager;
    private int selected_pos = -1;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ads);
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
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        adsModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setTitle(departmentModel.getName());
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        manager = new LinearLayoutManager(this);
        binding.recView.setLayoutManager(manager);
        adapter = new AdsAdapter(this,adsModelList);
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


    private void getData()
    {
        String user_id = null;

        if (userModel!=null)
        {
            user_id = String.valueOf(userModel.getId());
        }

        Api.getService(Tags.base_url)
                .getAds("on",20,1,departmentModel.getId(),user_id)
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
                                    Toast.makeText(AdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
        String user_id = null;

        if (userModel!=null)
        {
            user_id = String.valueOf(userModel.getId());
        }

        Api.getService(Tags.base_url)
                .getAds("on",20,page,departmentModel.getId(),user_id)
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
                                    Toast.makeText(AdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch (Exception e)
                        {
                            Log.e("Error",e.getMessage()+"__");
                        }
                    }
                });
    }


    public void setItemData(AdsModel model, int adapterPosition)
    {
        this.selected_pos = adapterPosition;
        Intent intent = new Intent(this, AdsDetailsActivity.class);
        intent.putExtra("data",model);
        intent.putExtra("title",departmentModel.getName());
        startActivityForResult(intent,100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode==RESULT_OK&&data!=null)
        {
            int action = data.getIntExtra("action",0);
            if (selected_pos!=-1)
            {

                if (action==0)
                {
                    //dislike

                    AdsModel adsModel = adsModelList.get(selected_pos);
                    adsModel.setUser_like(null);
                    adsModelList.set(selected_pos,adsModel);

                }else
                    {
                        //like

                        AdsModel adsModel = adsModelList.get(selected_pos);
                        AdsModel.User_Like user_like = new AdsModel.User_Like();
                        adsModel.setUser_like(user_like);
                        adsModelList.set(selected_pos,adsModel);

                    }


                adapter.notifyItemChanged(selected_pos);
                selected_pos = -1;
            }
        }
    }


    public void likeDislike(AdsModel model, int adapterPosition, int action)
    {
        if (userModel!=null)
        {
            Api.getService(Tags.base_url)
                    .likeDislike("Bearer"+userModel.getToken(),userModel.getId(),model.getId())
                    .enqueue(new Callback<LikeDislikeModel>() {
                        @Override
                        public void onResponse(Call<LikeDislikeModel> call, Response<LikeDislikeModel> response) {
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().getStatus()==0)
                                {
                                    model.setUser_like(null);

                                }else
                                    {
                                        model.setUser_like(new AdsModel.User_Like());

                                    }

                                adsModelList.set(adapterPosition,model);
                                adapter.notifyItemChanged(adapterPosition);

                            }else
                                {
                                    if (action==0)
                                    {
                                        model.setUser_like(new AdsModel.User_Like());
                                        adapter.notifyItemChanged(adapterPosition);

                                    }else
                                        {
                                            model.setUser_like(null);
                                            adapter.notifyItemChanged(adapterPosition);
                                        }

                                }
                        }

                        @Override
                        public void onFailure(Call<LikeDislikeModel> call, Throwable t) {
                            try {
                                if (action==0)
                                {
                                    model.setUser_like(new AdsModel.User_Like());
                                    adapter.notifyItemChanged(adapterPosition);

                                }else
                                {
                                    model.setUser_like(null);
                                    adapter.notifyItemChanged(adapterPosition);
                                }
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage() + "__");

                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(AdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                if (action==0)
                {
                    model.setUser_like(new AdsModel.User_Like());
                    adapter.notifyItemChanged(adapterPosition);

                }else
                {
                    model.setUser_like(null);
                    adapter.notifyItemChanged(adapterPosition);
                }
            }


    }

    @Override
    public void back() {
        finish();
    }



}
