package com.arabdevelopers.shamelapp.notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_notification.NotificationActivity;
import com.arabdevelopers.shamelapp.models.NotFireModel;
import com.arabdevelopers.shamelapp.preferences.Preferences;
import com.arabdevelopers.shamelapp.remote.Api;
import com.arabdevelopers.shamelapp.tags.Tags;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FireBaseMessaging extends FirebaseMessagingService {

    private Preferences preferences = Preferences.getInstance();
    private Map<String,String> map;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        map = remoteMessage.getData();

        for (String key:map.keySet())
        {
            Log.e("key",key+"    value "+map.get(key));
        }

        if (getSession().equals(Tags.session_login))
        {
            String to_user_id = String.valueOf(Integer.parseInt(map.get("to_user_id")));
            String my_id = getCurrentUser_id();

            if (my_id.equals(to_user_id))
            {
                manageNotification(map);
            }
        }
    }

    private void manageNotification(Map<String, String> map) {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            createNewNotificationVersion(map);
        }else
        {
            createOldNotificationVersion(map);

        }

    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (getSession().equals(Tags.session_login))
        {
            updateTokenFireBase(s);

        }

    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createNewNotificationVersion(Map<String, String> map) {
        String sound_Path = "android.resource://" + getPackageName() + "/" + R.raw.not;

        String title =map.get("title");
        String body = map.get("message");

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        String CHANNEL_ID = "my_channel_02";
        CharSequence CHANNEL_NAME = "my_channel_name";
        int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

        final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);

        channel.setShowBadge(true);
        channel.setSound(Uri.parse(sound_Path), new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build()
        );
        builder.setChannelId(CHANNEL_ID);
        builder.setSound(Uri.parse(sound_Path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round);
        builder.setLargeIcon(bitmap);




        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("not",true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);



        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));



        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {

            manager.createNotificationChannel(channel);
            manager.notify(Tags.not_tag,Tags.not_id, builder.build());

            EventBus.getDefault().post(new NotFireModel(true));

        }


    }

    private void createOldNotificationVersion(Map<String, String> map) {


        String sound_Path = "android.resource://" + getPackageName() + "/" + R.raw.not;

        String title =map.get("title");
        String body = map.get("message");


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


        builder.setSound(Uri.parse(sound_Path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round);
        builder.setLargeIcon(bitmap);

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("not",true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(Tags.not_tag,Tags.not_id, builder.build());



            EventBus.getDefault().post(new NotFireModel(true));

        }
    }



    private void updateTokenFireBase(String token) {


        FirebaseInstanceId.getInstance()
                .getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {

                try {

                    Api.getService(Tags.base_url)
                            .updateToken(token,Integer.parseInt(getCurrentUser_id()),1)
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful() && response.body() != null )
                                    {
                                        Log.e("token","updated successfully");
                                    } else {
                                        try {

                                            Log.e("error", response.code() + "_" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    try {

                                        if (t.getMessage() != null) {
                                            Log.e("error", t.getMessage());
                                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                Toast.makeText(FireBaseMessaging.this, R.string.something, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(FireBaseMessaging.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    } catch (Exception e) {
                                    }
                                }
                            });
                } catch (Exception e) {


                }

            }
        });
    }



    private String getCurrentUser_id()
    {
        return String.valueOf(preferences.getUserData(this).getId());

    }

    private String getSession()
    {
        return preferences.getSession(this);
    }
}
