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
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collections;

import fr.eisti.android.myplaylist.R;
import fr.eisti.android.myplaylist.beans.Music;
import fr.eisti.android.myplaylist.beans.PlayList;
import fr.eisti.android.myplaylist.dao.MyDataBase;
import fr.eisti.android.myplaylist.preferences.ListSharedPrefs;
import fr.eisti.android.myplaylist.utils.PlayListUtils;

/**
 * Created by Harrous Elias on 05/03/2015
 */
public class BackgroundSoundService extends Service implements MediaPlayer.OnCompletionListener {

    public static MediaPlayer mediaPlayer = null;
    public static float volume = 0.5f;
    public static PlayList playlist;
    public static int currentMusicIndex;
    public static int counter;

    private SQLiteDatabase myDataBase;
    private SharedPreferences prefs;
    private boolean isRandomMode;
    private boolean looping;

    private static final String TAG = "BackGroundSoundService";

    public class LocalBinder extends Binder {
        public BackgroundSoundService getService() {
            return BackgroundSoundService.this;
        }
    }

    private IBinder binder = new LocalBinder();


    @Override
    public void onCreate() {
        super.onCreate();
        this.myDataBase = new MyDataBase(getApplicationContext()).getWritableDatabase();
        this.prefs = getApplicationContext().getSharedPreferences(ListSharedPrefs.PREFS_NAME, 0);
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
        playlist = PlayListUtils.getPlayListById(myDataBase, intent.getLongExtra("PLAYLIST_ID", -1));
        currentMusicIndex = intent.getIntExtra("MUSIC_INDEX", 0);

        Log.d(TAG, "Should launch the music with index "+currentMusicIndex+" : "+playlist.getMusicList().get(currentMusicIndex).getTitle());

        if (playlist != null && playlist.getMusicList().size() > 0) {
            if (isRandomMode) {
                manageMusicOrder();
                Log.d(TAG, "After ordering the list, we launch the music with index "+currentMusicIndex+" : "+playlist.getMusicList().get(currentMusicIndex).getTitle());
            }
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(playlist.getMusicList().get(currentMusicIndex).getFile());
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.start();
                counter++;
            } catch (IOException e) {
                e.printStackTrace();
                stopServiceWithToast(R.string.error_load_music);
            }
        } else {
            stopServiceWithToast(R.string.error_number_of_music);
        }
        return Service.START_STICKY;
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
        player.setVolume(volume, volume);
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
        return binder;
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
        }
        playlist = null;
        currentMusicIndex = -1;
    }

    /**
     * The user can modify the volume, even when a music is running
     */
    public static void modifyVolume() {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }
}