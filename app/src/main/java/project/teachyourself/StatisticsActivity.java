package project.teachyourself;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;

import project.teachyourself.model.Category;
import project.teachyourself.model.Client;
import project.teachyourself.model.UserApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Display user's statistics or global statistics for all categories.
 * Uses charts to display advanced statistics.
 */
public class StatisticsActivity extends BaseActivity {

    private String email;
    private StatisticListAdapter adapter;

    private Call<List<Category>> call;

    private FrameLayout frameProgressBar;


    /**
     * Retrofit service to send user related http request
     */
    private UserApiService userService;

    /**
     * Bottom Navigation listener
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.statistics_navigation_personal:
                    retrieveStatistics(true);
                    return true;
                case R.id.statistics_navigation_global:
                    retrieveStatistics(false);
                    return true;
            }
            return false;
        }
    };

    /**
     * Callback method called when the activity is created.
     * Set a listener on every buttons
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        setupActionBar();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        frameProgressBar = findViewById(R.id.frameProgressBarStatistics);

        email = getIntent().getStringExtra(LoginActivity.EXTRA_USER_EMAIL);
        adapter = new StatisticListAdapter(this);

        RecyclerView mRecyclerView = findViewById(R.id.statisticRecyclerView);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        userService = Client.getInstance().userApiService;
        retrieveStatistics(true);
    }

    /**
     * Callback called when activity is not visible.
     * Cancels asynchronous call
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (call != null)
            call.cancel();
    }

    /**
     * Retrieves statistics for user or globally
     * @param user true for user statistics | false for global
     */
    private void retrieveStatistics(boolean user) {
        frameProgressBar.setVisibility(View.VISIBLE);
        if (user)
            call = userService.getStatistics(email);
        else
            call = userService.getStatistics(null);

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    adapter.setCategories(response.body());
                } else {
                    try {
                        String error = response.errorBody().string();
                        displayToast("Erreur: " + error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                frameProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                if (!call.isCanceled())
                    displayToast("Problème: " + t.getMessage());
                frameProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
