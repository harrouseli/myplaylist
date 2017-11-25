package fr.eisti.android.myplaylist.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Harrous Elias on 02/03/2015
 */
public class MyDataBase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MYPLAYLISTSDATABASE";

    public MyDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS PLAYLIST ( id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT )");
        db.execSQL("CREATE TABLE IF NOT EXISTS MUSIC ( id INTEGER PRIMARY KEY AUTOINCREMENT, file TEXT, title TEXT, author TEXT, duration LONG )");
        db.execSQL("CREATE TABLE IF NOT EXISTS PLAYLIST_MUSICS ( idPlaylist INTEGER, idMusic INTEGER, FOREIGN KEY(idPlaylist) REFERENCES PLAYLIST(id), FOREIGN KEY(idMusic) REFERENCES MUSIC(id), PRIMARY KEY(idPlaylist,idMusic) )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion) {
            Log.w("DATABASE UPGRADE", "Beware, I will use an older version than which you are requested !") ;
        }
        else {
            // ok everything is in order, just keep processing;
        }

    }
}
