package project.teachyourself;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;
import project.teachyourself.model.Client;
import project.teachyourself.model.Question;
import project.teachyourself.model.UserApiService;
import project.teachyourself.model.bodies.BodySave;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This activity displays user's score after the quiz
 */
public class ScoreActivity extends BaseActivity {

    private String email;
    private String category;
    private int score;
    private int questionNumber;
    private int wrong;
    private float avgResponseTime;
    private FrameLayout frameProgressBar;

    /**
     * Retrofit service to send user related http request
     */
    private UserApiService userService;


    /**
     * Callback method called when the activity is created.
     * Display score
     * @param savedInstanceState savedInstance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        setupActionBar();

        // Retrieves all data
        email = getIntent().getStringExtra(LoginActivity.EXTRA_USER_EMAIL);
        category = getIntent().getStringExtra(QuizActivity.EXTRA_QUIZ_CATEGORY);
        score = getIntent().getIntExtra(QuizActivity.EXTRA_QUIZ_SCORE, 0);
        questionNumber = getIntent().getIntExtra(QuizActivity.EXTRA_QUIZ_NUMBER, 1);
        wrong = questionNumber - score;
        avgResponseTime = getIntent().getFloatExtra(QuizActivity.EXTRA_QUIZ_AVG_TIME, 0f);

        // display charts
        displayScore();
        displayTimeAverage();

        Button ok = findViewById(R.id.buttonLeaveScore);
        frameProgressBar = findViewById(R.id.frameProgressBarScore);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        userService = Client.getInstance().userApiService;

        saveScore();
    }

    /**
     * Initialise the score Pie Chart
     */
    private void displayScore() {
        PieChart scoreChart = findViewById(R.id.chartScore);

        // Set all entries
        List<PieEntry>  entries = new ArrayList<>();
        entries.add(new PieEntry(score, "(" + score + ")"));
        entries.add(new PieEntry(wrong, "(" + wrong + ")"));

        // Entry specific styling
        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(new int[] {R.color.good, R.color.wrong}, this);
        dataSet.setValueTextColor(getResources().getColor(R.color.white));
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(16f);
        dataSet.setSliceSpace(10f);
        dataSet.setSelectionShift(0f);
        dataSet.setValueFormatter(new PercentFormatter(new DecimalFormat("###,###,##0")));

        // Chart general styling
        scoreChart.setData(new PieData(dataSet));
        scoreChart.setDescription(null);
        scoreChart.setDrawEntryLabels(true);
        scoreChart.setDrawHoleEnabled(true);
        scoreChart.setUsePercentValues(true);
        scoreChart.setCenterText(questionNumber + "");
        scoreChart.setCenterTextSize(38f);
        scoreChart.setHoleRadius(55f);
        scoreChart.setHoleColor(getResources().getColor(R.color.grey));
        scoreChart.setTransparentCircleRadius(0f);
        scoreChart.getLegend().setEnabled(false);
        scoreChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        scoreChart.invalidate();
    }

    /**
     * Initialise the time response average Pie Chart
     */
    private void displayTimeAverage() {
        PieChart timeChart = findViewById(R.id.chartTimeAverage);

        // Set all entries
        List<PieEntry> entriesTime = new ArrayList<>();
        entriesTime.add(new PieEntry(avgResponseTime));
        entriesTime.add(new PieEntry(QuizActivity.TIMER - avgResponseTime));

        // Entry specific styling
        PieDataSet dataSet = new PieDataSet(entriesTime, null);
        dataSet.setColors(new int[] {R.color.good, R.color.white}, this);
        dataSet.setSelectionShift(0f);
        dataSet.setDrawValues(false);

        // Chart general styling
        timeChart.setData(new PieData(dataSet));
        timeChart.setDescription(null);
        timeChart.setDrawEntryLabels(false);
        timeChart.setDrawHoleEnabled(true);
        timeChart.setCenterText(
                String.format(Locale.US, getString(R.string.chart_time_average), avgResponseTime));
        timeChart.setCenterTextSize(19f);
        timeChart.setHoleRadius(55f);
        timeChart.setHoleColor(getResources().getColor(R.color.grey));
        timeChart.setTransparentCircleRadius(0f);
        timeChart.getLegend().setEnabled(false);
        timeChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        timeChart.invalidate();
    }

    /**
     * Save Score
     */
    private void saveScore() {
        userService.save(new BodySave(email, category, score, questionNumber, avgResponseTime)).enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if (response.isSuccessful()) {
                    displayToast(getString(R.string.toast_saved));
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
            public void onFailure(Call<Question> call, Throwable t) {
                displayToast(String.format(getString(R.string.toast_problem), t.getMessage()));
                frameProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
