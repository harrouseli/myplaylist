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
import fr.eisti.android.myplaylist.beans.Artist;
import fr.eisti.android.myplaylist.beans.Music;
import fr.eisti.android.myplaylist.beans.PlayList;
import fr.eisti.android.myplaylist.services.BackgroundSoundService;
import fr.eisti.android.myplaylist.utils.NumberUtils;
import fr.eisti.android.myplaylist.utils.PlayListUtils;

/**
 * Created by Harrous Elias on 07/01/2018
 */
public class PlayListByArtistsAdapter extends BaseAdapter {

    private List<Artist> artistsList;
    private PlayList playlist;
    private LayoutInflater artistInf;

    public PlayListByArtistsAdapter(Context c, List<Artist> artistsList, PlayList playList){
        this.artistsList=artistsList;
        this.playlist=playList;
        this.artistInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return artistsList.size();
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
        //map to artist layout
        LinearLayout artistLay = (LinearLayout)artistInf.inflate(R.layout.artist, parent, false);
        //get views
        TextView nameView = (TextView)artistLay.findViewById(R.id.artist_name);
        TextView musicsNumberView = (TextView)artistLay.findViewById(R.id.artist_musics_number);
        //get artist using position
        Artist currArtist = artistsList.get(position);
        //fill the views with music information
        nameView.setText(currArtist.getName());
        musicsNumberView.setText(artistInf.getContext().getResources().getString(R.string.musics_number)+" : "+currArtist.getMusicsList().size());

        if (PlayListUtils.checkIfPlaylistContainsArtist(playlist, currArtist.getName())) {
            nameView.setTextColor(Color.parseColor("#009900"));
            musicsNumberView.setTextColor(Color.parseColor("#009900"));
        }

        //set position as tag
        artistLay.setTag(position);
        return artistLay;
    }
}