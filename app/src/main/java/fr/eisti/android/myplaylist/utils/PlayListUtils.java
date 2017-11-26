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

package fr.eisti.android.myplaylist.utils;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.eisti.android.myplaylist.R;
import fr.eisti.android.myplaylist.beans.Music;
import fr.eisti.android.myplaylist.beans.PlayList;
import fr.eisti.android.myplaylist.dao.MyDataBase;

/**
 * Created by Harrous Elias on 06/11/2017
 */
public class PlayListUtils {

    public static List<PlayList> getPlayLists(SQLiteDatabase db) {
        List<PlayList> playlists = new ArrayList<PlayList>();
        PlayList playList;

        Cursor task_results, musics_results;
        int idCol, nameCol, idMusicCol, nameFileCol, nameMusicCol, nameAuthorCol, nameDurationCol;

        task_results = db.rawQuery("SELECT * FROM PLAYLIST", null);
        idCol = task_results.getColumnIndex("id");
        nameCol = task_results.getColumnIndex("name");

        while (task_results.moveToNext()) {
            playList = new PlayList(task_results.getInt(idCol),task_results.getString(nameCol));
            musics_results = db.rawQuery("SELECT * FROM MUSIC m, PLAYLIST_MUSICS pm WHERE pm.idPlaylist = " + task_results.getInt(idCol)+ " AND m.id = pm.idMusic", null);
            idMusicCol = musics_results.getColumnIndex("id");
            nameFileCol = musics_results.getColumnIndex("file");
            nameMusicCol = musics_results.getColumnIndex("title");
            nameAuthorCol = musics_results.getColumnIndex("author");
            nameDurationCol = musics_results.getColumnIndex("duration");
            while (musics_results.moveToNext()) {
                playList.addMusic(new Music(musics_results.getInt(idMusicCol), musics_results.getString(nameFileCol), musics_results.getString(nameMusicCol), musics_results.getString(nameAuthorCol), musics_results.getLong(nameDurationCol)));
            }
            playlists.add(playList);
        }
        return playlists;
    }


    // TODO Apparently it only takes musics from the folder 'Music' from an external storage
    // TODO Find a way to get all musics, from any folder located in an external or internal storage
    /**
     * Find music in the music folder of the phone, and display them
     */
    public static List<Music> getPhoneMusics(ContentResolver musicResolver) {
        List<Music> phoneMusics = new ArrayList<Music>();
        String path = Environment.getExternalStorageDirectory() + "/Music/";
        Uri musicUri = MediaStore.Audio.Media.getContentUriForPath(path);
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int fileColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);

            //add songs to list
            do {
                String thisFile = musicCursor.getString(fileColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                Long thisDuration = musicCursor.getLong(durationColumn);

                Music music = new Music(thisFile, thisTitle, thisArtist, thisDuration);
                phoneMusics.add(music);
            }
            while (musicCursor.moveToNext());
        }
        musicCursor.close();

        Collections.sort(phoneMusics, new Comparator<Music>() {
            public int compare(Music a, Music b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        return phoneMusics;
    }


    /**
     * Select or un-select the music when it is clicked
     */
    public static void setOnMusicClickAction(ListView view, final List<Music> musics) {

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                String titleTextView = ((TextView) ((LinearLayout) arg1).getChildAt(0)).getText().toString();
                String authorTextView = ((TextView) ((LinearLayout) arg1).getChildAt(1)).getText().toString();
                String fileTextView = ((TextView) ((LinearLayout) arg1).getChildAt(2)).getText().toString();
                long durationTextView = Long.parseLong(((TextView) ((LinearLayout) arg1).getChildAt(4)).getText().toString());
                Music musicSelected = new Music(fileTextView, titleTextView, authorTextView, durationTextView);
                boolean selection = true;
                int i, index = 0;
                for (i = 0; i < musics.size(); i++) {
                    if (musicSelected.getTitle().equals(musics.get(i).getTitle()) & musicSelected.getAuthor().equals(musics.get(i).getAuthor())) {
                        selection = false;
                        index = i;
                    }
                }
                if (selection == false) {
                    musics.remove(index);
                    ((TextView) ((LinearLayout) arg1).getChildAt(0)).setTextColor(Color.parseColor("#C0C0C0"));
                    ((TextView) ((LinearLayout) arg1).getChildAt(1)).setTextColor(Color.parseColor("#C0C0C0"));
                    ((TextView) ((LinearLayout) arg1).getChildAt(5)).setTextColor(Color.parseColor("#C0C0C0"));
                } else {
                    musics.add(musicSelected);
                    ((TextView) ((LinearLayout) arg1).getChildAt(0)).setTextColor(Color.parseColor("#009900"));
                    ((TextView) ((LinearLayout) arg1).getChildAt(1)).setTextColor(Color.parseColor("#009900"));
                    ((TextView) ((LinearLayout) arg1).getChildAt(5)).setTextColor(Color.parseColor("#009900"));
                }
            }
        });
    }


    public static PlayList getPlayListById(SQLiteDatabase db, long playlistId) {
        PlayList playList = new PlayList(playlistId);
        Music music = null;
        int idMusicCol, nameFileCol, nameMusicCol, nameAuthorCol, nameDurationCol;
        Cursor task_results = db.rawQuery("SELECT * FROM PLAYLIST WHERE id = " + playlistId, null);
        if (task_results.moveToFirst()) {
            playList.setName(task_results.getString(task_results.getColumnIndex("name")));

            task_results = db.rawQuery("SELECT * FROM MUSIC m, PLAYLIST_MUSICS pm WHERE pm.idPlaylist = " + playlistId + " AND m.id = pm.idMusic", null);
            idMusicCol = task_results.getColumnIndex("id");
            nameFileCol = task_results.getColumnIndex("file");
            nameMusicCol = task_results.getColumnIndex("title");
            nameAuthorCol = task_results.getColumnIndex("author");
            nameDurationCol = task_results.getColumnIndex("duration");
            while (task_results.moveToNext()) {
                music = new Music(task_results.getInt(idMusicCol), task_results.getString(nameFileCol), task_results.getString(nameMusicCol), task_results.getString(nameAuthorCol), task_results.getInt(nameDurationCol));
                playList.addMusic(music);
            }
        }
        Collections.sort(playList.getMusicList(), new Comparator<Music>() {
            public int compare(Music a, Music b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        return playList;
    }

    //TODO Add a Toast ti inform witch PlayList has been deleted
    public static void deletePlayListById(SQLiteDatabase db, long playlistId) {
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM PLAYLIST_MUSICS WHERE idPlaylist="+playlistId);
            db.execSQL("DELETE FROM PLAYLIST WHERE id=" + playlistId);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

}