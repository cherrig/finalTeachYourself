package project.teachyourself;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.io.IOException;

import okhttp3.RequestBody;
import project.teachyourself.model.Client;
import project.teachyourself.model.Question;
import project.teachyourself.model.UserApiService;
import project.teachyourself.model.bodies.BodyAddQuestion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This activity allow an admin acount to create a new question
 */
public class QuestionActivity extends BaseActivity {

    private Call<Question> call;
    private UserApiService userApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        final Spinner categoryView = findViewById(R.id.spinner_category);
        final Spinner levelView = findViewById(R.id.spinner_level);
        final EditText questionView = findViewById(R.id.editQuestion);
        final EditText answer1View = findViewById(R.id.editAnswer1);
        final EditText answer2View = findViewById(R.id.editAnswer2);
        final EditText answer3View = findViewById(R.id.editAnswer3);
        final EditText answer4View = findViewById(R.id.editAnswer4);
        final Spinner correctionView = findViewById(R.id.spinner_correction);
        final Button createButton = findViewById(R.id.buttonCreateQuestion);
        final Switch imageSwitch = findViewById(R.id.switchQuestionImage);

        userApiService = Client.getInstance().userApiService;

        createButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Reset errors
                questionView.setError(null);
                answer1View.setError(null);
                answer2View.setError(null);
                answer3View.setError(null);
                answer4View.setError(null);

                String question = questionView.getText().toString();
                String answer1 = answer1View.getText().toString();
                String answer2 = answer2View.getText().toString();
                String answer3 = answer3View.getText().toString();
                String answer4 = answer4View.getText().toString();

                boolean cancel = false;
                View focusView = null;

                // Check non empty required field.
                if (TextUtils.isEmpty(answer4)) {
                    answer4View.setError(getString(R.string.error_field_required));
                    focusView = answer4View;
                    cancel = true;
                }
                if (TextUtils.isEmpty(answer3)) {
                    answer3View.setError(getString(R.string.error_field_required));
                    focusView = answer3View;
                    cancel = true;
                }
                if (TextUtils.isEmpty(answer2)) {
                    answer2View.setError(getString(R.string.error_field_required));
                    focusView = answer2View;
                    cancel = true;
                }
                if (TextUtils.isEmpty(answer1)) {
                    answer1View.setError(getString(R.string.error_field_required));
                    focusView = answer1View;
                    cancel = true;
                }
                if (TextUtils.isEmpty(question)) {
                    questionView.setError(getString(R.string.error_field_required));
                    focusView = questionView;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    progress.setMessage(getString(R.string.progress_createQuestion));
                    progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if (call != null)
                                call.cancel();
                        }
                    });
                    progress.show();

                    String category = categoryView.getSelectedItem().toString();
                    int level = levelView.getSelectedItemPosition() + 1;
                    int correction = correctionView.getSelectedItemPosition() + 1;

                    // Asynchronous retrofit request
                    call = userApiService.addQuestion(category, level,
                            new BodyAddQuestion(question, answer1, answer2, answer3, answer4, correction, imageSwitch.isChecked()));
                    call.enqueue(new Callback<Question>() {
                        @Override
                        public void onResponse(Call<Question> call, Response<Question> response) {
                            if (progress != null && progress.isShowing())
                                progress.dismiss();
                            if (response.isSuccessful()) {
                                displayToast(getString(R.string.toast_saved));
                                questionView.setText("");
                                answer1View.setText("");
                                answer2View.setText("");
                                answer3View.setText("");
                                answer4View.setText("");
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
                        public void onFailure(Call<Question> call, Throwable t) {
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
        });

    }
}
