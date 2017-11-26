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
