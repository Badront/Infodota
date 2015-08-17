package com.badr.infodota.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.api.joindota.LiveStream;
import com.badr.infodota.api.joindota.MatchItem;
import com.badr.infodota.api.joindota.SubmatchItem;
import com.badr.infodota.service.hero.HeroService;
import com.badr.infodota.service.joindota.JoinDotaService;
import com.badr.infodota.util.DateUtils;
import com.badr.infodota.util.GrayImageLoadListener;
import com.badr.infodota.util.Utils;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.badr.infodota.view.FlowLayout;
import com.bumptech.glide.Glide;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 22.04.14
 * Time: 18:58
 */
public class LeagueGameActivity extends BaseActivity implements RequestListener {
    public static final int ADD_CALENDAR_EVENT_ID = 4321;
    View progressBar;
    private MatchItem matchItem;
    private Menu menu;
    private Button showStreams;
    private LinearLayout streamsHolder;
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);
    private boolean initialized=false;
    @Override
    protected void onStart() {
        if(!spiceManager.isStarted()) {
            spiceManager.start(this);
            if(!initialized) {
                initMatch();
            }
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuItem refresh = menu.add(1, 1001, 1, R.string.refresh);
        refresh.setIcon(R.drawable.ic_menu_refresh);
        MenuItemCompat.setShowAsAction(refresh, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1001) {
            reloadMatchDetails();
            return true;
        } else if (item.getItemId() == ADD_CALENDAR_EVENT_ID) {
            createCalendarEvent();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.league_game_info);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("matchItem")) {
            matchItem = (MatchItem) bundle.get("matchItem");
        } else {
            finish();
        }
    }

    private void initMatch() {
        ((TextView) findViewById(R.id.team1)).setText(matchItem.getTeam1name());
        ((TextView) findViewById(R.id.team2)).setText(matchItem.getTeam2name());
        Glide.with(this).load(matchItem.getTeam1flagLink()).into((ImageView) findViewById(R.id.flag1)).onLoadStarted(getResources().getDrawable(R.drawable.flag_default));
        Glide.with(this).load(matchItem.getTeam2flagLink()).into((ImageView) findViewById(R.id.flag2)).onLoadStarted(getResources().getDrawable(R.drawable.flag_default));
        progressBar = findViewById(R.id.progressBar);
        showStreams = (Button) findViewById(R.id.show_streams);
        streamsHolder = (LinearLayout) findViewById(R.id.streams_holder);
        reloadMatchDetails();
    }

    private void reloadMatchDetails() {
        progressBar.setVisibility(View.VISIBLE);
        showStreams.setVisibility(View.GONE);
        streamsHolder.setVisibility(View.GONE);
        spiceManager.execute(new MatchItemLoadRequest(matchItem),this);
    }

    private void loadStreams() {
        progressBar.setVisibility(View.VISIBLE);
        spiceManager.execute(new ChannelLoadRequest(matchItem.getStreams()), this);
    }

    private void fillGameInfo() {
        ((TextView) findViewById(R.id.text_title)).setText(matchItem.getTitle());
        getSupportActionBar().setTitle(matchItem.getTitle());
        ((TextView) findViewById(R.id.middle_text)).setText(matchItem.getMiddleText());
        MenuItem calendar = menu.findItem(ADD_CALENDAR_EVENT_ID);
        if (matchItem.getDetailedDate() != null) {
            ((TextView) findViewById(R.id.text_detailed_date)).setText(DateUtils.DATE_TIME_FORMAT.format(matchItem.getDetailedDate()));
            if (calendar == null) {
                calendar = menu.add(0, ADD_CALENDAR_EVENT_ID, 1, R.string.add_calendar_event);
                calendar.setIcon(R.drawable.ic_menu_calendar);
                MenuItemCompat.setShowAsAction(calendar, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            }
        } else {
            if (calendar != null) {
                menu.removeItem(ADD_CALENDAR_EVENT_ID);
            }
            ((TextView) findViewById(R.id.text_detailed_date)).setText("TBA");
        }
        Glide.with(this).load(matchItem.getTeam1logoLink()).into((ImageView) findViewById(R.id.logo1)).onLoadStarted(getResources().getDrawable(R.drawable.flag_default));
        Glide.with(this).load(matchItem.getTeam2logoLink()).into((ImageView) findViewById(R.id.logo2)).onLoadStarted(getResources().getDrawable(R.drawable.flag_default));
        HeroService heroService = BeanContainer.getInstance().getHeroService();
        LinearLayout holder = (LinearLayout) findViewById(R.id.games_holder);
        holder.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        for (SubmatchItem submatchItem : matchItem.getSubmatches()) {
            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.league_game_submatch, null, false);
            FlowLayout team1bans = (FlowLayout) view.findViewById(R.id.bans1);
            for (String heroName : submatchItem.getTeam1bans()) {
                List<Hero> heroes = heroService.getHeroesByName(this, heroName);
                if (heroes != null && heroes.size() > 0) {
                    Hero hero = heroes.get(0);
                    LinearLayout imageLayout = (LinearLayout) inflater.inflate(R.layout.image_holder, team1bans, false);
                    imageLayout.setOnClickListener(new HeroInfoActivity.OnDotaHeroClickListener(hero.getId()));
                    team1bans.addView(imageLayout);
                    Glide.with(this).load(matchItem.getTeam2logoLink()).into(new GrayImageLoadListener((ImageView) imageLayout.findViewById(R.id.img))).onLoadStarted(getResources().getDrawable(R.drawable.emptyitembg));
                }
            }
            FlowLayout team2bans = (FlowLayout) view.findViewById(R.id.bans2);
            for (String heroName : submatchItem.getTeam2bans()) {
                List<Hero> heroes = heroService.getHeroesByName(this, heroName);
                if (heroes != null && heroes.size() > 0) {
                    Hero hero = heroes.get(0);
                    LinearLayout imageLayout = (LinearLayout) inflater.inflate(R.layout.image_holder, team2bans, false);
                    imageLayout.setOnClickListener(new HeroInfoActivity.OnDotaHeroClickListener(hero.getId()));
                    team2bans.addView(imageLayout);
                    ImageView imageView = (ImageView) imageLayout.findViewById(R.id.img);
                    Glide
                            .with(this)
                            .load(Utils.getHeroFullImage(hero.getDotaId()))
                            .into(new GrayImageLoadListener(imageView))
                            .onLoadStarted(getResources()
                                    .getDrawable(R.drawable.emptyitembg));
                }
            }
            LinearLayout team1 = (LinearLayout) view.findViewById(R.id.team1);
            LinearLayout team2 = (LinearLayout) view.findViewById(R.id.team2);
            int size = submatchItem.getTeam1picks().size();
            for (int i = 0; i < size; i++) {
                LinearLayout team1HeroHolder = (LinearLayout) inflater.inflate(R.layout.image_text_row_left, team1, false);
                LinearLayout team2HeroHolder = (LinearLayout) inflater.inflate(R.layout.image_text_row_right, team2, false);
                ((TextView) team1HeroHolder.findViewById(R.id.text)).setText(submatchItem.getTeam1playerNames().get(i));
                ((TextView) team2HeroHolder.findViewById(R.id.text)).setText(submatchItem.getTeam2playerNames().get(i));
                List<Hero> heroes = heroService.getHeroesByName(this, submatchItem.getTeam1picks().get(i));
                if (heroes != null && heroes.size() > 0) {
                    Hero hero = heroes.get(0);
                    ImageView imageView = (ImageView) team1HeroHolder.findViewById(R.id.img);
                    Glide.with(this).load(Utils.getHeroFullImage(hero.getDotaId())).into(imageView).onLoadStarted(getResources().getDrawable(R.drawable.emptyitembg));
                    imageView.setOnClickListener(new HeroInfoActivity.OnDotaHeroClickListener(hero.getId()));
                }
                heroes = heroService.getHeroesByName(this, submatchItem.getTeam2picks().get(i));
                if (heroes != null && heroes.size() > 0) {
                    Hero hero = heroes.get(0);
                    ImageView imageView = (ImageView) team2HeroHolder.findViewById(R.id.img);
                    Glide.with(this).load(Utils.getHeroFullImage(hero.getDotaId())).into(imageView).onLoadStarted(getResources().getDrawable(R.drawable.emptyitembg));
                    imageView.setOnClickListener(new HeroInfoActivity.OnDotaHeroClickListener(hero.getId()));
                }
                team1.addView(team1HeroHolder);
                team2.addView(team2HeroHolder);
            }
            ((TextView) view.findViewById(R.id.match_title)).setText(submatchItem.getScore());
            holder.addView(view);
        }
    }

    private void createCalendarEvent() {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, matchItem.getDetailedDate())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, matchItem.getDetailedDate())
                .putExtra(CalendarContract.Events.TITLE, matchItem.getTitle() + ", " + matchItem.getTeam1name() + " vs " + matchItem.getTeam2name());
        startActivity(intent);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        initialized=true;
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(Object object) {
        initialized=true;
        progressBar.setVisibility(View.GONE);
        if(object instanceof String){
            String result= (String) object;
            if (!TextUtils.isEmpty(result)) {
                onRequestFailure(new SpiceException(result));
            } else if (matchItem.getStreams() != null && matchItem.getStreams().size() > 0) {
                showStreams.setVisibility(View.VISIBLE);
                showStreams.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (streamsHolder.getVisibility() == View.VISIBLE) {
                            streamsHolder.setVisibility(View.GONE);
                        } else {
                            streamsHolder.setVisibility(View.VISIBLE);
                            streamsHolder.removeAllViews();
                            if (matchItem.getStreams() != null) {
                                LayoutInflater inflater = getLayoutInflater();
                                for (final LiveStream stream : matchItem.getStreams()) {
                                    View streamRow = inflater.inflate(R.layout.league_game_stream_row, streamsHolder, false);
                                    TextView name = (TextView) streamRow.findViewById(R.id.name);
                                    name.setText(stream.getName());
                                    TextView language = (TextView) streamRow.findViewById(R.id.language);
                                    language.setText(stream.getLanguage());
                                    TextView viewersStatus = (TextView) streamRow.findViewById(R.id.viewers);
                                    if (TextUtils.isEmpty(stream.getViewers())) {
                                        viewersStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                        viewersStatus.setText(stream.getStatus());
                                    } else {
                                        viewersStatus.setText(stream.getViewers());
                                        streamRow.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (TextUtils.isEmpty(stream.getChannelName())) {
                                                    Intent inBrowser = new Intent(Intent.ACTION_VIEW);
                                                    inBrowser.setData(Uri.parse(stream.getUrl()));
                                                    startActivity(inBrowser);
                                                } else {
                                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LeagueGameActivity.this);
                                                    Intent intent;
                                                    switch (preferences.getInt("player_type", 0)) {
                                                        case 0:
                                                            intent = new Intent(LeagueGameActivity.this, TwitchPlayActivity.class);
                                                            intent.putExtra("channelName", stream.getChannelName());
                                                            intent.putExtra("channelTitle", matchItem.getTitle());
                                                            break;
                                                        default:
                                                            String url = "http://www.twitch.tv/" + stream.getChannelName();
                                                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                            break;
                                                    }
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                    }
                                    streamsHolder.addView(streamRow);
                                }
                            }
                        }
                    }
                });
            }
        }
        else if(object instanceof MatchItem){
            matchItem = (MatchItem) object;
            loadStreams();
            fillGameInfo();
        }
    }

    public static class ChannelLoadRequest extends TaskRequest<String>{
        private List<LiveStream> liveStreams;
        private BeanContainer container = BeanContainer.getInstance();
        private JoinDotaService service = container.getJoinDotaService();
        public ChannelLoadRequest(List<LiveStream> liveStreams) {
            super(String.class);
            this.liveStreams=liveStreams;
        }

        @Override
        public String loadData() throws Exception {
            return service.fillChannelName(liveStreams);
        }
    }
    public static class MatchItemLoadRequest extends TaskRequest<MatchItem>{
        JoinDotaService service=BeanContainer.getInstance().getJoinDotaService();
        private MatchItem matchItem;
        public MatchItemLoadRequest(MatchItem matchItem) {
            super(MatchItem.class);
            this.matchItem=matchItem;
        }

        @Override
        public MatchItem loadData() throws Exception {
            return service.updateMatchItem(matchItem);
        }
    }
}
