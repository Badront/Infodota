package com.badr.infodota.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.adapter.pager.MatchInfoPagerAdapter;
import com.badr.infodota.api.matchdetails.Result;
import com.badr.infodota.api.matchdetails.Team;
import com.badr.infodota.service.team.TeamService;
import com.badr.infodota.task.MatchDetailsLoadRequest;
import com.badr.infodota.task.TeamLogoLoadRequest;
import com.badr.infodota.util.LongPair;
import com.badr.infodota.view.SlidingTabLayout;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * User: ABadretdinov
 * Date: 21.01.14
 * Time: 13:41
 */
public class MatchInfoActivity extends BaseActivity implements RequestListener {
    public static final int TRACKDOTA_GAME_ID = 322;
    BeanContainer container = BeanContainer.getInstance();
    TeamService teamService = container.getTeamService();
    private String simpleMatchId;
    private MatchInfoPagerAdapter adapter;
    private SpiceManager mSpiceManager = new SpiceManager(UncachedSpiceService.class);
    private Result matchResult;
    private MenuItem trackdotaItem;

    @Override
    protected void onStart() {
        if (!mSpiceManager.isStarted()) {
            mSpiceManager.start(this);
            mSpiceManager.execute(new MatchDetailsLoadRequest(getApplicationContext(), matchResult, simpleMatchId), this);
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        trackdotaItem = menu.add(1, TRACKDOTA_GAME_ID, 1, R.string.trackdota_match);
        MenuItemCompat.setShowAsAction(trackdotaItem, MenuItemCompat.SHOW_AS_ACTION_NEVER);
        trackdotaItem.setVisible(matchResult!=null&&((matchResult.getPicks_bans()!=null&& matchResult.getPicks_bans().size()>0)||matchResult.getRadiantTeamId()!=null||matchResult.getDireTeamId()!=null));
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
        Toast.makeText(this, spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(Object o) {
        if(o instanceof LongPair){
            LongPair pair= (LongPair) o;
            Team team = new Team();
            if(pair.first.equals(matchResult.getRadiantLogo())) {
                team.setId(matchResult.getRadiantTeamId());
                team.setTeamLogoId(matchResult.getRadiantLogo());
            }
            else {
                team.setId(matchResult.getDireTeamId());
                team.setTeamLogoId(matchResult.getDireLogo());
            }
            team.setLogo(pair.second);
            teamService.saveTeam(MatchInfoActivity.this, team);
           /*
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
            });*/
        }
        else if(o instanceof Result){
            matchResult = (Result) o;
            adapter.updateMatchDetails(matchResult);
            ActionBar actionBar = getSupportActionBar();
            if (matchResult.getRadiantLogo() != null) {
                Team radiant = teamService.getTeamById(MatchInfoActivity.this, matchResult.getRadiantTeamId());

                if (radiant!=null&&!TextUtils.isEmpty(radiant.getLogo())) {
                    /*imageLoader.loadImage(radiant.getLogo(), new ImageLoadingListener() {
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
                    });*/
                } else {
                    mSpiceManager.execute(new TeamLogoLoadRequest(this, matchResult.getRadiantLogo()), this);
                }
            }
            if (matchResult.getDireLogo() != null) {
                Team dire = teamService.getTeamById(MatchInfoActivity.this, matchResult.getDireTeamId());
                if (dire!=null&&!TextUtils.isEmpty(dire.getLogo())) {
                    /*Glide.with(this).load(dire.getLogo()).into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            resource.setBounds(
                                    0,
                                    0,
                                    Utils.dpSize(MatchInfoActivity.this, 40),
                                    Utils.dpSize(MatchInfoActivity.this, 40));

                        }
                    });*/
                    /*imageLoader.loadImage(dire.getLogo(), new ImageLoadingListener() {
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
										*//*dire.setIcon(drawable);
										dire.setText("");*//*
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });*/
                } else {
                    mSpiceManager.execute(new TeamLogoLoadRequest(getApplicationContext(), matchResult.getDireLogo()), this);
                }
            }

            trackdotaItem.setVisible((matchResult.getPicks_bans()!=null&& matchResult.getPicks_bans().size()>0)||matchResult.getRadiantTeamId()!=null||matchResult.getDireTeamId()!=null);
            actionBar.setTitle(getString(
                    matchResult.isRadiantWin() ?
                            R.string.radiant_win
                            : R.string.dire_win));
        }
    }


}
