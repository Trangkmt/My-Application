package com.example.music;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {

    static MediaPlayer mediaPlayer;


    @Override

    public void onCreate() {

        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.country_rock);

        mediaPlayer.setLooping(true); // Lặp lại nhạc

    }


    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {

        // 1. Tạo Notification Channel

        NotificationChannel channel = new NotificationChannel("noti_channel_id", "My Service Channel", NotificationManager.IMPORTANCE_LOW);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);


        // 2. Tạo Notification

        Notification notification = new NotificationCompat.Builder(this, "noti_channel_id")

                .setContentTitle("Đang chạy ngầm")

                .setSmallIcon(R.drawable.ic_launcher_foreground)

                .build();


        // 3. Chỉ định là Foreground Service

        startForeground(1, notification);


        mediaPlayer.start(); // Bắt đầu phát nhạc


        return START_STICKY;

    }


    @Override

    public void onDestroy() {

        super.onDestroy();

        mediaPlayer.stop(); // Dừng nhạc khi Service bị hủy

        mediaPlayer.release();

    }


    @Nullable

    @Override

    public IBinder onBind(Intent intent) {

        return null;

    }

}