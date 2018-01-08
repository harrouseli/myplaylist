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

import java.io.Serializable;

/**
 * Created by Harrous Elias on 02/03/2015
 */
public class Music implements Serializable {

    private long id;
    private String file;
    private String title;
    private String author;
    private long duration;

    public Music() {
    }

    public Music(String file, String title, String author, long duration) {
        this.id = 0;
        this.file = file;
        this.title = title;
        this.author = author;
        this.duration = duration;
    }

    public Music(long id, String file, String title, String author, long duration) {
        this.id = id;
        this.file = file;
        this.title = title;
        this.author = author;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Music{" +
                "id=" + id +
                ", file='" + file + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

}
