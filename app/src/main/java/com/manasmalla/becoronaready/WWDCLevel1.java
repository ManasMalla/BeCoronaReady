package com.manasmalla.becoronaready;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.unity3d.player.UnityPlayerActivity;

public class WWDCLevel1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wwdcgame1);
    }

    public void WWDCOnClick(View view) {
        Intent intent = new Intent(this, UnityPlayerActivity.class);
        intent.putExtra(Intent.EXTRA_INTENT, new Intent(getApplicationContext(), MainActivity.class));
        startActivity(intent);
    }

}
