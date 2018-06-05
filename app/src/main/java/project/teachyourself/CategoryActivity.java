package project.teachyourself;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import project.teachyourself.model.Category;
import project.teachyourself.model.Client;
import project.teachyourself.model.User;
import project.teachyourself.model.UserApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The first view of the application.
 * It's main goal is to display all the quiz categories
 */
public class CategoryActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private NavigationView navigationView;
    private String email;

    private Call<User> call;
    private ProgressBar progressBarDrawer;

    /**
     * Retrofit service to send user related http request
     */
    private UserApiService userService;


    /**
     * Callback method called when the activity is created.
     * Set a listener on every buttons
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialise the left navigation menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // retrieves user's email
        email = getIntent().getStringExtra(LoginActivity.EXTRA_USER_EMAIL);
        mRecyclerView = findViewById(R.id.recyclerView);
        progressBarDrawer = navigationView.getHeaderView(0).findViewById(R.id.progressBarDrawer);

        ArrayList<Category> categories = createCategoryList();

        CategoryListAdapter adapter = new CategoryListAdapter(this);
        adapter.setCategories(categories);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.COLUMN);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Category category = ((CategoryListAdapter) mRecyclerView.getAdapter()).getCategoryAtPosition(position);
                levelDialog(category);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        userService = Client.getInstance().userApiService;
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
     * Called when activity is visible again
     */
    @Override
    protected void onResume() {
        retrieveUser();
        super.onResume();
    }

    private ArrayList<Category> createCategoryList() {
        ArrayList<Category> categories = new ArrayList<>();

        categories.add(new Category(getString(R.string.category1),
                getString(R.string.category1_description),
                R.drawable.ic_category1,
                R.color.c1, R.drawable.shadow1));
        categories.add(new Category(getString(R.string.category2),
                getString(R.string.category2_description),
                R.drawable.ic_category2,
                R.color.c2, R.drawable.shadow2));
        categories.add(new Category(getString(R.string.category3),
                getString(R.string.category3_description),
                R.drawable.ic_category3,
                R.color.c3, R.drawable.shadow3));
        categories.add(new Category(getString(R.string.category4),
                getString(R.string.category4_description),
                R.drawable.ic_category4,
                R.color.c4, R.drawable.shadow4));
        categories.add(new Category(getString(R.string.category5),
                getString(R.string.category5_description),
                R.drawable.ic_category5,
                R.color.c5, R.drawable.shadow5));
        categories.add(new Category(getString(R.string.category6),
                getString(R.string.category6_description),
                R.drawable.ic_category6,
                R.color.c6, R.drawable.shadow6));
        categories.add(new Category(getString(R.string.category7),
                getString(R.string.category7_description),
                R.drawable.ic_category7,
                R.color.c7, R.drawable.shadow7));

        return categories;
    }

    /**
     * Open a custom dialog to select the level
     * @param category category of the quiz
     */
    private void levelDialog(final Category category) {
        final Dialog dialog = new Dialog(CategoryActivity.this);
        dialog.setContentView(R.layout.level_dialog);
        dialog.setTitle("Level");

        TextView textTitle = dialog.findViewById(R.id.textTitle);
        TextView textDescription = dialog.findViewById(R.id.textDescription);
        ListView levelList = dialog.findViewById(R.id.levelList);

        textTitle.setText(category.getTitle());
        textDescription.setText(category.getDescription());

        List<String> list = new ArrayList<>();
        list.add(getStringRes(R.string.level1));
        list.add(getStringRes(R.string.level2));
        list.add(getStringRes(R.string.level3));

        levelList.setAdapter(new ArrayAdapter<>(this, R.layout.item_level_list, list));

        levelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startQuiz(category.getTitle(), position + 1);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Retrieve user by email
     */
    private void retrieveUser() {
        call = userService.getUserByEmail(email);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    displayUser(response.body());
                } else {
                    try {
                        String error = response.errorBody().string();
                        displayToast(String.format(getString(R.string.toast_error), error));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                progressBarDrawer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (!call.isCanceled())
                    displayToast(String.format(getString(R.string.toast_problem), t.getMessage()));
                progressBarDrawer.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Display user's information
     * @param user
     */
    private void displayUser(User user) {
        TextView mNameView = navigationView.getHeaderView(0).findViewById(R.id.drawerUserName);
        TextView mEmailView = navigationView.getHeaderView(0).findViewById(R.id.drawerUserEmail);
        TextView mScoreView = navigationView.getHeaderView(0).findViewById(R.id.drawerUserScore);
        ImageView profile = navigationView.getHeaderView(0).findViewById(R.id.imageViewDrawerProfile);

        mNameView.setText(user.getName());
        mEmailView.setText(user.getEmail());
        mScoreView.setText(String.format(getString(R.string.score_percent), user.getScore()));
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProfile();
            }
        });

        RequestOptions ro = new RequestOptions()
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .circleCrop()
                .placeholder(R.drawable.ic_account)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(this)
                .load(user.getImage())
                .apply(ro)
                .into(profile);

        MenuItem questionItem = navigationView.getMenu().findItem(R.id.nav_questions);
        if (user.isAdmin())
            questionItem.setVisible(true);
        else
            questionItem.setVisible(false);
    }

    /**
     * Starts the quiz for the given level
     * @param category quiz category
     * @param level quiz level
     */
    private void startQuiz(String category, int level) {
        Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
        intent.putExtra(QuizActivity.EXTRA_QUIZ_CATEGORY, category);
        intent.putExtra(QuizActivity.EXTRA_QUIZ_LEVEL, level);
        intent.putExtra(LoginActivity.EXTRA_USER_EMAIL, email);
        startActivity(intent);
    }

    /**
     * Start the profile activity
     */
    private void startProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(LoginActivity.EXTRA_USER_EMAIL, email);
        startActivity(intent);
    }

    /**
     * Start the statistics activity
     */
    private void startStatistics() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra(LoginActivity.EXTRA_USER_EMAIL, email);
        startActivity(intent);
    }

    /**
     * Start the about activity
     */
    private void startCreateQuestion() {
        Intent intent = new Intent(this, QuestionActivity.class);
        startActivity(intent);
    }

    /**
     * Start the about activity
     */
    private void startAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Start the settings activity
     */
    private void startSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Callback method to handle back button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Callback method to create menu
     * @param menu the menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Callback method to handle menu button click
     * @param item item clicked in the menu
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method to handle navigation item selection
     * @param item item clicked
     * @return boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_profile)
            startProfile();
        else if (id == R.id.nav_statistics)
            startStatistics();
        else if (id == R.id.nav_questions)
            startCreateQuestion();
        else if (id == R.id.nav_about)
            startAbout();
        else if (id == R.id.nav_logout)
            finish();

        return false;
    }

    /**
     * Helper to get a string from resources
     * @param id string id from strings.xml
     * @return a string object
     */
    private String getStringRes(int id) {
        return getResources().getString(id);
    }


    /**
     * Interface for the RecyclerView.OnItemTouchListener
     */
    public interface ClickListener{

        /**
         * Handle a click
         * @param view view clicked
         * @param position position in the list
         */
        void onClick(View view,int position);

        /**
         * Handle a long click
         * @param view view clicked
         * @param position position in the list
         */
        void onLongClick(View view,int position);
    }

    /**
     * Handle click and long click on RecyclerView
     */
    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}