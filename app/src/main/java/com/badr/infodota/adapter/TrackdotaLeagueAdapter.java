package com.badr.infodota.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badr.infodota.R;
import com.badr.infodota.adapter.holder.TrackdotaLeagueHolder;
import com.badr.infodota.api.trackdota.TrackdotaUtils;
import com.badr.infodota.api.trackdota.game.League;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by ABadretdinov
 * 08.06.2015
 * 12:34
 */
public class TrackdotaLeagueAdapter extends BaseRecyclerAdapter<League,TrackdotaLeagueHolder> {
    DisplayImageOptions options;
    private ImageLoader imageLoader;
    private String viewers;
    private String matches;
    public TrackdotaLeagueAdapter(Context context,List<League> data) {
        super(data);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
        viewers=context.getString(R.string.viewers_);
        matches=context.getString(R.string.matches_);
    }

    @Override
    public TrackdotaLeagueHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trackdota_league_row, parent, false);
        return new TrackdotaLeagueHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(TrackdotaLeagueHolder holder, int position) {
        League entity=getItem(position);
        holder.title.setText(entity.getName());
        holder.description.setText(entity.getDescription());
        if(entity.isHasImage()){
            imageLoader.displayImage(TrackdotaUtils.getLeagueImageUrl(entity.getId()),holder.image,options);
        }
        else {
            holder.image.setImageResource(R.drawable.empty_item);
        }

        holder.viewers.setText(MessageFormat.format(viewers,entity.getViews()));
        holder.matches.setText(MessageFormat.format(matches,entity.getMatchCount()));
    }
}
