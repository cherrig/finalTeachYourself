package project.teachyourself.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;

/**
 * QuizViewModel is used to store data displayed in QuizActivity views
 * It's lifecycle survive configuration change like screen rotation
 * @see AndroidViewModel
 */
public class QuizViewModel extends AndroidViewModel {

    private int current;
    private int score;

    private CountDownTimer timer;
    private MutableLiveData<Integer> timerSeconds;
    private MutableLiveData<Boolean> timerFinished;

    /**
     * Constructor
     * @param application application
     */
    public QuizViewModel(@NonNull Application application) {
        super(application);
        timerSeconds = new MutableLiveData<>();
        timerFinished = new MutableLiveData<>();
        current = 0;
        score = 0;
    }

    /**
     * Get the current question
     * @return current question
     */
    public int getCurrent() {
        return current;
    }

    /**
     * Set the current question
     * @param current current question
     */
    public void setCurrent(int current) {
        this.current = current;
    }

    /**
     * Get score
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * Set the score
     * @param score score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Get the timer
     * @return timer
     */
    public CountDownTimer getTimer() {
        return timer;
    }

    /**
     * Get the timer seconds live data
     * @return live data containing current second of the timer
     */
    public LiveData<Integer> getTimerSeconds() {
        return timerSeconds;
    }

    /**
     * Get timer finished state in a live data
     * @return live data containing a boolean, true if timer is finished
     */
    public LiveData<Boolean> getTimerFinished() {
        return timerFinished;
    }


    /**
     * Starts a new timer if there isn't one already running
     * @param seconds timer length in second
     */
    public void startTimer(int seconds) {
        if (timer != null)
            return ;
        timer = new CountDownTimer(seconds * 1000, 1000) {

            @Override
            public void onTick(long l) {
                int seconds = (int) (l / 1000);
                timerSeconds.setValue(seconds);
            }

            @Override
            public void onFinish() {
                timerSeconds.setValue(0);
                timerFinished.setValue(true);
            }
        };
        timer.start();
    }

    /**
     * Cancels the timer
     */
    public void cancelTimer() {
        timerFinished.setValue(false);
        if (timer != null)
            timer.cancel();
        timer = null;
    }
}
