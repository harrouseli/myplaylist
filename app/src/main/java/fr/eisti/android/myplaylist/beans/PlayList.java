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

import java.util.ArrayList;
import java.util.List;

import fr.eisti.android.myplaylist.beans.Music;

/**
 * Created by Harrous Elias on 02/03/2015
 */

public class PlayList {

    private long id;
    private String name;
    private List<Music> musicList;


    public PlayList(long id) {
        this.id = id;
        this.musicList = new ArrayList<Music>();
    }

    public PlayList(long id, String name) {
        this.id = id;
        this.name = name;
        this.musicList = new ArrayList<Music>();

    }

    public PlayList(long id, String name, List<Music> musicList) {
        this.id = id;
        this.name = name;
        this.musicList = musicList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

    public void addMusic(Music music) {
        musicList.add(music);
    }

    @Override
    public String toString() {
        return "PlayList{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", musicList=" + musicList +
                '}';
    }
}
