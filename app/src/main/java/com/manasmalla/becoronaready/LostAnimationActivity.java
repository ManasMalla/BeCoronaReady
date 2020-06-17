package com.manasmalla.becoronaready;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class LostAnimationActivity extends AppCompatActivity {


    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_animation);

        videoView = findViewById(R.id.lostAnimationVideoView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.game_lost_animation));
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startActivity(new Intent(LostAnimationActivity.this, DashboardActivity.class));
            }
        });

    }

    public void skipOnClick(View view) {
        startActivity(new Intent(this, DashboardActivity.class));
    }
}
