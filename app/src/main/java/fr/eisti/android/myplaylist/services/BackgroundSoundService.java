//    This file is part of MyPlaylist.
//
//    MyPlaylist is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    MyPlaylist is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with MyPlaylist.  If not, see <http://www.gnu.org/licenses/>.

package fr.eisti.android.myplaylist.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.eisti.android.myplaylist.R;
import fr.eisti.android.myplaylist.beans.Music;
import fr.eisti.android.myplaylist.beans.PlayList;
import fr.eisti.android.myplaylist.broadcast.BecomingNoisyReceiver;
import fr.eisti.android.myplaylist.dao.MyDataBase;
import fr.eisti.android.myplaylist.preferences.ListSharedPrefs;
import fr.eisti.android.myplaylist.utils.PlayListUtils;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

/**
 * Created by Harrous Elias on 05/03/2015
 */
public class BackgroundSoundService extends Service implements MediaPlayer.OnCompletionListener {

    public static MediaPlayer mediaPlayer = null;
    public static String playlistType = null;

    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;
    private final float LOW_VOLUME = 0.2f;

    public static PlayList playlist;
    public static int currentMusicIndex;
    public static int counter;

    private SQLiteDatabase myDataBase;
    private boolean isRandomMode;
    private boolean looping;

    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();

    final Messenger messenger = new Messenger(new ServiceHandler());

    private static final String TAG = "BackGroundSoundService";

    public class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    super.handleMessage(msg);

            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        this.audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        this.myDataBase = new MyDataBase(getApplicationContext()).getWritableDatabase();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(ListSharedPrefs.PREFS_NAME, 0);
        this.isRandomMode = prefs.getBoolean(getApplicationContext().getString(R.string.pref_key_random), false);
        this.looping = prefs.getBoolean(getApplicationContext().getString(R.string.pref_key_looping), false);;
        this.counter = 0;
    }


    /**
     * Start the listen of the playlist
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        playlistType = intent.getStringExtra("PLAYLIST_TYPE");
        if ("artists".equals(playlistType)) {
            playlist = new PlayList((List<Music>)intent.getSerializableExtra("MUSICS_LIST"));
        } else {
            playlist = PlayListUtils.getPlayListById(myDataBase, intent.getLongExtra("PLAYLIST_ID", -1));
        }

        if (playlist != null && playlist.getMusicList().size() > 0) {
            currentMusicIndex = intent.getIntExtra("MUSIC_INDEX", 0);

            Log.d(TAG, "Selected playlist : " + playlist.getId() + " | " + playlist.getName());
            Log.d(TAG, "Should launch the music with index " + currentMusicIndex + " : " + playlist.getMusicList().get(currentMusicIndex).getTitle());

            if (isRandomMode) {
                manageMusicOrder();
                Log.d(TAG, "After ordering the list, we launch the music with index " + currentMusicIndex + " : " + playlist.getMusicList().get(currentMusicIndex).getTitle());
            }
            manageAudioFocusChange();
            requestAudioFocus();
        } else {
            stopServiceWithToast(R.string.error_number_of_music);
        }
        return Service.START_STICKY;
    }

    private Handler mHandler = new Handler();

    private Runnable mDelayedStopRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        }
    };

    private void manageAudioFocusChange() {
        afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    mediaPlayer.pause();
                    // Wait 30 seconds before stopping playback
                    mHandler.postDelayed(mDelayedStopRunnable, TimeUnit.SECONDS.toMillis(30));
                } else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                    mediaPlayer.pause();
                } else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    modifyVolume(0.2f);
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    modifyVolume(1.0f);
                }
            }
        };
    }

    private void requestAudioFocus() {
        // Request audio focus for playback
        int result = audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(playlist.getMusicList().get(currentMusicIndex).getFile());
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.start();
                counter++;
                registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
            } catch (IOException e) {
                e.printStackTrace();
                stopServiceWithToast(R.string.error_load_music);
            }
        } else {
            Log.d(TAG, "AUDIOFOCUS_REQUEST_FAILED");
        }
    }

    private void manageMusicOrder() {
        long musicId = playlist.getMusicList().get(currentMusicIndex).getId();
        if (currentMusicIndex != -1) {
            Collections.shuffle(playlist.getMusicList());
            for (int i=0; i<playlist.getMusicList().size(); i++) {
                if (playlist.getMusicList().get(i).getId() == musicId) {
                    currentMusicIndex = i;
                    break;
                }
            }
        }
    }


    private void stopServiceWithToast(int message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
        stopSelf();
    }


    /**
     * Launch following music if there is one
     * @param player
     */
    public void onCompletion(MediaPlayer player) {
        boolean keepListening = true;
        player.reset();
        currentMusicIndex++;

        if (looping) {
            if (currentMusicIndex >= playlist.getMusicList().size()) {
                currentMusicIndex = 0;
            }
        } else {
            if (counter < playlist.getMusicList().size()) {
                if (currentMusicIndex >= playlist.getMusicList().size()) {
                    currentMusicIndex = 0;
                }
            } else {
                keepListening = false;
            }
        }

        if (keepListening) {
            try {
                player.setDataSource(playlist.getMusicList().get(currentMusicIndex).getFile());
                player.setOnCompletionListener(this);
                player.prepare();
                player.start();
                counter++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            stopSelf();
        }
    }

    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
    public IBinder onUnBind(Intent intent) { return null; }
    public void onStart(Intent intent, int startId) { }
    public void onStop() { }
    public void onPause() { }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            unregisterReceiver(myNoisyAudioStreamReceiver);
            audioManager.abandonAudioFocus(afChangeListener);
        }
        playlist = null;
        currentMusicIndex = -1;
    }


    /**
     * The user can modify the volume, even when a music is running
     */
    public void modifyVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }
}

class ServiceHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            default:
                super.handleMessage(msg);
        }
    }
}