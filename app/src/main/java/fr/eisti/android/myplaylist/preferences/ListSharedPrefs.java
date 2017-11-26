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
