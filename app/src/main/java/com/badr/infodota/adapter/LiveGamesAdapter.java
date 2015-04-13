package com.badr.infodota.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.badr.infodota.R;
import com.badr.infodota.api.joindota.MatchItem;
import com.badr.infodota.api.trackdota.game.EnhancedGame;
import com.badr.infodota.api.trackdota.game.EnhancedMatch;
import com.badr.infodota.view.PinnedSectionListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by ABadretdinov
 * 13.04.2015
 * 18:54
 */
public class LiveGamesAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter{
    DisplayImageOptions options;
    private LayoutInflater inflater;
    private List<Object> games=new ArrayList<Object>();
    private ImageLoader imageLoader;
    public LiveGamesAdapter(Context context,List<EnhancedMatch> matches){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
        if(matches!=null){
            for (EnhancedMatch match:matches) {
                GameHeader header=new GameHeader();
                header.url=match.getUrl();
                header.id=match.getId();
                header.hasImage=match.isHasImage();
                header.name=match.getName();
                games.add(header);
                games.addAll(match.getGames());
            }
        }
    }
    public class GameHeader{
        String url;
        long id;
        boolean hasImage;
        String name;
    }
    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType==1;
    }
    @Override
    public int getItemViewType(int position) {
        return getItem(position) instanceof GameHeader? 1 : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public Object getItem(int position) {
        return games.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView=convertView;
        Object object=getItem(position);
        if(object instanceof GameHeader){
            itemView=inflater.inflate(R.layout.live_game_list_section,parent,false);
            TextView sectionHeader = (TextView) itemView.findViewById(R.id.text);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
            GameHeader header= (GameHeader) object;
            sectionHeader.setText(header.name);
            if(header.hasImage){
                imageView.setVisibility(View.VISIBLE);
                imageLoader.displayImage("http://www.trackdota.com/data/images/leagues/"+header.id+".jpg", imageView, options);
            }
            else {
                imageView.setVisibility(View.INVISIBLE);
            }
        }
        else {

        }
        return itemView;
    }
}
