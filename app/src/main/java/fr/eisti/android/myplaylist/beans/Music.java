package fr.eisti.android.myplaylist.beans;

/**
 * Created by Harrous Elias on 02/03/2015
 */
public class Music {

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
