package com.badr.infodota.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.badr.infodota.R;
import com.badr.infodota.adapter.holder.CommonStatHolder;
import com.badr.infodota.api.CommonStat;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by ABadretdinov
 * 20.03.2015
 * 18:06
 */
public class CommonStatsAdapter extends BaseRecyclerAdapter<CommonStat,CommonStatHolder> {
    protected ImageLoader imageLoader;
    DisplayImageOptions options;
    private String[] localizedWinLose;
    public CommonStatsAdapter(Context context,List<CommonStat> data) {
        super(data);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader = ImageLoader.getInstance();
        localizedWinLose=new String[]{context.getString(R.string.win),context.getString(R.string.lost)};
    }

    @Override
    public CommonStatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_common_stats_row, parent, false);
        return new CommonStatHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(CommonStatHolder holder, int position) {
        CommonStat entity=getItem(position);
        holder.header.setText(entity.getHeader());
        imageLoader
                .displayImage("assets://heroes/" + entity.getHero().getDotaId() + "/full.png",
                        holder.heroImg, options);
        holder.heroName.setText(entity.getHero().getLocalizedName());

        int color=entity.isWon()? Color.GREEN:Color.RED;
        holder.winLoose.setText(localizedWinLose[entity.isWon()?0:1]);
        holder.winLoose.setTextColor(color);

        holder.gameStartTime.setText(entity.getDateTime());
        holder.result.setText(entity.getResult());
    }
}
