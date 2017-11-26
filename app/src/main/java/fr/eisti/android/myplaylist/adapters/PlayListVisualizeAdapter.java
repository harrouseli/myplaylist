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

package fr.eisti.android.myplaylist.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.eisti.android.myplaylist.R;
import fr.eisti.android.myplaylist.beans.Music;
import fr.eisti.android.myplaylist.services.BackgroundSoundService;
import fr.eisti.android.myplaylist.utils.NumberUtils;

/**
 * Created by Harrous Elias on 12/11/2017
 */
public class PlayListVisualizeAdapter extends BaseAdapter {

    private List<Music> playlistMusics;
    private long playlistId;
    private LayoutInflater musicInf;

    public PlayListVisualizeAdapter(Context c, List<Music> playlistMusics, long playListId){
        this.playlistMusics=playlistMusics;
        this.playlistId=playListId;
        this.musicInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return playlistMusics.size();
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
        Music currSong = playlistMusics.get(position);
        //fill the views with music information
        idView.setText(""+currSong.getId());
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getAuthor());
        fileView.setText(currSong.getFile());
        durationView.setText(""+currSong.getDuration());
        durationDisplayView.setText(""+ NumberUtils.convertDuration(currSong.getDuration()));

        // this playlist is currently running
        if (BackgroundSoundService.playlist != null && BackgroundSoundService.playlist.getId() == playlistId) {
            long runningPlayListId = BackgroundSoundService.playlist.getId();
            long runningMusicId = BackgroundSoundService.playlist.getMusicList().get(BackgroundSoundService.currentMusicIndex).getId();
            if (runningPlayListId == playlistId && runningMusicId == currSong.getId()) {
                // check if the music is running or in pause
                int iconId = BackgroundSoundService.mediaPlayer.isPlaying() ? R.drawable.play : R.drawable.pause;
                Drawable myIcon = parent.getContext().getResources().getDrawable(iconId);
                myIcon.setColorFilter(Color.parseColor("#C0C0C0"), PorterDuff.Mode.SRC_ATOP);
                ImageView imageView = ((ImageView) songLay.findViewById(R.id.imageView2));
                imageView.setImageDrawable(myIcon);
                imageView.setAdjustViewBounds(true);
                imageView.setMaxHeight(26);
                imageView.setMaxWidth(26);
            }
        }
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}