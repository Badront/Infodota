package com.badr.infodota.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.api.matchdetails.Result;
import com.badr.infodota.api.matchdetails.Team;
import com.badr.infodota.api.matchhistory.Match;
import com.badr.infodota.service.team.TeamService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * User: ABadretdinov
 * Date: 15.05.14
 * Time: 17:12
 */
public class LeagueMatchResultsAdapter extends BaseAdapter implements PinnedSectionListAdapter {
    public SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, EEE");
    private Context context;
    private LayoutInflater inflater;
    private List<Match> matchItems;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private boolean isCanceled = false;

    public LeagueMatchResultsAdapter(Context context, List<Match> matchItems) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.matchItems = matchItems != null ? matchItems : new ArrayList<Match>();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.steam_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        timeFormat.setTimeZone(tz);
        //на всякий
        cal = Calendar.getInstance();
        tz = cal.getTimeZone();
        dateFormat.setTimeZone(tz);
        for (int i = 0; i < this.matchItems.size(); i++) {
            Match item = this.matchItems.get(i);
            Result possibleHeader = new Result();
            possibleHeader.setStart_time(item.getStart_time());
            possibleHeader.setSection(true);
            if (!this.matchItems.contains(possibleHeader)) {
                this.matchItems.add(i, possibleHeader);
            }
        }
    }
    public void addMatches(List<Match> subMatches) {
        if (subMatches != null) {
            matchItems.addAll(subMatches);
            Collections.sort(matchItems, new Comparator<Match>() {
                @Override
                public int compare(Match lhs, Match rhs) {
                    return (int) (rhs.getStart_time() - lhs.getStart_time());
                }
            });
            for (int i = 0; i < this.matchItems.size(); i++) {
                Match item = this.matchItems.get(i);
                Result possibleHeader = new Result();
                possibleHeader.setStart_time(item.getStart_time());
                possibleHeader.setSection(true);
                if (!this.matchItems.contains(possibleHeader)) {
                    this.matchItems.add(i, possibleHeader);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setResultMatch(Match match, Result result) {
        int position = matchItems.indexOf(match);
        if (position > -1) {
            matchItems.remove(position);
            matchItems.add(position, result);
            notifyDataSetChanged();
        }
    }

    public List<Match> getMatches() {
        return matchItems;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Match match = getItem(position);
        if ((match instanceof Result) && ((Result) match).isSection()) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getCount() {
        return matchItems.size();
    }

    @Override
    public Match getItem(int position) {
        return matchItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return matchItems.get(position).getMatch_id();
    }

    public void setCancel(boolean cancel) {
        isCanceled = cancel;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        Match item = getItem(position);
        if ((item instanceof Result) && ((Result) item).isSection()) {
            vi = inflater.inflate(R.layout.leagues_games_list_section, parent, false);
            TextView sectionHeader = (TextView) vi.findViewById(R.id.section_title);

            long timestamp = item.getStart_time();
            String localDate = dateFormat.format(new Date(timestamp * 1000));
            sectionHeader.setText(localDate);
        } else {
            final MatchItemHolder holder;

            if (vi == null) {
                vi = inflater.inflate(R.layout.league_match_row, parent, false);
                holder = new MatchItemHolder();
                holder.radiantLogo = (ImageView) vi.findViewById(R.id.radiant_logo);
                holder.radiantName = (TextView) vi.findViewById(R.id.radiant_name);
                holder.direLogo = (ImageView) vi.findViewById(R.id.dire_logo);
                holder.direName = (TextView) vi.findViewById(R.id.dire_name);
                holder.time = (TextView) vi.findViewById(R.id.game_start_time);
                vi.setTag(holder);
            } else {
                holder = (MatchItemHolder) vi.getTag();
            }

            long timestamp = item.getStart_time();
            String localTime = timeFormat.format(new Date(timestamp * 1000));
            holder.time.setText(localTime);
            if (item instanceof Result) {
                final Result matchResult = (Result) item;
                holder.radiantName.setText(matchResult.getRadiant_name());
                holder.direName.setText(matchResult.getDire_name());
                if (matchResult.isRadiant_win()) {
                    holder.radiantName.setTextColor(Color.GREEN);
                    holder.direName.setTextColor(Color.RED);
                } else {
                    holder.radiantName.setTextColor(Color.RED);
                    holder.direName.setTextColor(Color.GREEN);
                }

                TeamService teamService = BeanContainer.getInstance().getTeamService();
                Team radiant = teamService.getTeamById(context, matchResult.getRadiant_team_id());
                if (!TextUtils.isEmpty(radiant.getLogo())) {
                    imageLoader.displayImage(radiant.getLogo(), holder.radiantLogo, options);
                } else {
                    holder.radiantLogo.setImageResource(R.drawable.steam_default);
                }

                Team dire = teamService.getTeamById(context, matchResult.getDire_team_id());
                if (!TextUtils.isEmpty(dire.getLogo())) {
                    imageLoader.displayImage(dire.getLogo(), holder.direLogo, options);
                } else {
                    holder.direLogo.setImageResource(R.drawable.steam_default);
                }
            } else {
                holder.radiantName.setText("Radiant");
                holder.direName.setText("Dire");
                holder.radiantName.setTextColor(Color.WHITE);
                holder.direName.setTextColor(Color.WHITE);
                holder.radiantLogo.setImageResource(R.drawable.steam_default);
                holder.direLogo.setImageResource(R.drawable.steam_default);
            }
        }
        return vi;
    }

    public class MatchItemHolder {
        ImageView radiantLogo;
        TextView radiantName;
        ImageView direLogo;
        TextView direName;
        TextView time;
    }
}
