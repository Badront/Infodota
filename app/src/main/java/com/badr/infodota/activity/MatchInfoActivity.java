package com.badr.infodota.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.adapter.pager.MatchInfoPagerAdapter;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.api.matchdetails.MatchDetails;
import com.badr.infodota.api.matchdetails.Player;
import com.badr.infodota.api.matchdetails.Result;
import com.badr.infodota.api.matchdetails.Team;
import com.badr.infodota.service.match.MatchService;
import com.badr.infodota.service.player.PlayerService;
import com.badr.infodota.service.team.TeamService;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;
import com.badr.infodota.util.Utils;
import com.badr.infodota.view.SlidingTabLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 21.01.14
 * Time: 13:41
 */
public class MatchInfoActivity extends BaseActivity {
    private String simpleMatchId;
    private MatchInfoPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.match_info);
        setSupportProgressBarIndeterminateVisibility(false);

        Bundle intent = getIntent().getExtras();
        if (intent != null && (intent.containsKey("matchId") || intent.containsKey("match"))) {
            //accountId=intent.getLong("accountId");
            if (intent.containsKey("matchId")) {
                simpleMatchId = intent.getString("matchId");
            }

            initPager();
            setSupportProgressBarIndeterminateVisibility(true);
            new LoaderProgressTask<Pair<MatchDetails, String>>(new ProgressTask<Pair<MatchDetails, String>>() {
                BeanContainer container = BeanContainer.getInstance();
                TeamService teamService = container.getTeamService();
                ImageLoader imageLoader = ImageLoader.getInstance();

                @Override
                public Pair<MatchDetails, String> doTask(OnPublishProgressListener listener) throws Exception {
                    MatchService matchService = container.getMatchService();
                    PlayerService playerService = container.getPlayerService();

                    Pair<MatchDetails, String> result;
                    if (simpleMatchId != null) {
                        result = matchService.getMatchDetails(MatchInfoActivity.this, simpleMatchId);
                    } else {
                        Bundle intent = getIntent().getExtras();
                        result = Pair.create(new MatchDetails((Result) intent.getSerializable("match")), "");
                    }
                    if (result.first != null) {
                        Result matchResult = result.first.getResult();
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
                                    Pair<List<Unit>, String> unitsResult = playerService.loadAccounts(MatchInfoActivity.this, ids);
                                    if (unitsResult != null) {
                                        List<Unit> units = unitsResult.first;
                                        if (units != null && units.size() > 0) {
                                            for (Unit unit : units) {
                                                playerService.saveAccount(MatchInfoActivity.this, unit);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return result;
                }

                @Override
                public void doAfterTask(Pair<MatchDetails, String> result) {
                    setSupportProgressBarIndeterminateVisibility(false);
                    if (result.first != null && result.first.getResult() != null) {
                        final Result resultsResult = result.first.getResult();
                        adapter.updateMatchDetails(resultsResult);
                        ActionBar actionBar = getSupportActionBar();

                        /*SlidingTabLayout indicator = (SlidingTabLayout)findViewById(R.id.indicator);
                        View radiant=indicator.getChildAt(0);
						if(!TextUtils.isEmpty(resultsResult.getRadiant_name())){
							radiant.setText(resultsResult.getRadiant_name());
						}*/
                        if (resultsResult.getRadiant_logo() != null) {
                            Team radiant = teamService.getTeamById(MatchInfoActivity.this, resultsResult.getRadiant_team_id());

                            if (!TextUtils.isEmpty(radiant.getLogo())) {
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
                                        /*radiant.setIcon(drawable);
                                        radiant.setText("");*/
                                    }

                                    @Override
                                    public void onLoadingCancelled(String s, View view) {

                                    }
                                });
                            } else {
                                new LoaderProgressTask<Pair<String, String>>(new ProgressTask<Pair<String, String>>() {
                                    @Override
                                    public Pair<String, String> doTask(OnPublishProgressListener listener) throws Exception {
                                        return teamService.getTeamLogo(MatchInfoActivity.this, resultsResult.getRadiant_logo());
                                    }

                                    @Override
                                    public void doAfterTask(Pair<String, String> result) {
                                        if (result.first != null) {

                                            Team team = new Team();
                                            team.setId(resultsResult.getRadiant_team_id());
                                            team.setTeamLogoId(resultsResult.getRadiant_logo());
                                            team.setLogo(result.first);
                                            teamService.saveTeam(MatchInfoActivity.this, team);
                                            imageLoader.loadImage(result.first, new ImageLoadingListener() {
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
													/*radiant.setIcon(drawable);
													radiant.setText("");*/
                                                }

                                                @Override
                                                public void onLoadingCancelled(String s, View view) {

                                                }
                                            });
                                        } else {
                                            handleError(result.second);
                                        }
                                    }

                                    @Override
                                    public void handleError(String error) {
                                        //ignore
                                    }

                                    @Override
                                    public String getName() {
                                        return null;
                                    }
                                }, null).execute();
                            }
                        }
						/*final ActionBar.Tab dire=actionBar.getTabAt(1);
						if(!TextUtils.isEmpty(resultsResult.getDire_name())){
							dire.setText(resultsResult.getDire_name());
						}*/
                        if (resultsResult.getDire_logo() != null) {
                            Team dire = teamService.getTeamById(MatchInfoActivity.this, resultsResult.getDire_team_id());
                            if (!TextUtils.isEmpty(dire.getLogo())) {
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
                                new LoaderProgressTask<Pair<String, String>>(new ProgressTask<Pair<String, String>>() {
                                    @Override
                                    public Pair<String, String> doTask(OnPublishProgressListener listener) throws Exception {
                                        TeamService service = container.getTeamService();
                                        return service.getTeamLogo(MatchInfoActivity.this, resultsResult.getDire_logo());
                                    }

                                    @Override
                                    public void doAfterTask(Pair<String, String> result) {
                                        if (result.first != null) {
                                            Team team = new Team();
                                            team.setId(resultsResult.getDire_team_id());
                                            team.setTeamLogoId(resultsResult.getDire_logo());
                                            team.setLogo(result.first);
                                            teamService.saveTeam(MatchInfoActivity.this, team);
                                            imageLoader.loadImage(result.first, new ImageLoadingListener() {
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
                                            handleError(result.second);
                                        }
                                    }

                                    @Override
                                    public void handleError(String error) {
                                        //ignore
                                    }

                                    @Override
                                    public String getName() {
                                        return null;
                                    }
                                }, null).execute();
                            }
                        }
                        actionBar.setTitle(getString(
                                resultsResult.isRadiant_win() ?
                                        R.string.radiant_win
                                        : R.string.dire_win));
                    } else if (!TextUtils.isEmpty(result.second)) {
                        handleError(result.second);
                    }
                }

                @Override
                public void handleError(String error) {
                    Toast.makeText(MatchInfoActivity.this, error, Toast.LENGTH_LONG).show();
                }

                @Override
                public String getName() {
                    return null;
                }
            }, null).execute();
        }
    }

    private void initPager() {
        adapter = new MatchInfoPagerAdapter(getSupportFragmentManager(), this/*,simpleMatch*/);

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        SlidingTabLayout indicator = (SlidingTabLayout) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }
}
