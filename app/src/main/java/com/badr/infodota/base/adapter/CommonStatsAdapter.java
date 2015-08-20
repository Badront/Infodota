package com.badr.infodota.base.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badr.infodota.R;
import com.badr.infodota.base.adapter.holder.CommonStatHolder;
import com.badr.infodota.base.api.CommonStat;
import com.badr.infodota.util.Utils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by ABadretdinov
 * 20.03.2015
 * 18:06
 */
public class CommonStatsAdapter extends BaseRecyclerAdapter<CommonStat, CommonStatHolder> {
    private String[] localizedWinLose;

    public CommonStatsAdapter(Context context, List<CommonStat> data) {
        super(data);
        localizedWinLose = new String[]{context.getString(R.string.win), context.getString(R.string.lost)};
    }

    @Override
    public CommonStatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_common_stats_row, parent, false);
        return new CommonStatHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(CommonStatHolder holder, int position) {
        CommonStat entity = getItem(position);
        holder.header.setText(entity.getHeader());
        Context context = holder.header.getContext();
        Glide
                .with(context)
                .load(Utils.getHeroFullImage(entity.getHero().getDotaId()))
                .into(holder.heroImg);
        holder.heroName.setText(entity.getHero().getLocalizedName());

        int color = entity.isWon() ? Color.GREEN : Color.RED;
        holder.winLoose.setText(localizedWinLose[entity.isWon() ? 0 : 1]);
        holder.winLoose.setTextColor(color);

        holder.gameStartTime.setText(entity.getDateTime());
        holder.result.setText(entity.getResult());
    }
}
