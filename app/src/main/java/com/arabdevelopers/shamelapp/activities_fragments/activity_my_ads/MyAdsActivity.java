package com.arabdevelopers.shamelapp.activities_fragments.activity_my_ads;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_notification.NotificationActivity;
import com.arabdevelopers.shamelapp.adapters.MyAdsAdapter;
import com.arabdevelopers.shamelapp.adapters.NotificationAdapter;
import com.arabdevelopers.shamelapp.databinding.ActivityMyAdsBinding;
import com.arabdevelopers.shamelapp.databinding.ActivityNotificationBinding;
import com.arabdevelopers.shamelapp.interfaces.Listeners;
import com.arabdevelopers.shamelapp.language.Language;
import com.arabdevelopers.shamelapp.models.AdsDataModel;
import com.arabdevelopers.shamelapp.models.AdsModel;
import com.arabdevelopers.shamelapp.models.NotFireModel;
import com.arabdevelopers.shamelapp.models.NotificationDataModel;
import com.arabdevelopers.shamelapp.models.UserModel;
import com.arabdevelopers.shamelapp.preferences.Preferences;
import com.arabdevelopers.shamelapp.remote.Api;
import com.arabdevelopers.shamelapp.share.Common;
import com.arabdevelopers.shamelapp.tags.Tags;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAdsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityMyAdsBinding binding;
    private String lang;
    private List<AdsModel> adsModelList;
    private MyAdsAdapter adapter;
    private int current_page = 1;
    private boolean isLoading = false;
    private UserModel.User userModel;
    private Preferences preferences;
    private LinearLayoutManager manager;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_ads);
        initView();

    }


    private void initView() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager!=null)
        {
            notificationManager.cancel(Tags.not_tag,Tags.not_id);
        }
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        adsModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        manager = new LinearLayoutManager(this);
        binding.recView.setLayoutManager(manager);
        adapter = new MyAdsAdapter(this, adsModelList);
        binding.recView.setAdapter(adapter);

        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastItemPos = manager.findLastCompletelyVisibleItemPosition();
                int total_item = adapter.getItemCount();
                if (dy > 0 && lastItemPos == (total_item - 1) && total_item >= 20 && !isLoading) {
                    isLoading = true;
                    adsModelList.add(null);
                    adapter.notifyItemInserted(adsModelList.size() - 1);
                    int page = current_page + 1;
                    loadMore(page);
                }
            }
        });
        getData();
    }


    private void getData() {
        Api.getService(Tags.base_url)
                .getMyAds( "on", 20, 1, userModel.getId())
                .enqueue(new Callback<AdsDataModel>() {
                    @Override
                    public void onResponse(Call<AdsDataModel> call, Response<AdsDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getData() != null) {
                                adsModelList.clear();
                                adsModelList.addAll(response.body().getData().getData());
                                if (adsModelList.size() > 0) {
                                    adapter.notifyDataSetChanged();
                                    binding.tvNoData.setVisibility(View.GONE);
                                } else {
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
                                    Toast.makeText(MyAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void loadMore(int page) {

        Api.getService(Tags.base_url).getMyAds("on", 20, page, userModel.getId())
        .enqueue(new Callback<AdsDataModel>() {
            @Override
            public void onResponse(Call<AdsDataModel> call, Response<AdsDataModel> response) {
                isLoading = false;
                adsModelList.remove(adsModelList.size() - 1);
                adapter.notifyItemRemoved(adsModelList.size() - 1);

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getData() != null) {
                        if (response.body().getData().getData().size() > 0) {
                            int oldPos = adsModelList.size() - 1;
                            adsModelList.addAll(response.body().getData().getData());
                            adapter.notifyItemRangeChanged(oldPos, adsModelList.size());
                            current_page = response.body().getData().getCurrent_page();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AdsDataModel> call, Throwable t) {
                try {
                    isLoading = false;
                    if (adsModelList.get(adsModelList.size() - 1) == null) {
                        adsModelList.remove(adsModelList.size() - 1);
                        adapter.notifyItemRemoved(adsModelList.size() - 1);
                    }


                    if (t.getMessage() != null) {
                        Log.e("error", t.getMessage() + "__");

                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                            Toast.makeText(MyAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                        } else {
                            if (!t.getMessage().toLowerCase().contains("socket close")){
                                Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Error", e.getMessage() + "__");
                }
            }
        });
    }



    @Override
    public void back() {
        finish();
    }


    public void setDeleteItemData(AdsModel model, int adapterPosition) {

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .deleteAds("Bearer " + userModel.getToken(), model.getId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            adsModelList.remove(adapterPosition);
                            adapter.notifyItemRemoved(adapterPosition);

                            if (adsModelList.size() > 0) {
                                binding.tvNoData.setVisibility(View.GONE);
                            } else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }
                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(MyAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(MyAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }
}
