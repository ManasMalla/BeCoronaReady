package com.manasmalla.becoronaready;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameLoseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lose);

        TextView textView = findViewById(R.id.praiseTextView_lose);
        String praiseText = (String) textView.getText();
        praiseText = praiseText + getSharedPreferences("com.manasmalla.becoronaready", MODE_PRIVATE).getString("username", "User") + " !";
        textView.setText(praiseText);
    }

    public void retryOnClick(View view) {
        startActivity(new Intent(this, QuizActivity.class));
    }

    public void homeOnClick_lose(View view) {
        startActivity(new Intent(this, LostAnimationActivity.class));
    }


}
