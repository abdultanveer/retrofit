package com.spot.broadcastreceivers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

        private static final String TAG = MainActivity.class.getSimpleName();

        // TODO - insert your themoviedb.org API KEY here
        private final static String API_KEY = "03764d1b4dd8f3ef2ee9b951e22d1b94";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            if (API_KEY.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain your API KEY first from themoviedb.org", Toast.LENGTH_LONG).show();
                return;
            }

            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);

            Call<MoviesResponse> call = apiService.getTopRatedMovies(API_KEY);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse>call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    Log.d(TAG, "Number of movies received: " + movies.size());
                }

                @Override
                public void onFailure(Call<MoviesResponse>call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                }
            });
        }


    public void showNotification(View view) {

        switch (view.getId()){
            case R.id.buttonalarm:
                setAlarm();
                break;
            case R.id.buttonnotif:
                showNotification();
                break;
        }


    }

    private void setAlarm() {
        AlarmManager alarmManager =
                (AlarmManager) getSystemService(ALARM_SERVICE);

        int alarmType = AlarmManager.RTC_WAKEUP;
        long triggerAtMillis = System.currentTimeMillis()+30*1000;

        long intervalMillis = 15*1000;
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:98765432"));
        PendingIntent operation =
                PendingIntent.getActivity(this,345,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact( alarmType,
         triggerAtMillis,
         operation);

    }

    private void showNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "mychannel id")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Title")
                .setContentText("content text")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(007, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "my channel";
            String description = "channel description";//getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("mychannel id", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
