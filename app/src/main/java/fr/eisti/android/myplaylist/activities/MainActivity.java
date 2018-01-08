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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.eisti.android.myplaylist.BuildConfig;
import fr.eisti.android.myplaylist.R;
import fr.eisti.android.myplaylist.beans.PlayList;
import fr.eisti.android.myplaylist.dao.MyDataBase;
import fr.eisti.android.myplaylist.adapters.MainAdapter;
import fr.eisti.android.myplaylist.services.BackgroundSoundService;
import fr.eisti.android.myplaylist.utils.PlayListUtils;


public class MainActivity extends ActionBarActivity {

    private List<PlayList> playlists = new ArrayList<PlayList>();

    private ListView playlistView;
    private SQLiteDatabase myDataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        playlistView = (ListView) findViewById(R.id.playlist_list);
        myDataBase = new MyDataBase(getApplicationContext()).getWritableDatabase();

        setActionOnLongClick();
        setActionOnClick();
        showPlayLists();
    }


     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.menu_main, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         // Handle action bar item clicks here. The action bar will
         // automatically handle clicks on the Home/Up button, so long
         // as you specify a parent activity in AndroidManifest.xml.
         int id = item.getItemId();

         switch (id) {
             case R.id.playlist_create : {
                /*PlayListManagement.playlistManagement = "create";
                Intent intent = new Intent(this, PlayListManagement.class);
                startActivity(intent);*/
                Intent intent = new Intent(this, PlayListCreateActivity.class);
                startActivity(intent);
                return true;
             }
             case R.id.play_mode : {
                 Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
                 startActivity(intent);
                 return true;
             }
             case R.id.about : {
                 AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                 builder.setMessage(getString(R.string.about_text)+"\n\nVersion : "+BuildConfig.VERSION_NAME);
                 AlertDialog alert = builder.create();
                 alert.show();
                 return true;
             }
             case R.id.exit : {
                 stopService(new Intent(this, BackgroundSoundService.class));
                 finishAffinity();
                 return true;
             }
             case R.id.playlist_artists : {
                 Intent intent = new Intent(getApplicationContext(), PlayListByArtistsActivity.class);
                 startActivity(intent);
                 return true;
             }
         }

         return super.onOptionsItemSelected(item);
     }


    @Override
    protected void onResume() {
        super.onResume();
        showPlayLists();
    }


     @Override
     protected void onDestroy() {
         super.onDestroy();
     }


    /**
     * When the user do a long click on a playlist, an alert is displayed
     * The user can either edit the playlist, or delete it
     */
    public void setActionOnLongClick() {
        ((ListView) findViewById(R.id.playlist_list)).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                final long playlistId = Long.valueOf(((TextView) ((LinearLayout) arg1).getChildAt(0)).getText().toString()).longValue();

                AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                alert.setMessage(getResources().getString(R.string.playlist_management_choice));
                alert.setButton(AlertDialog.BUTTON_NEUTRAL,getResources().getString(R.string.playlist_edit_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(), PlayListEditActivity.class);
                            intent.putExtra("PLAYLIST_ID", playlistId);
                            startActivity(intent);
                        }
                });
                alert.setButton(AlertDialog.BUTTON_POSITIVE,getResources().getString(R.string.playlist_delete_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PlayListUtils.deletePlayListById(myDataBase, playlistId);
                            showPlayLists();
                        }
                });
                alert.show();
                return true;
            }
        });
    }


    /**
     * When the user click on a playlist, that one is displayed
     */
    public void setActionOnClick() {
        ((ListView) findViewById(R.id.playlist_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Intent intent = new Intent(getApplicationContext(), PlayListVisualizeActivity.class);
                long playlistId = Long.valueOf(((TextView) ((LinearLayout) arg1).getChildAt(0)).getText().toString()).longValue();
                intent.putExtra("PLAYLIST_ID", playlistId);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            onResume();
        }
    }

    /**
     * All existing playlists are displayed automatically when the application is launched
     * Displayed data are the name, the number of musics and the total duration of the playlist
     */
    public void showPlayLists() {
        playlists = PlayListUtils.getPlayLists(myDataBase);
        playlistView.setAdapter(new MainAdapter(getApplicationContext(), playlists));
    }
 }