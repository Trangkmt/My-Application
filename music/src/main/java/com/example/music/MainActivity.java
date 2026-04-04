package com.example.music;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {

    Button button;
    ImageView imageView;
    Context context;
    SeekBar seekBar;
    boolean isPlaying = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        seekBar = findViewById(R.id.seekBar);


        String imageUrl = "https://static-cse.canva.com/blob/1379502/1600w-1Nr6gsUndKw.jpg";
        MyThread myThread = new MyThread(imageUrl, context);
        myThread.start();

        button.setOnClickListener(view -> {
            if (isPlaying) {
                if (MyService.mediaPlayer != null && MyService.mediaPlayer.isPlaying()) {
                    MyService.mediaPlayer.pause();
                    isPlaying = false;
                    button.setText("Play");
                }

            } else {
                startService(new Intent(this, MyService.class));
                isPlaying = true;
                button.setText("Pause");
                updateSeekBar();
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && MyService.mediaPlayer != null) {
                    MyService.mediaPlayer.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSeekBar();
    }
    private void updateSeekBar() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MyService.mediaPlayer != null && MyService.mediaPlayer.isPlaying()) {
                    isPlaying = true;
                    seekBar.setMax(MyService.mediaPlayer.getDuration());
                    seekBar.setProgress(MyService.mediaPlayer.getCurrentPosition());
                }
                handler.postDelayed(this, 1000); // Cập nhật seekbar mỗi 1 giây để đảm bảo thanh tiến trình luôn hiển thị đúng theo thời gian bản nhạc
            }
        }, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isPlaying && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // 1. Khởi tạo Intent là MainActivity
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);


            // 2. Chuyển sang PendingIntent để đưa cho Notification
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);


            // 3. Khởi tạo thông báo
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Music")
                    .setContentText("Nhạc đang phát")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSound(null)
                    // 4. Set Intent vừa tạo ở đây. Khi nhấn vào thông báo sẽ mở lại MainActivity
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);


            // 5. Hiển thị thông báo đến người dùng
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, builder.build());
        }
    }
}
