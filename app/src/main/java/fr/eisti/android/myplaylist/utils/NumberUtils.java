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