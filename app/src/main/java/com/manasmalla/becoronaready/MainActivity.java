package com.manasmalla.becoronaready;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    int gotScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.manasmalla.becoronaready", MODE_PRIVATE);

        Intent getintent = getIntent();
        gotScore = getintent.getIntExtra("score", 20);
        if (gotScore > 20) {
            Log.i("gotScore2", String.valueOf(sharedPreferences.getInt("totScore", 35)));
            sharedPreferences.edit().putInt("totScore", sharedPreferences.getInt("totScore", 20) + 35).apply();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);

    }

    public void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void jigsawOnClick(View view) {
        Intent intent = new Intent(this, JigsawPuzzleActivity.class);
        startActivity(intent);
    }

    public void quizOnClick(View view) {
        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }

    public void WWDCGameOnClick(View view) {
        Intent intent = new Intent(this, WWDCLevel1.class);
        startActivity(intent);
    }

    public void GuessMeOnClick(View view) {
        Intent intent = new Intent(this, GuessMeActivity.class);
        startActivity(intent);
    }

}
