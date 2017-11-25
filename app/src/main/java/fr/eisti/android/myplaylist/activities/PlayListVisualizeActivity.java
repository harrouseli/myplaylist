package fr.eisti.android.myplaylist.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.eisti.android.myplaylist.R;
import fr.eisti.android.myplaylist.adapters.PlayListVisualizeAdapter;
import fr.eisti.android.myplaylist.beans.Music;
import fr.eisti.android.myplaylist.beans.PlayList;
import fr.eisti.android.myplaylist.dao.MyDataBase;
import fr.eisti.android.myplaylist.services.BackgroundSoundService;
import fr.eisti.android.myplaylist.utils.PlayListUtils;

/**
 * Created by Harrous Elias on 12/11/2017
 */
public class PlayListVisualizeActivity extends ActionBarActivity {

    private PlayList playList;
    private SQLiteDatabase myDataBase;

    private TextView titreView;
    private ListView musicsView;

    private static final String TAG = "PLVisualizeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_list_visualize);
        setTitle(getResources().getString(R.string.title_activity_play_list_visualize));

        myDataBase = new MyDataBase(getApplicationContext()).getWritableDatabase();
        playList = PlayListUtils.getPlayListById(myDataBase, getIntent().getLongExtra("PLAYLIST_ID", 0));

        Log.d(TAG, "Playlist selected : = "+ playList.getId());

        modifyActivityLayout();
        setMusicActionOnClick();
        manageButtons();
    }

    private void modifyActivityLayout() {
        SpannableString content = new SpannableString(playList.getName());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        titreView = (TextView) findViewById(R.id.playlist_name);
        titreView.setText(content);
        musicsView = (ListView) findViewById(R.id.music_list);
        musicsView.setAdapter(new PlayListVisualizeAdapter(this, playList.getMusicList(), playList.getId()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.playlist_visualize_header, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.return_home: {
                finish();
                return true;
            }
            case R.id.play_mode : {
                Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.volume_management : {
                Intent intent = new Intent(getApplicationContext(), SeekBarActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.about : {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayListVisualizeActivity.this);
                builder.setMessage(R.string.about_text);
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Display the buttons according to the status of the media player
     */
    public void manageButtons() {
        // there is a running playlist
        if (BackgroundSoundService.mediaPlayer != null) {
            // the running playlist and the visualized one are not the same
            if (BackgroundSoundService.playlist.getId() != playList.getId()) {
                findViewById(R.id.button4).setVisibility(View.GONE);
                findViewById(R.id.button7).setVisibility(View.GONE);
                findViewById(R.id.button8).setVisibility(View.GONE);
            }
            else {
                // the playlist is in pause
                if (BackgroundSoundService.mediaPlayer.isPlaying() == false) {
                    findViewById(R.id.button3).setVisibility(View.GONE);
                    findViewById(R.id.button7).setVisibility(View.GONE);
                }
                // the playlist is playing
                else {
                    findViewById(R.id.button8).setVisibility(View.GONE);
                }
            }
        }
        // there is no running playlist
        else {
            findViewById(R.id.button4).setVisibility(View.GONE);
            findViewById(R.id.button7).setVisibility(View.GONE);
            findViewById(R.id.button8).setVisibility(View.GONE);
        }
    }


    /**
     * Start the selected playlist
     * @param view
     */
    public void startPlayList(View view) {
        Intent backgroundSoundIntent = new Intent(getApplicationContext(), BackgroundSoundService.class);
        if (BackgroundSoundService.mediaPlayer != null) {
            stopService(backgroundSoundIntent);
        }
        backgroundSoundIntent.putExtra("PLAYLIST_ID", playList.getId());

        startService(backgroundSoundIntent);
        finish();
        startActivity(getIntent());
    }


    /**
     * Launch the clicked music if nothing is running, or if the playlist in which belong the music is running
     */
    public void setMusicActionOnClick() {
        final Intent backgroundSoundServiceIntent = new Intent(getApplicationContext(), BackgroundSoundService.class);
        ((ListView) findViewById(R.id.music_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                stopService(backgroundSoundServiceIntent);
                //long musicId = Long.valueOf(((TextView) ((LinearLayout) arg1).getChildAt(3)).getText().toString()).longValue();
                Log.d(TAG, "Playlist started = "+playList.getId());
                Log.d(TAG, "Music selected = "+pos);
                backgroundSoundServiceIntent .putExtra("PLAYLIST_ID", playList.getId());
                backgroundSoundServiceIntent .putExtra("MUSIC_INDEX", pos);
                startService(backgroundSoundServiceIntent);
                finish();
                startActivity(getIntent());
            }
        });
    }


    /**
     * Stop the listened playlist
     * @param view
     */
    public void stopPlayList(View view) {
        stopService(new Intent(getApplicationContext(), BackgroundSoundService.class));
        finish();
        startActivity(getIntent());
    }


    /**
     * Pause the listened playlist
     * @param view
     */
    public void pausePlayList(View view) {
        if (BackgroundSoundService.mediaPlayer != null && BackgroundSoundService.mediaPlayer.isPlaying()) {
            BackgroundSoundService.mediaPlayer.pause();
            finish();
            startActivity(getIntent());
        }
    }


    /**
     * Resume the listened playlist
     * @param view
     */
    public void resumePlayList(View view) {
        if (BackgroundSoundService.mediaPlayer != null && !BackgroundSoundService.mediaPlayer.isPlaying()) {
            int position = BackgroundSoundService.mediaPlayer.getCurrentPosition();
            BackgroundSoundService.mediaPlayer.seekTo(position);
            BackgroundSoundService.mediaPlayer.start();
            finish();
            startActivity(getIntent());
        }
    }


    /**
     * Focus the EditText when clicking the TextView
     * @param view
     */
    public void moveFocus(View view) {
        ((EditText) findViewById(R.id.editText)).requestFocus();
    }
}