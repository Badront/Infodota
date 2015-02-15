package com.badr.infodota.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.api.matchdetails.Player;
import com.badr.infodota.api.matchhistory.Match;
import com.badr.infodota.service.hero.HeroService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * User: ABadretdinov
 * Date: 20.01.14
 * Time: 19:00
 */
public class MatchAdapter extends BaseAdapter {
    List<Match> matches;
    LayoutInflater inflater;
    DisplayImageOptions options;
    HeroService heroService = BeanContainer.getInstance().getHeroService();
    private ImageLoader imageLoader;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm  dd.MM.yyyy");
    private long accountId;

    public MatchAdapter(Context context, List<Match> matches, long accountId) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.accountId = accountId;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
        this.matches = matches != null ? matches : new ArrayList<Match>();
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        sdf.setTimeZone(tz);
    }

    public void addMatches(List<Match> subMatches) {
        if (subMatches != null) {
            matches.addAll(subMatches);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return matches.size();
    }

    @Override
    public Match getItem(int position) {
        return matches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return matches.get(position).getMatch_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        MatchHolder holder;
        if (vi == null) {
            vi = inflater.inflate(R.layout.match_row, parent, false);
            holder = new MatchHolder();
            holder.heroImg = (ImageView) vi.findViewById(R.id.hero_img);
            holder.heroName = (TextView) vi.findViewById(R.id.hero_name);
            holder.gameStartTime = (TextView) vi.findViewById(R.id.game_start_time);
            holder.gameType = (TextView) vi.findViewById(R.id.game_type);
            vi.setTag(holder);
        } else {
            holder = (MatchHolder) vi.getTag();
        }
        Match match = getItem(position);

        long timestamp = match.getStart_time();
        String localTime = sdf.format(new Date(timestamp * 1000));
        holder.gameStartTime.setText(localTime);
        Context context = parent.getContext();
        int gameType = match.getLobby_type();
        if (gameType != -1) {
            holder.gameType.setText(context.getResources().getStringArray(R.array.lobby_types)[gameType]);
        }
        List<Player> players = match.getPlayers();
        boolean found = false;
        for (int i = 0; i < players.size() && !found; i++) {
            Player player = players.get(i);
            if (player.getAccount_id() == accountId) {
                found = true;
                Hero hero = heroService.getHeroById(context, player.getHero_id());
                if (hero != null) {
                    imageLoader.displayImage("assets://heroes/" + hero.getDotaId() + "/full.png", holder.heroImg, options);
                    holder.heroName.setText(hero.getLocalizedName());
                }
            }
        }
        return vi;
    }

    private class MatchHolder {
        ImageView heroImg;
        TextView gameStartTime;
        TextView heroName;
        TextView gameType;
    }
}
