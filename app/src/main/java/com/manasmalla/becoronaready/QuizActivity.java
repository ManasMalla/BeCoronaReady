package com.manasmalla.becoronaready;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    private final int[] keys1 = {R.drawable.mask, R.drawable.umbrella, R.drawable.pixel, R.drawable.sunglasses};
    private final int[] keys2 = {R.drawable.whatsapp, R.drawable.pubg, R.drawable.handsanitizer, R.drawable.shakehands};
    private final int[] keys3 = {R.drawable.bakasura, R.drawable.exercise, R.drawable.sleep, R.drawable.pubg};
    private final int[] keys4 = {R.drawable.sleep, R.drawable.bakasura, R.drawable.pubg, R.drawable.yoga};
    private final int[] keys5 = {R.drawable.eat_healthy, R.drawable.raw_food, R.drawable.bakasura, R.drawable.fasting};
    private final List<String> questions = new ArrayList<>();
    private final List<String> answers = new ArrayList<>();
    TextView textQuestion, textTitle;
    LinearLayout linearLayoutOptions;
    Animation smallbigforth;
    int autoRandomGeneratedNumber, questionRandomNumber;
    ImageView option1ImageView, option2ImageView, option3ImageView, option4ImageView;
    List<Integer> optionsOrderArray;
    Random random, randomQuestions;
    TextView scoreTextView;
    int score = 0;
    private List<int[]> keys;
    private int[] keyQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        smallbigforth = AnimationUtils.loadAnimation(this, R.anim.smallbigforth);

        textQuestion = findViewById(R.id.questionTextView);
        textTitle = findViewById(R.id.quizTimeHeading);

        linearLayoutOptions = findViewById(R.id.layoutParentQuiz);
        option1ImageView = findViewById(R.id.option1ImageView);
        option2ImageView = findViewById(R.id.option2ImageView);
        option3ImageView = findViewById(R.id.option3ImageView);
        option4ImageView = findViewById(R.id.option4ImageView);

        scoreTextView = findViewById(R.id.scoreQuizTextView);
        score = 0;
        scoreTextView.setText("Score: " + score);

        questions.add("Q. You decide to go outside your house for supplies. What must you take?");
        questions.add("Q. Now you come back home after getting supplies. What must you do? ");
        questions.add("Q.  Now you are all refreshed and energetic. What must you do?");
        questions.add("Q.  Now you want to increase your immunity in the most natural way, What must you do?");
        questions.add("Q. Hmm... Due to lockdown we aren't able to get fastfood or ready-to-cook food, so what must we do?");

        answers.add(String.valueOf(R.drawable.mask));
        answers.add(String.valueOf(R.drawable.handsanitizer));
        answers.add(String.valueOf(R.drawable.exercise));
        answers.add(String.valueOf(R.drawable.yoga));
        answers.add(String.valueOf(R.drawable.eat_healthy));


        keys = new ArrayList<>();
        keys.add(keys1);
        keys.add(keys2);
        keys.add(keys3);
        keys.add(keys4);
        keys.add(keys5);

        score = 0;

        linearLayoutOptions.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                linearLayoutOptions.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int widthLayout = linearLayoutOptions.getWidth();
                int optionImageView = widthLayout / 2 - (int) getResources().getDimension(R.dimen.paddingOption);

                option1ImageView.getLayoutParams().width = optionImageView;
                option1ImageView.getLayoutParams().height = optionImageView;
                option2ImageView.getLayoutParams().width = optionImageView;
                option2ImageView.getLayoutParams().height = optionImageView;
                option3ImageView.getLayoutParams().width = optionImageView;
                option3ImageView.getLayoutParams().height = optionImageView;
                option4ImageView.getLayoutParams().width = optionImageView;
                option4ImageView.getLayoutParams().height = optionImageView;

                option1ImageView.requestLayout();
                option2ImageView.requestLayout();
                option3ImageView.requestLayout();
                option4ImageView.requestLayout();

            }
        });

        generateQuestion();

    }

    public void generateQuestion() {
        random = new Random();
        optionsOrderArray = new ArrayList<Integer>();

        randomQuestions = new Random();
        questionRandomNumber = randomQuestions.nextInt(questions.size());
        textQuestion.setText(questions.get(questionRandomNumber));
        keyQuestion = keys.get(questionRandomNumber);

        SetupUI setupTask = new SetupUI();
        setupTask.execute("1q");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setImagesOption(List<Integer> optionsArray) {
        option1ImageView.setImageDrawable(getResources().getDrawable(keyQuestion[optionsArray.get(0)]));
        option2ImageView.setImageDrawable(getResources().getDrawable(keyQuestion[optionsArray.get(1)]));
        option3ImageView.setImageDrawable(getResources().getDrawable(keyQuestion[optionsArray.get(2)]));
        option4ImageView.setImageDrawable(getResources().getDrawable(keyQuestion[optionsArray.get(3)]));
        option1ImageView.setTag(String.valueOf(keyQuestion[optionsArray.get(0)]));
        option2ImageView.setTag(String.valueOf(keyQuestion[optionsArray.get(1)]));
        option3ImageView.setTag(String.valueOf(keyQuestion[optionsArray.get(2)]));
        option4ImageView.setTag(String.valueOf(keyQuestion[optionsArray.get(3)]));
    }

    public void optionsOnClick(View view) {
        String tag = (String) view.getTag();
        if (tag.matches(answers.get(questionRandomNumber))) {
            if (score >= 4) {


                // getSharedPreferences("com.manasmalla.becoronaready", MODE_PRIVATE).edit().putInt("score",  getSharedPreferences("com.manasmalla.becoronaready", MODE_PRIVATE).getInt("score", 20) + 20).apply();
                Toast.makeText(this, "Hurray!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, GameWinActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "That's right!", Toast.LENGTH_SHORT).show();
                score += 1;
                Log.i("score", String.valueOf(score));
                scoreTextView.setText("Score: " + score);
                questions.remove(questionRandomNumber);
                answers.remove(questionRandomNumber);
                keys.remove(questionRandomNumber);
                generateQuestion();
            }
        } else {
            Toast.makeText(this, "Sorry, Incorrect!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, GameLoseActivity.class);
            startActivity(intent);
        }

    }

    public class SetupUI extends AsyncTask<String, Void, List<Integer>> {

        @Override
        protected List<Integer> doInBackground(String... strings) {

            for (int i = 0; i < 4; ) {

                autoRandomGeneratedNumber = random.nextInt(4);

                if ((optionsOrderArray.contains(autoRandomGeneratedNumber))) {
//Do nothing
                } else {
                    optionsOrderArray.add(autoRandomGeneratedNumber);
                    i++;
                }
            }
            return optionsOrderArray;
        }

        @Override
        protected void onPostExecute(List<Integer> integers) {
            super.onPostExecute(integers);
            setImagesOption(integers);
        }
    }

}
