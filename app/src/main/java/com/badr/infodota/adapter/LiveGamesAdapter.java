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
import com.badr.infodota.api.trackdota.game.EnhancedGame;
import com.badr.infodota.api.trackdota.game.EnhancedMatch;
import com.badr.infodota.api.trackdota.game.Team;
import com.badr.infodota.view.PinnedSectionListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.helper.StringUtil;

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
                .showImageOnLoading(R.drawable.empty_item)
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
            LiveGameHolder holder;
            if(itemView==null||itemView.getTag()==null){
                itemView=inflater.inflate(R.layout.live_game_row,parent,false);
                holder=new LiveGameHolder();
                holder.radiantTag= (TextView) itemView.findViewById(R.id.radiant_tag);
                holder.direTag= (TextView) itemView.findViewById(R.id.dire_tag);
                holder.radiantScore= (TextView) itemView.findViewById(R.id.radiant_score);
                holder.direScore= (TextView) itemView.findViewById(R.id.dire_score);
                holder.scoreHolder=itemView.findViewById(R.id.score_holder);
                holder.radiantName= (TextView) itemView.findViewById(R.id.radiant_name);
                holder.direName= (TextView) itemView.findViewById(R.id.dire_name);
                holder.gameState= (TextView) itemView.findViewById(R.id.game_state);
                holder.streams= (TextView) itemView.findViewById(R.id.streams);
                holder.gameTime= (TextView) itemView.findViewById(R.id.game_time);
                itemView.setTag(holder);
            }
            else {
                holder= (LiveGameHolder) itemView.getTag();
            }
            EnhancedGame game= (EnhancedGame) object;
            Team radiant=game.getRadiant();
            if(radiant!=null){
                holder.radiantTag.setText(!TextUtils.isEmpty(radiant.getTag())?radiant.getTag():"Radiant");
                holder.radiantName.setText(!TextUtils.isEmpty(radiant.getName())?radiant.getName():"Radiant");
            }
            else {
                holder.radiantTag.setText("Radiant");
                holder.radiantName.setText("Radiant");
            }
            holder.radiantScore.setText(String.valueOf(game.getRadiantKills()));
            Team dire=game.getDire();
            if(dire!=null){
                holder.direTag.setText(!TextUtils.isEmpty(dire.getTag())?dire.getTag():"Dire");
                holder.direName.setText(!TextUtils.isEmpty(dire.getName())?dire.getName():"Dire");
            }
            else {
                holder.direTag.setText("Dire");
                holder.direName.setText("Dire");
            }
            holder.direScore.setText(String.valueOf(game.getDireKills()));
            StringBuilder gameState=new StringBuilder("Game ");
            gameState.append(game.getDireWins()+game.getRadiantWins()+1);
            gameState.append(" / BO");
            switch (game.getSeriesType()){
                case 0:
                    gameState.append(1);
                    break;
                case 1:
                    gameState.append(3);
                    gameState.append(" (");
                    gameState.append(game.getRadiantWins());
                    gameState.append(" - ");
                    gameState.append(game.getDireWins());
                    gameState.append(")");
                    break;
                default:
                    gameState.append("{").append(game.getSeriesType()).append("}");
            }
            holder.gameState.setText(gameState.toString());
            if(game.getStreams()>0)
            {
                holder.streams.setText(game.getStreams()+" streams");
                holder.streams.setVisibility(View.VISIBLE);
            }
            else {
                holder.streams.setVisibility(View.INVISIBLE);
            }
            switch (game.getStatus()){
                case 1:
                    holder.gameTime.setText("In hero selection");
                    break;
                case 2:
                    holder.gameTime.setText("Waiting for horn");
                    break;
                case 3:
                    holder.gameTime.setText(game.getDuration()/60+" minutes");
                    break;
                default:
            }

        }
        return itemView;
    }
    public class LiveGameHolder{
        TextView radiantTag;
        TextView direTag;
        TextView radiantScore;
        TextView direScore;
        View scoreHolder;
        TextView radiantName;
        TextView direName;
        TextView gameState;
        TextView streams;
        TextView gameTime;
    }
}
