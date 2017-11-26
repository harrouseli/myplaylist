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
import fr.eisti.android.myplaylist.activities.MainActivity;
import fr.eisti.android.myplaylist.beans.Music;
import fr.eisti.android.myplaylist.beans.PlayList;
import fr.eisti.android.myplaylist.services.BackgroundSoundService;
import fr.eisti.android.myplaylist.utils.NumberUtils;

/**
 * Created by Harrous Elias on 03/03/2015
 */
public class MainAdapter extends BaseAdapter {

    private List<PlayList> playLists;
    private LayoutInflater playlistInf;

    public MainAdapter(Context c, List<PlayList> thePlaylists){
        playLists=thePlaylists;
        playlistInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return playLists.size();
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
        LinearLayout playlistLay = (LinearLayout)playlistInf.inflate(R.layout.playlist, parent, false);
        PlayList currPlayList = playLists.get(position);

        TextView idView = (TextView)playlistLay.findViewById(R.id.playlist_id);
        TextView nameView = (TextView)playlistLay.findViewById(R.id.playlist_name);
        TextView sizeView = (TextView)playlistLay.findViewById(R.id.playlist_size);

        long duration = 0;
        int size = currPlayList.getMusicList().size();
        // calculate the total duration of the playlist
        for (int i=0; i<size; i++) {
            duration += currPlayList.getMusicList().get(i).getDuration();
        }

        idView.setText(""+currPlayList.getId());
        nameView.setText(currPlayList.getName());
        sizeView.setText(size+" musics - "+ NumberUtils.convertDuration(duration));

        // this playlist is currently running
        if (BackgroundSoundService.playlist != null && BackgroundSoundService.playlist.getId() == currPlayList.getId()) {
            if (currPlayList.getId() == BackgroundSoundService.playlist.getId()) {
                // check if the music is running or in pause
                int iconId = BackgroundSoundService.mediaPlayer.isPlaying() ? R.drawable.play : R.drawable.pause;
                Drawable myIcon = parent.getContext().getResources().getDrawable(iconId);
                myIcon.setColorFilter(Color.parseColor("#C0C0C0"), PorterDuff.Mode.SRC_ATOP);
                ImageView imageView = ((ImageView) playlistLay.findViewById(R.id.imageView1));
                imageView.setImageDrawable(myIcon);
                imageView.setAdjustViewBounds(true);
                imageView.setMaxHeight(26);
                imageView.setMaxWidth(26);
            }
        }

        playlistLay.setTag(position);
        return playlistLay;
    }
}