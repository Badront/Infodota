package com.badr.infodota.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.adapter.pager.TrackdotaGamePagerAdapter;
import com.badr.infodota.api.trackdota.core.CoreResult;
import com.badr.infodota.api.trackdota.live.LiveGame;
import com.badr.infodota.service.trackdota.TrackdotaService;
import com.badr.infodota.util.Refresher;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.badr.infodota.view.SlidingTabLayout;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by ABadretdinov
 * 14.04.2015
 * 14:11
 */
public class TrackdotaGameInfoActivity extends BaseActivity implements Refresher, RequestListener {

    private CoreResult coreResult;
    private LiveGame liveGame;
    private long matchId;
    private TrackdotaGamePagerAdapter adapter;
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

    @Override
    protected void onStart() {
        super.onStart();
        if(!spiceManager.isStarted()) {
            spiceManager.start(this);
            onRefresh();
        }
    }

    @Override
    protected void onStop() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trackdota_game_info);

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
        spiceManager.execute(new CoreGameLoadRequest(matchId), this);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(this, spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(Object object) {
        if (object instanceof CoreResult) {
            coreResult = (CoreResult) object;
            spiceManager.execute(new LiveGameLoadRequest(matchId), this);
        } else if (object instanceof LiveGame) {
            liveGame = (LiveGame) object;
            adapter.update(coreResult, liveGame);
        } else if(object==null){
            adapter.update(coreResult,liveGame);
        }
    }

    public static class CoreGameLoadRequest extends TaskRequest<CoreResult> {
        private BeanContainer container = BeanContainer.getInstance();
        private TrackdotaService trackdotaService = container.getTrackdotaService();
        private long matchId;

        public CoreGameLoadRequest(long matchId) {
            super(CoreResult.class);
            this.matchId = matchId;
        }

        @Override
        public CoreResult loadData() throws Exception {
            return trackdotaService.getGameCoreData(matchId);
        }
    }

    public static class LiveGameLoadRequest extends TaskRequest<LiveGame> {
        private BeanContainer container = BeanContainer.getInstance();
        private TrackdotaService trackdotaService = container.getTrackdotaService();
        private long matchId;

        public LiveGameLoadRequest(long matchId) {
            super(LiveGame.class);
            this.matchId = matchId;
        }

        @Override
        public LiveGame loadData() throws Exception {
            return trackdotaService.getLiveGame(matchId);
        }
    }
}
