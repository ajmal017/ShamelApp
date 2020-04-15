package com.arabdevelopers.shamelapp.services;


import com.arabdevelopers.shamelapp.models.AdsDataModel;
import com.arabdevelopers.shamelapp.models.AppDataModel;
import com.arabdevelopers.shamelapp.models.LikeDislikeModel;
import com.arabdevelopers.shamelapp.models.MainDeptSliderData;
import com.arabdevelopers.shamelapp.models.MainDeptSubDeptDataModel;
import com.arabdevelopers.shamelapp.models.NotificationCountModel;
import com.arabdevelopers.shamelapp.models.NotificationDataModel;
import com.arabdevelopers.shamelapp.models.PlaceGeocodeData;
import com.arabdevelopers.shamelapp.models.PlaceMapDetailsData;
import com.arabdevelopers.shamelapp.models.SubDeptSliderData;
import com.arabdevelopers.shamelapp.models.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {

    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);

    @FormUrlEncoded
    @POST("api/register")
    Call<UserModel> signUpWithoutImage(@Field("name") String name,
                                       @Field("phone_code") String phone_code,
                                       @Field("phone") String phone,
                                       @Field("gender") String gender,
                                       @Field("type") String type
    );

    @Multipart
    @POST("api/register")
    Call<UserModel> signUpWithImage(@Part("name") RequestBody name,
                                    @Part("phone_code") RequestBody phone_code,
                                    @Part("phone") RequestBody phone,
                                    @Part("gender") RequestBody gender,
                                    @Part("type") RequestBody type,
                                    @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @POST("api/login")
    Call<UserModel> login(@Field("phone_code") String phone_code,
                          @Field("phone") String phone);

    @GET("api/main-page")
    Call<MainDeptSliderData> getSliders_MainDepartments();

    @GET("api/show-setting")
    Call<AppDataModel> getSettings(@Header("lang") String lang);

    @GET("api/sub-page")
    Call<SubDeptSliderData> getSliders_SubDepartments(@Query("department_id") int department_id);

    @GET("api/advertisements-by-sub-department")
    Call<AdsDataModel> getAds(@Query("pagination_status") String pagination_status,
                              @Query("per_link_") int per_link,
                              @Query("page") int page,
                              @Query("sub_department_id") int sub_department_id,
                              @Query("user_id") String user_id
    );


    @FormUrlEncoded
    @POST("api/like-dislike-action")
    Call<LikeDislikeModel> likeDislike(@Header("Authorization") String token,
                                       @Field("user_id") int user_id,
                                       @Field("advertisement_id") int advertisement_id);


    @GET("api/departments-with-sub-departments")
    Call<MainDeptSubDeptDataModel> getMainSubDepartment();


    @Multipart
    @POST("api/add-advertisement")
    Call<ResponseBody> addAds(@Header("Authorization") String token,
                              @Part("name") RequestBody name,
                              @Part("department_id") RequestBody department_id,
                              @Part("sub_department_id") RequestBody sub_department_id,
                              @Part("user_id") RequestBody user_id,
                              @Part("content") RequestBody content,
                              @Part("latitude") RequestBody latitude,
                              @Part("longitude") RequestBody longitude,
                              @Part("address") RequestBody address,
                              @Part MultipartBody.Part image
    );


    @FormUrlEncoded
    @POST("api/logout")
    Call<ResponseBody> logout(@Header("Authorization") String token,
                              @Field("user_id") int user_id
    );

    @GET("api/favourite")
    Call<AdsDataModel> getFavourite(@Query("pagination_status") String pagination_status,
                                    @Query("per_link_") int per_link,
                                    @Query("page") int page,
                                    @Query("user_id") int user_id
    );

    @FormUrlEncoded
    @POST("api/notification-count")
    Call<NotificationCountModel> getNotificationCount(@Header("Authorization") String token,
                                                      @Field("user_id") int user_id
    );

    @FormUrlEncoded
    @POST("api/notification-is-read")
    Call<ResponseBody> readNotification(@Header("Authorization") String token,
                                        @Field("user_id") int user_id
    );


    @GET("api/notifications")
    Call<NotificationDataModel> getNotification(@Header("Authorization") String token,
                                                @Query("pagination_status") String pagination_status,
                                                @Query("per_link_") int per_link,
                                                @Query("page") int page,
                                                @Query("user_id") int user_id
    );


    @FormUrlEncoded
    @POST("api/delete_notifications")
    Call<ResponseBody> deleteNotification(@Header("Authorization") String token,
                                          @Field("id") int id,
                                          @Field("user_id") int user_id
    );

}