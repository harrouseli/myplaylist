package fr.eisti.android.myplaylist.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fr.eisti.android.myplaylist.beans.Music;
import fr.eisti.android.myplaylist.beans.PlayList;

public class NumberUtils {

    /**
     * That method is using to display the duration of a music or playlist in minute - second
     * @param duration
     * @return
     */
    public static String convertDuration(long duration) {
        String result = "";
        long time = duration;
        long second = 0;
        long minute = 0;
        long hour = 0;

        time = time / 1000;
        second = time % 60;
        time = time / 60;
        minute = time % 60;
        time = time /60;
        hour = time;

        if (hour > 0) {
            result += hour+"h+ ";
        }
        result += minute+"m "+second+"s";
        return result;
    }

}