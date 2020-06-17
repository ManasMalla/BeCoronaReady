package com.manasmalla.becoronaready;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuessMeActivity extends AppCompatActivity implements View.OnKeyListener {

    TextView jumbled_word_textView, scoreTextView;
    TextInputEditText jumbed_editText;
    List<String> jumbledWords, unJumbledWords;
    Random random;
    int randomJumbled;
    String answer;

    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_me);

    }

    @Override
    protected void onResume() {
        super.onResume();
        jumbled_word_textView = findViewById(R.id.jumbledWordTextView);
        jumbed_editText = findViewById(R.id.jumbledWordEditText);
        jumbed_editText.setOnKeyListener(this);
        scoreTextView = findViewById(R.id.scoreTextViewJumbled);

        jumbledWords = new ArrayList<>();
        unJumbledWords = new ArrayList<>();

        jumbledWords.add("OROVACRUSIN");
        jumbledWords.add("HUNAW");
        jumbledWords.add("MAYHOEST");
        jumbledWords.add("FASSTAYE");
        jumbledWords.add("DANPMICE");
        jumbledWords.add("KNOWDCOL");
        jumbledWords.add("FECRUW");
        jumbledWords.add("SIOVEPIT");
        jumbledWords.add("ONGASIDIS");
        jumbledWords.add("KERBAUTO");
        jumbledWords.add("ASCEMFAK");
        jumbledWords.add("LOSIONTIA");
        jumbledWords.add("RATINQUAEN");
        jumbledWords.add("GAETVIEN");
        jumbledWords.add("NIASHATINZERD");

        unJumbledWords.add("CORONAVIRUS");
        unJumbledWords.add("WUHAN");
        unJumbledWords.add("STAYHOME");
        unJumbledWords.add("STAYSAFE");
        unJumbledWords.add("PANDEMIC");
        unJumbledWords.add("LOCKDOWN");
        unJumbledWords.add("CURFEW");
        unJumbledWords.add("POSITIVE");
        unJumbledWords.add("DIAGNOSIS");
        unJumbledWords.add("OUTBREAK");
        unJumbledWords.add("FACEMASK");
        unJumbledWords.add("ISOLATION");
        unJumbledWords.add("QUARANTINE");
        unJumbledWords.add("NEGATIVE");
        unJumbledWords.add("HANDSANITIZER");

        random = new Random();
        setupJumbled();
    }

    private void setupJumbled() {
        jumbed_editText.setText("");
        randomJumbled = random.nextInt(jumbledWords.size());
        jumbled_word_textView.setText(jumbledWords.get(randomJumbled));
        answer = unJumbledWords.get(randomJumbled);

        Log.i("answer", answer);
    }

    public void checkAnswerJumbledOnClick(View view) {

        checkAnswer();

    }

    public void checkAnswer() {
        String userAnswer = jumbed_editText.getText().toString();
        userAnswer = userAnswer.toUpperCase();
        if (userAnswer.matches(answer)) {
            //CORRECT
            score++;
            scoreTextView.setText("Score: " + score);
            jumbledWords.remove(randomJumbled);
            unJumbledWords.remove(randomJumbled);
            if (jumbledWords.size() <= 0 && score >= 7) {
                Intent intent = new Intent(this, GameWinActivity.class);
                startActivity(intent);
            } else if (jumbledWords.size() <= 0 && score <= 7) {
                Intent intent = new Intent(this, GameLoseActivity.class);
                startActivity(intent);
            } else {
                setupJumbled();
            }
        } else {
            jumbed_editText.setError("Oops! Wrong Answer!");
            if (jumbledWords.size() <= 0 && score >= 7) {
                Intent intent = new Intent(this, GameWinActivity.class);
                startActivity(intent);
            } else if (jumbledWords.size() <= 0 && score <= 7) {
                Intent intent = new Intent(this, GameLoseActivity.class);
                startActivity(intent);
            } else {
                setupJumbled();
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            checkAnswer();
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        return false;
    }
}
