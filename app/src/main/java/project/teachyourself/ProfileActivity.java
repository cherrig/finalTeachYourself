package project.teachyourself;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import project.teachyourself.model.Client;
import project.teachyourself.model.User;
import project.teachyourself.model.UserApiService;
import project.teachyourself.model.bodies.BodySaveImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Profile Activity display user profile
 */
public class ProfileActivity extends BaseActivity implements IPickResult {

    private String email;

    private ImageView mImageView;

    private Call<User> call;

    private FrameLayout frameProgressBar;

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
        setContentView(R.layout.activity_profile);
        setupActionBar();

        email = getIntent().getStringExtra(LoginActivity.EXTRA_USER_EMAIL);

        userService = Client.getInstance().userApiService;
        retrieveUser();

        mImageView = findViewById(R.id.imageViewProfile);
        mImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        frameProgressBar = findViewById(R.id.frameProgressBarProfile);
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
     * Open a Dialog to pick an image
     */
    private void pickImage() {
        PickImageDialog.build(new PickSetup()).show(this);
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
                frameProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (!call.isCanceled())
                    displayToast(String.format(getString(R.string.toast_problem), t.getMessage()));
                frameProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Display user's information
     * @param user
     */
    private void displayUser(User user) {
        TextView mNameView = findViewById(R.id.ProfileName);
        TextView mEmailView = findViewById(R.id.ProfileEmail);
        TextView mAgeView = findViewById(R.id.ProfileAge);

        mNameView.setText(user.getName());
        mEmailView.setText(user.getEmail());
        mAgeView.setText(String.format(getString(R.string.timer), user.getAge()));

        RequestOptions ro = new RequestOptions()
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .circleCrop()
                .placeholder(R.drawable.ic_account)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(this)
                .load(user.getImage())
                .apply(ro)
                .into(mImageView);
    }

    /**
     * Callback when an image is picked for upload. From jrvansuita/PickImage library
     * @param pickResult pickResult
     */
    @Override
    public void onPickResult(PickResult pickResult) {
        if (pickResult.getError() != null) {
            Toast.makeText(this, pickResult.getError().getMessage(), Toast.LENGTH_LONG).show();
            return ;
        }
        //If you want the Uri.
        //Mandatory to refresh image from Uri.
        //getImageView().setImageURI(null);

        //Setting the real returned image.
        //getImageView().setImageURI(r.getUri());

        //If you want the Bitmap.
        //getImageView().setImageBitmap(r.getBitmap());

        // Convert the bitmap image into a base64 string
        Bitmap bitmap = pickResult.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String image =  Base64.encodeToString(imageBytes, Base64.DEFAULT);

        userService.saveImage(new BodySaveImage(email, image)).enqueue(new Callback<User>() {
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
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                displayToast(String.format(getString(R.string.toast_problem), t.getMessage()));
            }
        });
    }
}
