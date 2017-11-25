package fr.eisti.android.myplaylist.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.eisti.android.myplaylist.R;
import fr.eisti.android.myplaylist.beans.Music;
import fr.eisti.android.myplaylist.utils.NumberUtils;

/**
 * Created by Harrous Elias on 03/03/2015
 */
public class PlayListEditAdapter extends BaseAdapter {

    private List<Music> phoneMusics;
    private List<Music> playlistMusics;
    private LayoutInflater musicInf;

    public PlayListEditAdapter(Context c, List<Music> phoneMusics, List<Music> playlistMusics){
        this.phoneMusics=phoneMusics;
        this.playlistMusics=playlistMusics;
        this.musicInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return phoneMusics.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout)musicInf.inflate(R.layout.music, parent, false);

        //get views
        TextView idView = (TextView)songLay.findViewById(R.id.music_id);
        TextView songView = (TextView)songLay.findViewById(R.id.music_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.music_artist);
        TextView fileView = (TextView)songLay.findViewById(R.id.music_file);
        TextView durationView = (TextView)songLay.findViewById(R.id.music_duration);
        TextView durationDisplayView = (TextView)songLay.findViewById(R.id.music_duration_view);

        //get music using position
        Music currSong = phoneMusics.get(position);

        //fill the views with music information
        idView.setText(""+currSong.getId());
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getAuthor());
        fileView.setText(currSong.getFile());
        durationView.setText(""+currSong.getDuration());
        durationDisplayView.setText(""+ NumberUtils.convertDuration(currSong.getDuration()));

        //iterate over the playlist list of musics, and check if the current song is in.
        //if it's the case, the police turns to green color
        String title, author, file;
        for (Music music : playlistMusics) {
            title = songView.getText().toString();
            author = artistView.getText().toString();
            file = fileView.getText().toString();
            // this music is already in the playlist
            if ((title.equals(music.getTitle()) && (author.equals(music.getAuthor())) && file.equals(music.getFile()))) {
                songView.setTextColor(Color.parseColor("#009900"));
                artistView.setTextColor(Color.parseColor("#009900"));
                durationDisplayView.setTextColor(Color.parseColor("#009900"));
            }
        }

        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}