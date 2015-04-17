package com.badr.infodota.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.adapter.pager.TrackdotaGamePagerAdapter;
import com.badr.infodota.api.trackdota.GameManager;
import com.badr.infodota.api.trackdota.TrackdotaUtils;
import com.badr.infodota.api.trackdota.core.CoreResult;
import com.badr.infodota.api.trackdota.game.Team;
import com.badr.infodota.api.trackdota.live.LiveGame;
import com.badr.infodota.api.trackdota.live.LiveTeam;
import com.badr.infodota.service.trackdota.TrackdotaService;
import com.badr.infodota.util.Refresher;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.badr.infodota.view.SlidingTabLayout;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.text.MessageFormat;

/**
 * Created by ABadretdinov
 * 14.04.2015
 * 14:11
 */
public class TrackdotaGameInfoActivity extends BaseActivity implements Refresher, RequestListener {

    private static final long DELAY_20_SEC = 20000;
    private CoreResult coreResult;
    private LiveGame liveGame;
    private long matchId;
    private TrackdotaGamePagerAdapter adapter;
    private View progressBar;
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);
    private Handler updateHandler=new Handler();
    private Runnable updateTask;
    private GameManager mGameManager=GameManager.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
        if(!spiceManager.isStarted()) {
            spiceManager.start(this);
        }
        if(liveGame==null||liveGame.getStatus()<4) {
            onRefresh();
        }
    }

    @Override
    protected void onStop() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        cancelDelayedUpdate();
        super.onStop();
    }

    private void cancelDelayedUpdate() {
        if(updateTask!=null) {
            updateHandler.removeCallbacks(updateTask);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trackdota_game_info);
        progressBar=findViewById(R.id.progressBar);
        Bundle intent = getIntent().getExtras();
        if (intent != null && intent.containsKey("id")) {
            matchId = intent.getLong("id");
            adapter = new TrackdotaGamePagerAdapter(this, getSupportFragmentManager(), this, coreResult, liveGame);

            final ViewPager pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(adapter);
            pager.setOffscreenPageLimit(2);
            SlidingTabLayout indicator = (SlidingTabLayout) findViewById(R.id.indicator);
            indicator.setViewPager(pager);
        }
    }

    @Override
    public void onRefresh() {
        cancelDelayedUpdate();
        progressBar.setVisibility(View.VISIBLE);
        spiceManager.execute(new CoreGameLoadRequest(this, matchId), this);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(Object object) {
        if (object instanceof CoreResult) {
            coreResult = (CoreResult) object;
            spiceManager.execute(new LiveGameLoadRequest(this,matchId), this);
        } else if (object instanceof LiveGame) {
            liveGame = (LiveGame) object;
            LiveTeam radiantLive=liveGame.getRadiant();
            Team radiant=coreResult.getRadiant();
            LiveTeam direLive=liveGame.getDire();
            Team dire=coreResult.getDire();
            if(radiant!=null&&dire!=null&&radiantLive!=null&&direLive!=null){
                getSupportActionBar().setTitle(
                        MessageFormat.format(
                                "{0}:{1} - {2}:{3}",
                                TrackdotaUtils.getTeamTag(radiant,TrackdotaUtils.RADIANT),
                                radiantLive.getScore(),
                                TrackdotaUtils.getTeamTag(dire,TrackdotaUtils.DIRE),
                                direLive.getScore())
                );
            }
            progressBar.setVisibility(View.GONE);
            adapter.update(coreResult, liveGame);
            if(liveGame.getStatus()<4)
            {
                startDelayedUpdate();
            }
        } else if(object==null){
            progressBar.setVisibility(View.GONE);
            adapter.update(coreResult,liveGame);
        }
    }

    private void startDelayedUpdate() {
        cancelDelayedUpdate();
        updateTask=new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        };
        updateHandler.postDelayed(updateTask,DELAY_20_SEC);
    }

    public static class CoreGameLoadRequest extends TaskRequest<CoreResult> {
        private BeanContainer container = BeanContainer.getInstance();
        private TrackdotaService trackdotaService = container.getTrackdotaService();
        private long matchId;
        private Context context;

        public CoreGameLoadRequest(Context context,long matchId) {
            super(CoreResult.class);
            this.matchId = matchId;
            this.context=context;
        }

        @Override
        public CoreResult loadData() throws Exception {
            return trackdotaService.getGameCoreData(context,matchId);
        }
    }

    public static class LiveGameLoadRequest extends TaskRequest<LiveGame> {
        private BeanContainer container = BeanContainer.getInstance();
        private TrackdotaService trackdotaService = container.getTrackdotaService();
        private long matchId;
        private Context context;

        public LiveGameLoadRequest(Context context,long matchId) {
            super(LiveGame.class);
            this.context=context;
            this.matchId = matchId;
        }

        @Override
        public LiveGame loadData() throws Exception {
            return trackdotaService.getLiveGame(context,matchId);
        }
    }
}
