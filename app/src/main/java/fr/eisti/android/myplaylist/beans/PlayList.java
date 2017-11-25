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
