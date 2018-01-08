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

package fr.eisti.android.myplaylist.beans;

import java.util.List;

/**
 * Created by Harrous Elias on 07/01/2018
 */
public class Artist {

    private String name;
    private List<Music> musicsList;

    public Artist(String name, List<Music> musicsList) {
        this.name = name;
        this.musicsList = musicsList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Music> getMusicsList() {
        return musicsList;
    }

    public void setMusicsList(List<Music> musicsList) {
        this.musicsList = musicsList;
    }

}
