package com.manasmalla.becoronaready;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final SharedPreferences sharedPreferences = this.getSharedPreferences("com.manasmalla.becoronaready", MODE_PRIVATE);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (sharedPreferences.getString("username", null) != null && !(sharedPreferences.getString("username", null).isEmpty())) {
                    intent = new Intent(SplashScreenActivity.this, DashboardActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
        handler.postDelayed(runnable, 1500);

    }
}
