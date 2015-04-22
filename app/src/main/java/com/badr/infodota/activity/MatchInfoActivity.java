package com.badr.infodota.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.adapter.pager.MatchInfoPagerAdapter;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.api.items.Item;
import com.badr.infodota.api.matchdetails.AdditionalUnit;
import com.badr.infodota.api.matchdetails.MatchDetails;
import com.badr.infodota.api.matchdetails.Player;
import com.badr.infodota.api.matchdetails.Result;
import com.badr.infodota.api.matchdetails.Team;
import com.badr.infodota.service.hero.HeroService;
import com.badr.infodota.service.item.ItemService;
import com.badr.infodota.service.match.MatchService;
import com.badr.infodota.service.player.PlayerService;
import com.badr.infodota.service.team.TeamService;
import com.badr.infodota.util.Utils;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.badr.infodota.view.SlidingTabLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 21.01.14
 * Time: 13:41
 */
public class MatchInfoActivity extends BaseActivity implements RequestListener {
    private String simpleMatchId;
    private MatchInfoPagerAdapter adapter;
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);
    BeanContainer container = BeanContainer.getInstance();
    TeamService teamService = container.getTeamService();
    ImageLoader imageLoader = ImageLoader.getInstance();

    private Result matchResult;
    private boolean initialized=false;
    @Override
    protected void onStart() {
        if(!spiceManager.isStarted()) {
            spiceManager.start(this);
            if(!initialized) {
                spiceManager.execute(new MatchDetailsLoadRequest(getApplicationContext(), matchResult, simpleMatchId), this);
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

    private MenuItem trackdotaItem;
    public static final int TRACKDOTA_GAME_ID=322;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        trackdotaItem = menu.add(1, TRACKDOTA_GAME_ID, 1, R.string.trackdota_match);
        MenuItemCompat.setShowAsAction(trackdotaItem, MenuItemCompat.SHOW_AS_ACTION_NEVER);
        trackdotaItem.setVisible(matchResult!=null&&((matchResult.getPicks_bans()!=null&& matchResult.getPicks_bans().size()>0)||matchResult.getRadiant_team_id()!=null||matchResult.getDire_team_id()!=null));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==trackdotaItem.getItemId()){
            Intent intent = new Intent(this, TrackdotaGameInfoActivity.class);
            intent.putExtra("id", simpleMatchId!=null?Long.valueOf(simpleMatchId):matchResult.getMatch_id());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_info);

        final Bundle intent = getIntent().getExtras();
        if (intent != null && (intent.containsKey("matchId") || intent.containsKey("match"))) {
            //accountId=intent.getLong("accountId");
            if (intent.containsKey("matchId")) {
                simpleMatchId = intent.getString("matchId");
            }
            if(intent.containsKey("match")){
                matchResult = (Result) intent.getSerializable("match");
            }

            initPager();
        }
    }

    private void initPager() {
        adapter = new MatchInfoPagerAdapter(getSupportFragmentManager(), this);

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        SlidingTabLayout indicator = (SlidingTabLayout) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        initialized=true;
        Toast.makeText(this, spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(Object o) {
        initialized=true;
        if(o instanceof LongPair){
            LongPair pair= (LongPair) o;
            Team team = new Team();
            if(pair.first.equals(matchResult.getRadiant_logo())) {
                team.setId(matchResult.getRadiant_team_id());
                team.setTeamLogoId(matchResult.getRadiant_logo());
            }
            else {
                team.setId(matchResult.getDire_team_id());
                team.setTeamLogoId(matchResult.getDire_logo());
            }
            team.setLogo(pair.second);
            teamService.saveTeam(MatchInfoActivity.this, team);
            imageLoader.loadImage(pair.second, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    drawable.setBounds(
                            0,
                            0,
                            Utils.dpSize(MatchInfoActivity.this, 40),
                            Utils.dpSize(MatchInfoActivity.this, 40));
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
        else if(o instanceof Result){
            matchResult = (Result) o;
            adapter.updateMatchDetails(matchResult);
            ActionBar actionBar = getSupportActionBar();
            if (matchResult.getRadiant_logo() != null) {
                Team radiant = teamService.getTeamById(MatchInfoActivity.this, matchResult.getRadiant_team_id());

                if (radiant!=null&&!TextUtils.isEmpty(radiant.getLogo())) {
                    imageLoader.loadImage(radiant.getLogo(), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                            drawable.setBounds(
                                    0,
                                    0,
                                    Utils.dpSize(MatchInfoActivity.this, 40),
                                    Utils.dpSize(MatchInfoActivity.this, 40));
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {
                        }
                    });
                } else {
                    spiceManager.execute(new TeamLogoLoadRequest(this, matchResult.getRadiant_logo()), this);
                }
            }
            if (matchResult.getDire_logo() != null) {
                Team dire = teamService.getTeamById(MatchInfoActivity.this, matchResult.getDire_team_id());
                if (dire!=null&&!TextUtils.isEmpty(dire.getLogo())) {
                    imageLoader.loadImage(dire.getLogo(), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                            drawable.setBounds(
                                    0,
                                    0,
                                    Utils.dpSize(MatchInfoActivity.this, 40),
                                    Utils.dpSize(MatchInfoActivity.this, 40));
										/*dire.setIcon(drawable);
										dire.setText("");*/
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });
                } else {
                    spiceManager.execute(new TeamLogoLoadRequest(this, matchResult.getDire_logo()), this);
                }
            }

            trackdotaItem.setVisible((matchResult.getPicks_bans()!=null&& matchResult.getPicks_bans().size()>0)||matchResult.getRadiant_team_id()!=null||matchResult.getDire_team_id()!=null);
            actionBar.setTitle(getString(
                    matchResult.isRadiant_win() ?
                            R.string.radiant_win
                            : R.string.dire_win));
        }
    }
    public static class MatchDetailsLoadRequest extends TaskRequest<Result>{

        private Result matchResult;
        private String matchId;
        private Context context;
        BeanContainer container=BeanContainer.getInstance();
        MatchService matchService = container.getMatchService();
        PlayerService playerService = container.getPlayerService();
        HeroService heroService=container.getHeroService();
        ItemService itemService=container.getItemService();
        public MatchDetailsLoadRequest(Context context,Result matchResult,String matchId) {
            super(Result.class);
            this.context=context;
            this.matchResult = matchResult;
            this.matchId=matchId;
        }

        @Override
        public Result loadData() throws Exception {
            if (matchId != null) {
                MatchDetails result = matchService.getMatchDetails(context, matchId);
                if (result != null) {
                    matchResult = result.getResult();
                }
            }
            if (matchResult != null) {
                List<Player> players = matchResult.getPlayers();
                if (players != null && players.size() > 0) {
                    List<Long> ids = new ArrayList<Long>();
                    for (Player player : players) {
                        if (player.getAccount_id() != Player.HIDDEN_ID) {
                            ids.add(player.getAccount_id());
                        }
                    }
                    if (ids.size() > 0) {
                        Unit.List unitsResult = playerService.loadAccounts(ids);
                        if (unitsResult != null &&unitsResult.size()>0) {
                            for (Unit unit : unitsResult) {
                                playerService.saveAccount(context, unit);
                            }
                            for (Player player : players) {
                                if (player.getAccount_id() != Player.HIDDEN_ID) {
                                    player.setAccount(playerService.getAccountById(context, player.getAccount_id()));
                                }
                                player.setHero(heroService.getHeroById(context, player.getHero_id()));
                                loadPlayerItems(player);
                            }
                        }
                    }
                }

            }
            return matchResult;
        }

        private void loadPlayerItems(Player player) {
            Item current=itemService.getItemById(context,player.getItem0());
            if(current!=null){
                player.setItem0dotaId(current.getDotaId());
            }
            current=itemService.getItemById(context,player.getItem1());
            if(current!=null){
                player.setItem1dotaId(current.getDotaId());
            }
            current=itemService.getItemById(context,player.getItem2());
            if(current!=null){
                player.setItem2dotaId(current.getDotaId());
            }
            current=itemService.getItemById(context,player.getItem3());
            if(current!=null){
                player.setItem3dotaId(current.getDotaId());
            }
            current=itemService.getItemById(context,player.getItem4());
            if(current!=null){
                player.setItem4dotaId(current.getDotaId());
            }
            current=itemService.getItemById(context,player.getItem5());
            if(current!=null){
                player.setItem5dotaId(current.getDotaId());
            }
            if(player.getAdditionalUnits()!=null){
                for(AdditionalUnit unit:player.getAdditionalUnits()){
                    current=itemService.getItemById(context,unit.getItem0());
                    if(current!=null){
                        unit.setItem0dotaId(current.getDotaId());
                    }
                    current=itemService.getItemById(context,unit.getItem1());
                    if(current!=null){
                        unit.setItem1dotaId(current.getDotaId());
                    }
                    current=itemService.getItemById(context,unit.getItem2());
                    if(current!=null){
                        unit.setItem2dotaId(current.getDotaId());
                    }
                    current=itemService.getItemById(context,unit.getItem3());
                    if(current!=null){
                        unit.setItem3dotaId(current.getDotaId());
                    }
                    current=itemService.getItemById(context,unit.getItem4());
                    if(current!=null){
                        unit.setItem4dotaId(current.getDotaId());
                    }
                    current=itemService.getItemById(context,unit.getItem5());
                    if(current!=null){
                        unit.setItem5dotaId(current.getDotaId());
                    }
                }
            }
        }
    }
    public static class TeamLogoLoadRequest extends TaskRequest<LongPair>{

        TeamService service = BeanContainer.getInstance().getTeamService();
        private Context context;
        private long teamId;
        public TeamLogoLoadRequest(Context context,long teamId) {
            super(LongPair.class);
            this.context=context;
            this.teamId=teamId;
        }

        @Override
        public LongPair loadData() throws Exception {
            String result= service.getTeamLogo(context, teamId);
            return new LongPair(teamId,result);
        }
    }
    public static class LongPair extends Pair<Long,String>{

        /**
         * Constructor for a Pair.
         *
         * @param first  the first object in the Pair
         * @param second the second object in the pair
         */
        public LongPair(Long first, String second) {
            super(first, second);
        }
    }
}
