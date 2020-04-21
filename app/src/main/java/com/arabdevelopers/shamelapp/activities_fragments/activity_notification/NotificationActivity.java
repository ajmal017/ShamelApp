package com.arabdevelopers.shamelapp.activities_fragments.activity_notification;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.adapters.NotificationAdapter;
import com.arabdevelopers.shamelapp.databinding.ActivityNotificationBinding;
import com.arabdevelopers.shamelapp.interfaces.Listeners;
import com.arabdevelopers.shamelapp.language.Language;
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

public class NotificationActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityNotificationBinding binding;
    private String lang;
    private List<NotificationDataModel.Data.NotificationModel> notificationModelList;
    private NotificationAdapter adapter;
    private int current_page = 1;
    private boolean isLoading = false;
    private UserModel.User userModel;
    private Preferences preferences;
    private LinearLayoutManager manager;
    private Call<NotificationDataModel> callLoadMore;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
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
        notificationModelList = new ArrayList<>();
        EventBus.getDefault().register(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        manager = new LinearLayoutManager(this);
        binding.recView.setLayoutManager(manager);
        adapter = new NotificationAdapter(this, notificationModelList);
        binding.recView.setAdapter(adapter);

        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastItemPos = manager.findLastCompletelyVisibleItemPosition();
                int total_item = adapter.getItemCount();
                if (dy > 0 && lastItemPos == (total_item - 1) && total_item >= 20 && !isLoading) {
                    isLoading = true;
                    notificationModelList.add(null);
                    adapter.notifyItemInserted(notificationModelList.size() - 1);
                    int page = current_page + 1;
                    loadMore(page);
                }
            }
        });
        getData();
    }


    private void getData() {
        Api.getService(Tags.base_url)
                .getNotification("Bearer " + userModel.getToken(), "on", 20, 1, userModel.getId())
                .enqueue(new Callback<NotificationDataModel>() {
                    @Override
                    public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getData() != null) {
                                notificationModelList.clear();
                                notificationModelList.addAll(response.body().getData().getData());
                                if (notificationModelList.size() > 0) {
                                    adapter.notifyDataSetChanged();
                                    binding.tvNoData.setVisibility(View.GONE);
                                } else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationDataModel> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });

    }

    private void loadMore(int page) {

        callLoadMore = Api.getService(Tags.base_url).getNotification("Bearer " + userModel.getToken(), "on", 20, page, userModel.getId());
        callLoadMore.enqueue(new Callback<NotificationDataModel>() {
            @Override
            public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
                isLoading = false;
                notificationModelList.remove(notificationModelList.size() - 1);
                adapter.notifyItemRemoved(notificationModelList.size() - 1);

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getData() != null) {
                        if (response.body().getData().getData().size() > 0) {
                            int oldPos = notificationModelList.size() - 1;
                            notificationModelList.addAll(response.body().getData().getData());
                            adapter.notifyItemRangeChanged(oldPos, notificationModelList.size());
                            current_page = response.body().getData().getCurrent_page();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationDataModel> call, Throwable t) {
                try {
                    isLoading = false;
                    if (notificationModelList.get(notificationModelList.size() - 1) == null) {
                        notificationModelList.remove(notificationModelList.size() - 1);
                        adapter.notifyItemRemoved(notificationModelList.size() - 1);
                    }


                    if (t.getMessage() != null) {
                        Log.e("error", t.getMessage() + "__");

                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                            Toast.makeText(NotificationActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                        } else {
                            if (!t.getMessage().toLowerCase().contains("socket close")){
                                Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Error", e.getMessage() + "__");
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenToNotifications(NotFireModel notFireModel) {
        if (userModel != null) {
            if (isLoading) {
                if (callLoadMore!=null){
                    callLoadMore.cancel();
                }
                isLoading = false;
                current_page = 1;
                if (notificationModelList.get(notificationModelList.size() - 1) == null) {
                    notificationModelList.remove(notificationModelList.size() - 1);
                    adapter.notifyItemRemoved(notificationModelList.size() - 1);
                }
            }
            getData();
        }
    }

    @Override
    public void back() {
        finish();
    }


    public void setItemDelete(NotificationDataModel.Data.NotificationModel model, int adapterPosition) {


        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .deleteNotification("Bearer " + userModel.getToken(), model.getId(), userModel.getId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            notificationModelList.remove(adapterPosition);
                            adapter.notifyItemRemoved(adapterPosition);

                            if (notificationModelList.size() > 0) {
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
                                Toast.makeText(NotificationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(NotificationActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });

    }
}
