package project.teachyourself;

import android.animation.ValueAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import project.teachyourself.model.Client;
import project.teachyourself.model.Question;
import project.teachyourself.model.UserApiService;
import project.teachyourself.viewmodel.QuizViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The quiz contains 1 question, 4 responses and a timer
 */
public class QuizActivity extends BaseActivity {

    /**
     * Key to send quiz category between activities via intent.
     * {@value}
     */
    public static final String EXTRA_QUIZ_CATEGORY = "QUIZ_CATEGORY";

    /**
     * Key to send quiz description between activities via intent.
     * {@value}
     */
    public static final String EXTRA_QUIZ_DESCRIPTION = "QUIZ_DESCRIPTION";

    /**
     * Key to send quiz level between activities via intent.
     * {@value}
     */
    public static final String EXTRA_QUIZ_LEVEL = "QUIZ_LEVEL";

    /**
     * Key to send quiz question number between activities via intent.
     * {@value}
     */
    public static final String EXTRA_QUIZ_NUMBER = "QUIZ_NUMBER";

    /**
     * Key to send quiz score between activities via intent.
     * {@value}
     */
    public static final String EXTRA_QUIZ_SCORE = "QUIZ_SCORE";

    /**
     * Key to send average response time between activities via intent.
     * {@value}
     */
    public static final String EXTRA_QUIZ_AVG_TIME = "QUIZ_AVG_TIME";

    /**
     * Number of questions asked for the quiz
     * {@value}
     */
    public static final int QUESTION_NUMBER = 5;

    /**
     * Time in seconds to answer the question
     * {@value}
     */
    public static final int TIMER = 10;

    /**
     * Delay between each questions, in milliseconds
     */
    private static final int QUESTION_DELAY = 400;

    // Text displayed at top of the screen
    private TextView textPosition;
    private TextView textScore;
    private TextView textTimer;

    // Either of them is used to display the question
    private TextView textQuestion;
    private ImageView imageQuestion;

    // Answer buttons
    private Button rep1;
    private Button rep2;
    private Button rep3;
    private Button rep4;
    private ArrayList<Button> buttons;
    private FrameLayout frameProgressBar;

    private List<Question> questions;

    // Background thread to call the next question after a delay
    private Handler handler;
    private Runnable runnable;

    private int questionNumber;

    private QuizViewModel quizViewModel;

    private String email;
    private String category;
    private int level;

    // TODO: put in viewmodel if activity orientation is allowed
    private float avgTime = 0;
    private int timeLeft;

    private Call<List<Question>> call;

    /**
     * Retrofit service to send user related http request
     */
    private UserApiService userService;

    /**
     * Callback method called when the activity is created.
     * Parses questions from the json file and handles errors.
     * Display questions and set listeners.
     * @param savedInstanceState savedInstance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Retrieves data
        email = getIntent().getStringExtra(LoginActivity.EXTRA_USER_EMAIL);
        category = getIntent().getStringExtra(EXTRA_QUIZ_CATEGORY);
        level = getIntent().getIntExtra(EXTRA_QUIZ_LEVEL, 1);

        textQuestion = findViewById(R.id.textQuestion);
        imageQuestion = findViewById(R.id.imageQuestion);
        textPosition = findViewById(R.id.textPosition);
        textScore = findViewById(R.id.textScore);
        textTimer = findViewById(R.id.textTimer);
        frameProgressBar = findViewById(R.id.frameProgressBarScore);

        rep1 = findViewById(R.id.rep1);
        rep2 = findViewById(R.id.rep2);
        rep3 = findViewById(R.id.rep3);
        rep4 = findViewById(R.id.rep4);

        buttons = new ArrayList<>();
        buttons.add(rep1);
        buttons.add(rep2);
        buttons.add(rep3);
        buttons.add(rep4);

        quizViewModel = ViewModelProviders.of(this).get(QuizViewModel.class);

        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int resp = buttons.indexOf(view);
                handleResponse(resp + 1);
            }
        };

        rep1.setOnClickListener(listener);
        rep2.setOnClickListener(listener);
        rep3.setOnClickListener(listener);
        rep4.setOnClickListener(listener);

        // Timer animation
        final float startSize = 16; // Size in pixels
        final float endSize = 24;
        long animationDuration = 600; // Animation duration in ms

        final ValueAnimator animator = ValueAnimator.ofFloat(startSize, endSize);
        animator.setDuration(animationDuration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();

                textTimer.setTextSize(animatedValue);
            }
        });

        // Happens for each seconds
        quizViewModel.getTimerSeconds().observe(this, new Observer<Integer>() {

            @Override
            public void onChanged(@Nullable Integer seconds) {
                timeLeft = seconds;
                textTimer.setText(String.format(getResources().getString(R.string.timer), seconds));
                if (seconds <= 3)
                    textTimer.setTextColor(getResources().getColor(R.color.wrong));
                else
                    textTimer.setTextColor(getResources().getColor(R.color.white));
                animator.start();
            }
        });

        // Happens at the end of the timer
        quizViewModel.getTimerFinished().observe(this, new Observer<Boolean>() {

            @Override
            public void onChanged(@Nullable Boolean finished) {
                if (finished) {
                    quizViewModel.cancelTimer();
                    handleResponse(-1);
                }
            }
        });

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                nextQuestion();
            }
        };

        userService = Client.getInstance().userApiService;
        retrieveQuestions(category, level);
    }

    /**
     * Callback method called when activity is not visible
     */
    @Override
    protected void onStop() {
        handler.removeCallbacks(runnable);
        super.onStop();

    }

    /**
     * Callback method called when activity is destroyed
     * Removes observers
     */
    @Override
    protected void onDestroy() {
        quizViewModel.getTimerSeconds().removeObservers(this);
        quizViewModel.getTimerFinished().removeObservers(this);
        super.onDestroy();
    }

    /**
     * Fill UI elements with question and answer and start timer
     */
    private void displayQuestion() {
        textPosition.setText(String.format(getString(R.string.position_label),
                quizViewModel.getCurrent(), questionNumber));
        textScore.setText(String.format(getString(R.string.score_percent),
                quizViewModel.getScore() * 100 / questionNumber));

        if (!questions.get(quizViewModel.getCurrent()).isImage()) {
            // Question is text
            textQuestion.setVisibility(View.VISIBLE);
            imageQuestion.setVisibility(View.GONE);
            textQuestion.setText(questions.get(quizViewModel.getCurrent()).getQuestion());
        } else {
            // Question is image
            textQuestion.setVisibility(View.GONE);
            imageQuestion.setVisibility(View.VISIBLE);
            putImageOnView(imageQuestion, questions.get(quizViewModel.getCurrent()).getQuestion());
        }
        rep1.setText(questions.get(quizViewModel.getCurrent()).getRep1());
        rep2.setText(questions.get(quizViewModel.getCurrent()).getRep2());
        rep3.setText(questions.get(quizViewModel.getCurrent()).getRep3());
        rep4.setText(questions.get(quizViewModel.getCurrent()).getRep4());

       for(Iterator<Button> it = buttons.iterator(); it.hasNext(); ) {
           Button b = it.next();
           b.setBackgroundColor(getResources().getColor(R.color.white));
           b.setTextColor(getResources().getColor(R.color.black));
           b.setEnabled(true);
       }
        quizViewModel.startTimer(TIMER);
    }

    /**
     * Convert image id into a drawable and set it on the button
     * @param view the view to put the image on
     * @param image image id as string
     */
    private void putImageOnView(ImageView view, String image) {
        try {
            //Field field = R.drawable.class.getField(image);
            //int id = (Integer)field.get(null);
            //view.setImageDrawable(getResources().getDrawable(id));
            // Use glide to scale the image into the view automatically
            Glide.with(this).load(image).into(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Display next question
     */
    private void nextQuestion() {
        if (quizViewModel.getCurrent() <  questionNumber - 1) {
            quizViewModel.setCurrent(quizViewModel.getCurrent() + 1);
            displayQuestion();
        } else {
            // No more questions
            finish();
            if (quizViewModel.getTimer() != null)
                quizViewModel.getTimer().cancel();

            Intent intent = new Intent(this, ScoreActivity.class);
            intent.putExtra(EXTRA_QUIZ_NUMBER, questionNumber);
            intent.putExtra(EXTRA_QUIZ_SCORE, quizViewModel.getScore());
            intent.putExtra(EXTRA_QUIZ_AVG_TIME, avgTime);
            intent.putExtra(LoginActivity.EXTRA_USER_EMAIL, email);
            intent.putExtra(EXTRA_QUIZ_CATEGORY, category);
            startActivity(intent);
        }
    }

    /**
     * Handles responses and starts new question
     * @param response response number
     */
    private void handleResponse(int response) {
        if (questions.isEmpty())
            return;
        int correction = questions.get(quizViewModel.getCurrent()).getCorrection();

        if (response > 0)
            quizViewModel.cancelTimer();
        if (response == correction) {
            // Correct
            quizViewModel.setScore(quizViewModel.getScore() + 1);
            buttons.get(response - 1).setBackgroundColor(getResources().getColor(R.color.good));
            buttons.get(response - 1).setTextColor(getResources().getColor(R.color.white));
        } else {
            // Wrong response
            if (response > 0) {
                buttons.get(response - 1).setBackgroundColor(getResources().getColor(R.color.wrong));
                buttons.get(response - 1).setTextColor(getResources().getColor(R.color.white));
            }
            // No response
            buttons.get(correction - 1).setBackgroundColor(getResources().getColor(R.color.good));
            buttons.get(correction - 1).setTextColor(getResources().getColor(R.color.white));
        }

        // calculate average response time
        int responseTime = TIMER - timeLeft;
        avgTime = avgTime + ((responseTime - avgTime) / (quizViewModel.getCurrent() + 1));
        for (Button b: buttons) {
            b.setEnabled(false);
        }
        handler.postDelayed(runnable, QUESTION_DELAY);
    }

    /**
     * Retrieves all questions from the api
     * for the given category and level
     * @param category quiz category
     * @param level quiz level
     */
    private void retrieveQuestions(String category, int level) {
        questions = new ArrayList<>();

        call = userService.getQuestions(category.replace("'", " "), level);
        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful()) {
                    questions = response.body();
                    if (questions.isEmpty()) {
                        displayToast(getString(R.string.not_implemented));
                        finish();
                        return;
                    }
                    // shuffle questions
                    Collections.shuffle(questions);
                    questionNumber = (questions.size() > QUESTION_NUMBER) ? QUESTION_NUMBER : questions.size();
                    displayQuestion();
                } else {
                    try {
                        String error = response.errorBody().string();
                        displayToast(String.format(getString(R.string.toast_error), error));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                frameProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                if (!call.isCanceled())
                    displayToast(String.format(getString(R.string.toast_problem), t.getMessage()));
                frameProgressBar.setVisibility(View.GONE);
            }
        });
    }

}
