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

import android.content.ContentValues;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.List;

import fr.eisti.android.myplaylist.R;
import fr.eisti.android.myplaylist.adapters.PlayListEditAdapter;
import fr.eisti.android.myplaylist.beans.Music;
import fr.eisti.android.myplaylist.dao.MyDataBase;
import fr.eisti.android.myplaylist.utils.PlayListUtils;

/**
 * Created by Harrous Elias on 06/11/2017
 */
public class PlayListEditActivity extends ActionBarActivity {

    private long playListId;
    private List<Music> phoneMusics = new ArrayList<Music>();
    private List<Music> playListMusics = new ArrayList<Music>();
    private ListView musicView;
    private SQLiteDatabase myDataBase;

    private static final String TAG = "PlayListEditActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_list_edit);
        setTitle(getResources().getString(R.string.title_activity_play_list_edit));

        myDataBase = new MyDataBase(getApplicationContext()).getWritableDatabase();
        playListId = getIntent().getLongExtra("PLAYLIST_ID", 0);
        playListMusics = getPlayListMusics();
        phoneMusics = PlayListUtils.getPhoneMusics(getContentResolver());

        musicView = (ListView) findViewById(R.id.music_list);
        musicView.setAdapter(new PlayListEditAdapter(this, phoneMusics, playListMusics));

        PlayListUtils.setOnMusicClickAction((ListView) findViewById(R.id.music_list), playListMusics);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.playlist_visualize_header, menu);
        getMenuInflater().inflate(R.menu.playlist_edit_header, menu);
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
     * Get music registered in the playlist which is being edited
     */
    public List<Music> getPlayListMusics() {
        List<Music> playListMusics = new ArrayList<Music>();

        Cursor task_results = myDataBase.rawQuery("SELECT * FROM PLAYLIST WHERE id = " + playListId, null);
        int nameCol = task_results.getColumnIndex("name");
        while (task_results.moveToNext()) {
            ((EditText) findViewById(R.id.editText2)).setText(task_results.getString(nameCol));
        }

        task_results = myDataBase.rawQuery("SELECT * FROM MUSIC m, PLAYLIST_MUSICS pm WHERE pm.idPlaylist = " + playListId+ " AND m.id = pm.idMusic", null);
        int idMusicCol = task_results.getColumnIndex("id");
        int nameFileCol = task_results.getColumnIndex("file");
        int nameMusicCol = task_results.getColumnIndex("title");
        int nameAuthorCol = task_results.getColumnIndex("author");
        int nameDurationCol = task_results.getColumnIndex("duration");

        while (task_results.moveToNext()) {
            playListMusics.add(new Music(task_results.getInt(idMusicCol), task_results.getString(nameFileCol), task_results.getString(nameMusicCol), task_results.getString(nameAuthorCol), task_results.getLong(nameDurationCol)));
        }
        task_results.close();

        return playListMusics;
    }


    /**
     * Edit the playlist, changes are registered
     * @param view
     */
    public void editPlayList(View view) {
        Log.d(TAG, "Start editPlayList");
        ContentValues values;
        long musicId;
        try {
            String name = ((EditText) findViewById(R.id.editText2)).getText().toString();
            myDataBase.beginTransaction();
            myDataBase.execSQL("UPDATE PLAYLIST SET NAME='"+name+"' WHERE id="+playListId);
            myDataBase.execSQL("DELETE FROM PLAYLIST_MUSICS WHERE idPlaylist="+playListId);
            for (Music music : playListMusics) {
                values = new ContentValues();
                values.put("file", music.getFile());
                values.put("title", music.getTitle());
                values.put("author", music.getAuthor());
                values.put("duration", music.getDuration());
                musicId = myDataBase.insert("MUSIC", null, values);

                values = new ContentValues();
                values.put("idPlayList", playListId);
                values.put("idMusic", musicId);
                myDataBase.insert("PLAYLIST_MUSICS", null, values);
            }
            myDataBase.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            myDataBase.endTransaction();
            finish();
        }
    }
}