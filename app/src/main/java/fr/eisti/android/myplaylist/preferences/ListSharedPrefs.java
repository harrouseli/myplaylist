package fr.eisti.android.myplaylist.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import fr.eisti.android.myplaylist.R;

/**
 * Created by Harrous Elias on 07/03/2015
 */
public class ListSharedPrefs {
    public final static String PREFS_NAME = "list_prefs";

    public static boolean getBackgroundUpdateFlag(Context context) {
       try {
           SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
       } catch (Exception e) {
           e.printStackTrace();
       }
        return true;
    }

    public static void setBackgroundUpdateFlag(Context context, boolean newValue) {

            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putBoolean(context.getString(R.string.pref_key_looping), newValue);
            prefsEditor.putBoolean(context.getString(R.string.pref_key_random), newValue);
            prefsEditor.commit();
    }
}
