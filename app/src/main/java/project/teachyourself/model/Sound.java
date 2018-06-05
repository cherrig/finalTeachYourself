package project.teachyourself.model;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;

import java.io.IOException;

import project.teachyourself.R;

/**
 * Sound handle sound related actions
 */
public class Sound {

    private MediaPlayer mp;
    private int selected;
    private Resources res;

    private static Sound INSTANCE = null;

    /**
     * Private constructor for singleton
     * @param res resources to get music from
     * @param play music on or off
     * @param selected selected track from 1 to 3
     */
    private Sound(Resources res, boolean play, int selected) {
        mp = new MediaPlayer();
        this.res = res;

        this.selected = selected;
        if (play)
            start();
    }

    /**
     * Get the sound singleton instance
     * @param res resources to get music from
     * @param play music on or off
     * @param selected selected track from 1 to 3
     * @return Sound instance
     */
    public static Sound getInstance(Resources res, boolean play, int selected) {
        if (INSTANCE == null)
            synchronized (Sound.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Sound(res, play, selected);
                }
            }
        return INSTANCE;
    }

    /**
     * Stop the music
     */
    public void stop() {
        mp.stop();
    }

    /**
     * Stop previous music and start the new one in a loop
     */
    public void start() {
        mp.stop();
        mp.reset();
        AssetFileDescriptor afd = null;
        if (selected == 1)
            afd = res.openRawResourceFd(R.raw.track1);
        else if (selected == 2)
            afd = res.openRawResourceFd(R.raw.track2);
        else if (selected == 3)
            afd = res.openRawResourceFd(R.raw.track3);

        if (afd == null)
            return;
        try {
            mp.setLooping(true);
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Select an other music and start it
     * @param selected selected track
     */
    public void setSelected(int selected) {
        if (this.selected == selected)
            return;
        this.selected = selected;
        start();
    }





}
