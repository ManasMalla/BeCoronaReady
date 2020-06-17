package com.manasmalla.becoronaready;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        videoView = findViewById(R.id.introVideoView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.covid19_ntro));
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(IntroActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });

    }

    public void skipOnClick(View view) {
        Intent intent = new Intent(IntroActivity.this, DashboardActivity.class);
        startActivity(intent);
    }
}
