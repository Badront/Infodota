package com.badr.infodota.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.badr.infodota.R;
import com.badr.infodota.api.trackdota.game.Game;
import com.badr.infodota.api.trackdota.game.League;
import com.badr.infodota.api.trackdota.game.Team;
import com.badr.infodota.view.PinnedSectionListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ABadretdinov
 * 14.04.2015
 * 12:34
 */
public class FeaturedGamesAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {
    DisplayImageOptions options;
    private LayoutInflater inflater;
    private List<Object> mGames=new ArrayList<Object>();
    private ImageLoader imageLoader;
    private SimpleDateFormat dateTimeFormat=new SimpleDateFormat("HH:mm  dd.MM.yyyy");
    public FeaturedGamesAdapter(Context context,List<Game> games){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_item)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
        if(games!=null){
            for (Game game:games) {
                int size=mGames.size();
                if(size==0||!((Game)mGames.get(size-1)).getLeague().equals(game.getLeague())){
                    GameHeader header=new GameHeader();
                    League league=game.getLeague();
                    header.url=league.getUrl();
                    header.id=league.getId();
                    header.hasImage=league.isHasImage();
                    header.name=league.getName();
                    mGames.add(header);
                }
                mGames.add(game);
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
        return mGames.size();
    }

    @Override
    public Object getItem(int position) {
        return mGames.get(position);
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
            itemView=inflater.inflate(R.layout.trackdota_live_game_list_section,parent,false);
            TextView sectionHeader = (TextView) itemView.findViewById(R.id.text);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
            GameHeader header= (GameHeader) object;
            if(!TextUtils.isEmpty(header.name))
            {
                sectionHeader.setText(header.name);
            }
            else {
                sectionHeader.setText("Unspecified league");
            }
            if(header.hasImage){
                imageView.setVisibility(View.VISIBLE);
                imageLoader.displayImage("http://www.trackdota.com/data/images/leagues/"+header.id+".jpg", imageView, options);
            }
            else {
                imageView.setVisibility(View.INVISIBLE);
            }
        }
        else {
            FeaturedGameHolder holder;
            if(itemView==null||itemView.getTag()==null){
                itemView=inflater.inflate(R.layout.trackdota_featured_game_row,parent,false);
                holder=new FeaturedGameHolder();
                holder.radiantTag= (TextView) itemView.findViewById(R.id.radiant_tag);
                holder.direTag= (TextView) itemView.findViewById(R.id.dire_tag);
                holder.radiantScore= (TextView) itemView.findViewById(R.id.radiant_score);
                holder.direScore= (TextView) itemView.findViewById(R.id.dire_score);
                holder.scoreHolder=itemView.findViewById(R.id.score_holder);
                holder.radiantName= (TextView) itemView.findViewById(R.id.radiant_name);
                holder.direName= (TextView) itemView.findViewById(R.id.dire_name);
                holder.radiantLogo= (ImageView) itemView.findViewById(R.id.radiant_logo);
                holder.direLogo= (ImageView) itemView.findViewById(R.id.dire_logo);
                holder.gameStartTime= (TextView) itemView.findViewById(R.id.game_start_time);
                holder.viewers= (TextView) itemView.findViewById(R.id.viewers);
                holder.gameDuration= (TextView) itemView.findViewById(R.id.game_duration);
                itemView.setTag(holder);
            }
            else {
                holder= (FeaturedGameHolder) itemView.getTag();
            }
            Game game= (Game) object;
            Team radiant=game.getRadiant();
            if(radiant!=null){
                holder.radiantTag.setText(!TextUtils.isEmpty(radiant.getTag())?radiant.getTag():"Radiant");
                holder.radiantName.setText(!TextUtils.isEmpty(radiant.getName())?radiant.getName():"Radiant");
                if(radiant.isHasLogo()) {
                    imageLoader.displayImage("http://www.trackdota.com/data/images/teams/" + radiant.getId() + ".png", holder.radiantLogo, options);
                }
                else {
                    holder.radiantLogo.setImageResource(R.drawable.default_img);
                }

            }
            else {
                holder.radiantTag.setText("Radiant");
                holder.radiantName.setText("Radiant");
                holder.radiantLogo.setImageResource(R.drawable.default_img);
            }
            holder.radiantScore.setText(String.valueOf(game.getRadiantKills()));
            Team dire=game.getDire();
            if(dire!=null){
                holder.direTag.setText(!TextUtils.isEmpty(dire.getTag())?dire.getTag():"Dire");
                holder.direName.setText(!TextUtils.isEmpty(dire.getName())?dire.getName():"Dire");
                if(dire.isHasLogo()) {
                    imageLoader.displayImage("http://www.trackdota.com/data/images/teams/" + dire.getId() + ".png", holder.direLogo, options);
                }else {
                    holder.direLogo.setImageResource(R.drawable.default_img);
                }

            }
            else {
                holder.direTag.setText("Dire");
                holder.direName.setText("Dire");
                holder.direLogo.setImageResource(R.drawable.default_img);
            }
            holder.direScore.setText(String.valueOf(game.getDireKills()));

            holder.gameStartTime.setText(dateTimeFormat.format(new Date(game.getStartTime()*1000)));
            holder.viewers.setText(game.getSpectators()+" viewers");
            holder.gameDuration.setText(game.getDuration()/60+" minutes");
        }
        return itemView;
    }

    public class FeaturedGameHolder{
        ImageView radiantLogo;
        ImageView direLogo;
        TextView radiantTag;
        TextView direTag;
        TextView radiantScore;
        TextView direScore;
        View scoreHolder;
        TextView radiantName;
        TextView direName;
        TextView gameStartTime;
        TextView viewers;
        TextView gameDuration;
    }
}
