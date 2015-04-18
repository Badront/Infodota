package com.badr.infodota.fragment.trackdota;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.ListHolderActivity;
import com.badr.infodota.adapter.pager.TrackdotaPagerAdapter;
import com.badr.infodota.api.trackdota.game.GamesResult;
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
 * 11:28
 */
public class TrackdotaMain extends Fragment implements RequestListener<GamesResult>,Refresher {
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);
    private TrackdotaPagerAdapter adapter;
    private View progressBar;
    private Handler updateHandler=new Handler();
    private Runnable updateTask;
    private static final long DELAY_20_SEC = 20000;
    private boolean initialized=false;

    @Override
    public void onStart() {
        if(!spiceManager.isStarted()) {
            spiceManager.start(getActivity());
            if(!initialized){
                onRefresh();
            }
            else {
                startDelayedUpdate();
            }
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        cancelDelayedUpdate();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        initialized=false;
        super.onDestroy();
    }

    private void cancelDelayedUpdate() {
        if(updateTask!=null) {
            updateHandler.removeCallbacks(updateTask);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.trackdota,container,false);
        progressBar=view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        ((ListHolderActivity) getActivity()).getActionMenuView().setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        initPager();
    }

    private void initPager() {
        View root=getView();
        Activity activity=getActivity();
        if(activity!=null&&root!=null) {
            adapter = new TrackdotaPagerAdapter(activity,getChildFragmentManager(),this);

            ViewPager pager = (ViewPager) root.findViewById(R.id.pager);
            pager.setAdapter(adapter);
            pager.setOffscreenPageLimit(3);

            SlidingTabLayout indicator = (SlidingTabLayout) root.findViewById(R.id.indicator);
            indicator.setViewPager(pager);
        }
    }

    @Override
    public void onRefresh(){
        cancelDelayedUpdate();
        progressBar.setVisibility(View.VISIBLE);
        spiceManager.execute(new GamesResultLoadRequest(),this);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        initialized=true;
        progressBar.setVisibility(View.GONE);
        adapter.update(null);
        Toast.makeText(getActivity(), spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(GamesResult gamesResult) {
        initialized=true;
        progressBar.setVisibility(View.GONE);
        adapter.update(gamesResult);
        startDelayedUpdate();
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

    public class GamesResultLoadRequest extends TaskRequest<GamesResult> {
        private BeanContainer container=BeanContainer.getInstance();
        private TrackdotaService trackdotaService=container.getTrackdotaService();
        public GamesResultLoadRequest() {
            super(GamesResult.class);
        }

        @Override
        public GamesResult loadData() throws Exception {
            return trackdotaService.getGames();
        }
    }

}
