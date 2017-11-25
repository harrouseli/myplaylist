package fr.eisti.android.myplaylist.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.eisti.android.myplaylist.R;
import fr.eisti.android.myplaylist.adapters.PlayListEditAdapter;
import fr.eisti.android.myplaylist.beans.Music;
import fr.eisti.android.myplaylist.dao.MyDataBase;
import fr.eisti.android.myplaylist.utils.PlayListUtils;


/**
 * Created by Harrous Elias on 12/11/2017
 */
public class PlayListCreateActivity extends ActionBarActivity {

    private List<Music> phoneMusics = new ArrayList<Music>();
    private List<Music> selectedMusics = new ArrayList<Music>();
    private ListView musicView;
    private SQLiteDatabase myDataBase;

    private static final String TAG = "PlayListCreateActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "PlayListCreateActivity start init");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_list_create);
        setTitle(getResources().getString(R.string.title_activity_play_list_create));

        myDataBase = new MyDataBase(getApplicationContext()).getWritableDatabase();
        phoneMusics = PlayListUtils.getPhoneMusics(getContentResolver());

        musicView = (ListView) findViewById(R.id.music_list);
        musicView.setAdapter(new PlayListEditAdapter(this, phoneMusics, selectedMusics));

        PlayListUtils.setOnMusicClickAction((ListView) findViewById(R.id.music_list), selectedMusics);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.playlist_create_header, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Create a new playlist with the name, and selected musics
     * @param view
     */
    public void createPlayList(View view) {

        ContentValues values = new ContentValues();
        long playlistId, musicId;
        try {
            String name = ((EditText) findViewById(R.id.editText)).getText().toString();
            myDataBase.beginTransaction();
            values.put("name", name);
            playlistId = myDataBase.insert("PLAYLIST", null, values);

            for (Music music : selectedMusics) {
                values = new ContentValues();
                values.put("file", music.getFile());
                values.put("title", music.getTitle());
                values.put("author", music.getAuthor());
                values.put("duration", music.getDuration());
                musicId = myDataBase.insert("MUSIC", null, values);

                values = new ContentValues();
                values.put("idPlayList", playlistId);
                values.put("idMusic", musicId);
                myDataBase.insert("PLAYLIST_MUSICS", null, values);
            }
            myDataBase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.error_playlist_creation, Toast.LENGTH_SHORT).show();
        } finally {
            myDataBase.endTransaction();
            finish();
        }
    }


    /**
     * Focus the EditText when clicking the TextView
     * @param view
     */
    public void moveFocus(View view) {
        ((EditText) findViewById(R.id.editText)).requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}