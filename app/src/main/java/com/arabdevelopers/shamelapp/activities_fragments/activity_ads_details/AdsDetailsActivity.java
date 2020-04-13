package com.arabdevelopers.shamelapp.activities_fragments.activity_ads_details;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.FragmentMapTouchListener;
import com.arabdevelopers.shamelapp.databinding.ActivityAdsDetailsBinding;
import com.arabdevelopers.shamelapp.interfaces.Listeners;
import com.arabdevelopers.shamelapp.language.Language;
import com.arabdevelopers.shamelapp.models.AdsModel;
import com.arabdevelopers.shamelapp.models.LikeDislikeModel;
import com.arabdevelopers.shamelapp.models.UserModel;
import com.arabdevelopers.shamelapp.preferences.Preferences;
import com.arabdevelopers.shamelapp.remote.Api;
import com.arabdevelopers.shamelapp.tags.Tags;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdsDetailsActivity extends AppCompatActivity implements Listeners.BackListener, Listeners.AdDetailsActions , OnMapReadyCallback {
    private ActivityAdsDetailsBinding binding;
    private String lang;
    private AdsModel adsModel;
    private Preferences preferences;
    private UserModel.User userModel;
    private String title;
    private FragmentMapTouchListener fragmentMapTouchListener;
    private Marker marker;
    private GoogleMap mMap;
    private float zoom = 15.6f;
    private int action = -1;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ads_details);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            adsModel = (AdsModel) intent.getSerializableExtra("data");
            title = intent.getStringExtra("title");
        }
    }


    private void initView()
    {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setListener(this);
        binding.setModel(adsModel);
        binding.setTitle(title);
        updateUI();



    }


    private void updateUI() {

        fragmentMapTouchListener = (FragmentMapTouchListener) getSupportFragmentManager().findFragmentById(R.id.map);
        fragmentMapTouchListener.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null) {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
            fragmentMapTouchListener.setListener(()->binding.scrollView.requestDisallowInterceptTouchEvent(true));

            AddMarker(Double.parseDouble(adsModel.getLatitude()),Double.parseDouble(adsModel.getLongitude()),adsModel.getAddress());
        }
    }


    private void AddMarker(double lat, double lng, String address) {


        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));
        } else {
            marker.setTitle(address);
            marker.setPosition(new LatLng(lat, lng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));


        }
    }




    @Override
    public void like_dislike()
    {

        if (userModel!=null)
        {
            Api.getService(Tags.base_url)
                    .likeDislike("Bearer"+userModel.getToken(),userModel.getId(),adsModel.getId())
                    .enqueue(new Callback<LikeDislikeModel>() {
                        @Override
                        public void onResponse(Call<LikeDislikeModel> call, Response<LikeDislikeModel> response) {
                            if (response.isSuccessful()&&response.body()!=null)
                            {
                                if (response.body().getStatus()==0)
                                {
                                    adsModel.setUser_like(null);
                                    action = 0;
                                }else
                                {
                                    adsModel.setUser_like(new AdsModel.User_Like());
                                    action = 1;

                                }

                                binding.setModel(adsModel);


                            }else
                            {
                                if (action==0)
                                {
                                    adsModel.setUser_like(new AdsModel.User_Like());

                                }else
                                {
                                    adsModel.setUser_like(null);
                                }

                                binding.setModel(adsModel);
                            }
                        }

                        @Override
                        public void onFailure(Call<LikeDislikeModel> call, Throwable t) {
                            try {
                                if (action==0)
                                {
                                    adsModel.setUser_like(new AdsModel.User_Like());

                                }else
                                {
                                    adsModel.setUser_like(null);
                                }
                                binding.setModel(adsModel);
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage() + "__");

                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(AdsDetailsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AdsDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
            if (binding.checkbox.isChecked())
            {
                adsModel.setUser_like(null);
                binding.setModel(adsModel);
            }else
            {
                adsModel.setUser_like(new AdsModel.User_Like());
                binding.setModel(adsModel);
            }
        }


    }

    @Override
    public void info() {
        binding.flInfo.setBackgroundResource(R.color.second);
        binding.flLocation.setBackgroundResource(R.color.colorPrimary);
        binding.llInfo.setVisibility(View.VISIBLE);
        binding.flMap.setVisibility(View.GONE);

    }

    @Override
    public void map() {
        binding.flInfo.setBackgroundResource(R.color.colorPrimary);
        binding.flLocation.setBackgroundResource(R.color.second);
        binding.llInfo.setVisibility(View.GONE);
        binding.flMap.setVisibility(View.VISIBLE);
    }

    @Override
    public void call() {
        String phone = adsModel.getUser().getPhone();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
        startActivity(intent);
    }

    @Override
    public void call2() {
        String phone = adsModel.getUser().getOther_phone();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
        startActivity(intent);
    }

    @Override
    public void whatApp() {

        String patterns = "(\\+|00)\\d{5,}";

        if (adsModel.getUser().getWhats_up().matches(patterns))
        {
            String url = "https://api.whatsapp.com/send?phone="+adsModel.getUser().getWhats_up();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }else
        {
            Toast.makeText(this, R.string.inv_whatsapp, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void facebook() {

        String pattern = "https?://.+\\..{2,}";
        if (adsModel.getUser().getFacebook().matches(pattern))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(adsModel.getUser().getFacebook()));
            startActivity(intent);
        }else
        {
            Toast.makeText(this, R.string.inv_url, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void twitter() {
        String pattern = "https?://.+\\..{2,}";
        if (adsModel.getUser().getTwitter().matches(pattern))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(adsModel.getUser().getTwitter()));
            startActivity(intent);
        }else
        {
            Toast.makeText(this, R.string.inv_url, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void instagram() {
        String pattern = "https?://.+\\..{2,}";
        if (adsModel.getUser().getInstagram().matches(pattern))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(adsModel.getUser().getInstagram()));
            startActivity(intent);
        }else
        {
            Toast.makeText(this, R.string.inv_url, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void back() {

        if (action!=-1)
        {
            Intent intent = getIntent();
            intent.putExtra("action",action);
            setResult(RESULT_OK,intent);
        }
        finish();
    }
}
