package project.teachyourself;

import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import project.teachyourself.model.Client;
import project.teachyourself.model.User;
import project.teachyourself.model.UserApiService;
import project.teachyourself.model.bodies.BodyInsert;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Retrofit service to send user related http request
     */
    private UserApiService userService;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mNameView;
    private EditText mAgeView;
    private ImageButton showHideView;
    private Switch mAdminView;

    private Call<User> call = null;

    /**
     * Callback method called when the activity is created.
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupActionBar();
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        // Display Show/Hide button if it's hidden and there is a password
        mPasswordView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && showHideView.getVisibility() != View.VISIBLE) {
                    showHideView.setVisibility(View.VISIBLE);
                }
            }
        });

        mNameView = findViewById(R.id.name);
        mAgeView = findViewById(R.id.age);
        mAdminView = findViewById(R.id.switchAdmin);

        Button mEmailSignInButton = (Button) findViewById(R.id.buttonRegisterForm);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        showHideView = findViewById(R.id.showForm);
        showHideView.setTag(false);
        showHideView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!(boolean)showHideView.getTag()) {
                    // Show password
                    showHideView.setTag(true);
                    showHideView.setImageDrawable(getDrawable(R.drawable.ic_visibility_off_black_24dp));
                    mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // Hide password
                    showHideView.setTag(false);
                    showHideView.setImageDrawable(getDrawable(R.drawable.ic_visibility_black_24dp));
                    mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        userService = Client.getInstance().userApiService;
    }

    /**
     * Email validator
     * @param email the email input
     * @return boolean that indicate if the input is valid email
     */
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /**
     * Password validator
     * @param password the password input
     * @return boolean that indicate if the password is valid
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mNameView.setError(null);
        mAgeView.setError(null);

        // Store values at the time of the register attempt.
        String email = mEmailView.getText().toString();
        String name = mNameView.getText().toString();
        String age = mAgeView.getText().toString();
        String password = mPasswordView.getText().toString();
        int ageInt = 0;
        boolean admin = mAdminView.isChecked();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
            showHideView.setVisibility(View.GONE);
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
            showHideView.setVisibility(View.GONE);
        }

        // Check for a valid age
        if (TextUtils.isEmpty(age)) {
            mAgeView.setError(getString(R.string.error_field_required));
            focusView = mAgeView;
            cancel = true;
        } else {
            try {
                ageInt = Integer.parseInt(age);
            } catch (Exception e) {
                mAgeView.setError(getString(R.string.error_age_invalid));
                focusView = mAgeView;
                cancel = true;
            }
        }

        // Check for a valid name
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progress.setMessage(getString(R.string.progress_register));
            progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (call != null)
                        call.cancel();
                }
            });
            progress.show();

            // Asynchronous retrofit request
            call = userService.insert(new BodyInsert(email, name, ageInt, password, admin));
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (progress != null && progress.isShowing())
                        progress.dismiss();
                    if (response.isSuccessful()) {
                        displayToast(getString(R.string.toast_saved));
                        finish();
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
                    if (progress != null && progress.isShowing())
                        progress.dismiss();
                    if (call.isCanceled())
                        displayToast(getString(R.string.toast_canceled));
                    else
                        displayToast(String.format(getString(R.string.toast_problem), t.getMessage()));
                }
            });
        }
    }



    /**
     * Check contact permissions and load data from contacts
     */
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Fill a dropdown list with email retrieved from contacts
     * @param emailAddressCollection emails retrieved from contacts
     */
    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);

        // If there is only 1 email, set it directly
        if (emailAddressCollection.size() == 1) {
            mEmailView.setText(emailAddressCollection.get(0));
            mEmailView.dismissDropDown();
        }
    }

    /**
     * Request contact permission
     * @return permission to access contact
     */
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Interface used to retrieve contact informations
     */
    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    // LOADER CALLBACKS | RETRIEVE CONTACTS INFORMATIONS

    /**
     * Called when the system needs a new loader to be created.
     * Your code should create a Loader object and return it to the system
     * @param i The ID whose loader is to be created.
     * @param bundle Any arguments supplied by the caller.
     * @return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }


    /**
     * Called when a loader has finished loading data.
     * Typically, your code should display the data to the user.
     * @param cursorLoader The Loader that has finished.
     * @param cursor The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    /**
     * Called when a previously created loader is being reset
     * @param cursorLoader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
