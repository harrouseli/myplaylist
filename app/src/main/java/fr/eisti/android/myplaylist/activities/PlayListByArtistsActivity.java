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

package fr.eisti.android.myplaylist.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import fr.eisti.android.myplaylist.R;
import fr.eisti.android.myplaylist.adapters.PlayListByArtistsAdapter;
import fr.eisti.android.myplaylist.adapters.PlayListVisualizeAdapter;
import fr.eisti.android.myplaylist.beans.Artist;
import fr.eisti.android.myplaylist.beans.Music;
import fr.eisti.android.myplaylist.beans.PlayList;
import fr.eisti.android.myplaylist.dao.MyDataBase;
import fr.eisti.android.myplaylist.services.BackgroundSoundService;
import fr.eisti.android.myplaylist.utils.PlayListUtils;

/**
 * Created by Harrous Elias on 07/01/2018
 */
public class PlayListByArtistsActivity extends ActionBarActivity {

    private List<Artist> artistsList;
    private List<String> selectedArtists;
    private ListView artistsView;
    private static final String TAG = "PLByArtistsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_list_by_artists);
        setTitle(getResources().getString(R.string.title_activity_play_list_by_artists));

        artistsList = PlayListUtils.getArtistsList(getContentResolver());
        selectedArtists = PlayListUtils.getRunningArtistsList();

        try {
            Log.d(TAG, "Number of artists = " + artistsList.size());
        } catch (Exception e) {
            Log.e(TAG, "No artists found among the musics");
        }

        modifyActivityLayout();
        setArtistActionOnClick();
        manageButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void modifyActivityLayout() {
        artistsView = (ListView) findViewById(R.id.music_list);
        artistsView.setAdapter(new PlayListByArtistsAdapter(this, artistsList, BackgroundSoundService.playlist));
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
            case R.id.about : {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayListByArtistsActivity.this);
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
            if (!"artists".equals(BackgroundSoundService.playlistType)) {
                findViewById(R.id.buttonStop).setVisibility(View.GONE);
                findViewById(R.id.buttonPause).setVisibility(View.GONE);
                findViewById(R.id.buttonResume).setVisibility(View.GONE);
            }
            else {
                // the playlist is in pause
                if (BackgroundSoundService.mediaPlayer.isPlaying() == false) {
                    findViewById(R.id.buttonStart).setVisibility(View.GONE);
                    findViewById(R.id.buttonPause).setVisibility(View.GONE);
                }
                // the playlist is playing
                else {
                    findViewById(R.id.buttonResume).setVisibility(View.GONE);
                }
            }
        }
        // there is no running playlist
        else {
            findViewById(R.id.buttonStop).setVisibility(View.GONE);
            findViewById(R.id.buttonPause).setVisibility(View.GONE);
            findViewById(R.id.buttonResume).setVisibility(View.GONE);
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

        List<Music> musicsList = createPlayListByArtistsList().getMusicList();
        Log.d(TAG, "Starts a playlist with "+selectedArtists.size()+" artists, containing "+musicsList.size()+" musics");
        backgroundSoundIntent.putExtra("MUSICS_LIST", (Serializable) musicsList);
        backgroundSoundIntent.putExtra("PLAYLIST_TYPE", "artists");

        startService(backgroundSoundIntent);
        finish();
        startActivity(getIntent());
    }


    /**
     * Select or un-select the music when it is clicked
     */
    private void setArtistActionOnClick() {

        artistsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                TextView artistNameTextView = ((TextView) ((LinearLayout) arg1).getChildAt(0));
                TextView artistMusicsNumberTextView = ((TextView) ((LinearLayout) arg1).getChildAt(1));
                String artistName = artistNameTextView.getText().toString();
                boolean selection = true;
                int i, index = 0;
                for (i = 0; i < selectedArtists.size(); i++) {
                    if (artistName.equals(selectedArtists.get(i))) {
                        selection = false;
                        index = i;
                    }
                }
                String color;
                if (selection == false) {
                    selectedArtists.remove(index);
                    color = "#C0C0C0";
                } else {
                    selectedArtists.add(artistName);
                    color = "#009900";
                }
                artistNameTextView.setTextColor(Color.parseColor(color));
                artistMusicsNumberTextView.setTextColor(Color.parseColor(color));
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




    private PlayList createPlayListByArtistsList() {
        List<Music> musicList = new ArrayList<>();
        for (int i=0; i<artistsList.size(); i++) {
            if (selectedArtists.contains(artistsList.get(i).getName())) {
                musicList.addAll(artistsList.get(i).getMusicsList());
            }
        }
        return new PlayList(-1,"", musicList);
    }
}