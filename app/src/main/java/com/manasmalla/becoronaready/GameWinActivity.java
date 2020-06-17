package com.manasmalla.becoronaready;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameWinActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_win);


        sharedPreferences = getSharedPreferences("com.manasmalla.becoronaready", MODE_PRIVATE);
        sharedPreferences.edit().putInt("totScore", sharedPreferences.getInt("totScore", 20) + 20).apply();

        TextView textView = findViewById(R.id.praiseTextView);
        String praiseText = (String) textView.getText();
        praiseText = praiseText + getSharedPreferences("com.manasmalla.becoronaready", MODE_PRIVATE).getString("username", "User") + " !";
        textView.setText(praiseText);

    }

    public void continueOnClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void homeOnClick(View view) {
        startActivity(new Intent(GameWinActivity.this, DashboardActivity.class));
    }

}
