package com.badr.infodota.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.adapter.holder.CommonStatHolder;
import com.badr.infodota.adapter.holder.PlayerMatchHolder;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.api.matchdetails.Player;
import com.badr.infodota.api.matchhistory.Match;
import com.badr.infodota.api.matchhistory.PlayerMatch;
import com.badr.infodota.service.hero.HeroService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * User: ABadretdinov
 * Date: 20.01.14
 * Time: 19:00
 */
public class MatchAdapter extends BaseRecyclerAdapter<PlayerMatch,PlayerMatchHolder> {
    DisplayImageOptions options;
    private ImageLoader imageLoader;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm  dd.MM.yyyy");

    public MatchAdapter(List<PlayerMatch> matches) {
        super(matches);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        sdf.setTimeZone(tz);
    }

    public void addMatches(List<PlayerMatch> subMatches) {
        if (subMatches != null) {
            getItems().addAll(subMatches);
            notifyDataSetChanged();
        }
    }

    @Override
    public PlayerMatchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_row, parent, false);
        return new PlayerMatchHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(PlayerMatchHolder holder, int position) {
        PlayerMatch entity=getItem(position);
        holder.gameStartTime.setText(sdf.format(entity.getGameTime()));
        int gameType=entity.getLobbyType();
        Context context=holder.gameType.getContext();
        if (gameType != -1) {
            holder.gameType.setText(context.getResources().getStringArray(R.array.lobby_types)[gameType]);
        }
        Hero hero=entity.getPlayer().getHero();
        imageLoader.displayImage("assets://heroes/" + hero.getDotaId() + "/full.png", holder.heroImg, options);
        holder.heroName.setText(hero.getLocalizedName());
    }
}
