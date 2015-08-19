package com.badr.infodota.fragment.trackdota;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.TrackdotaLeagueInfoActivity;
import com.badr.infodota.adapter.TrackdotaLeagueAdapter;
import com.badr.infodota.adapter.holder.TrackdotaLeagueHolder;
import com.badr.infodota.api.trackdota.LeaguesResult;
import com.badr.infodota.api.trackdota.game.League;
import com.badr.infodota.fragment.RecyclerFragment;
import com.badr.infodota.service.trackdota.TrackdotaService;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by ABadretdinov
 * 08.06.2015
 * 12:13
 */
public class LeaguesList extends RecyclerFragment<League,TrackdotaLeagueHolder> implements RequestListener<LeaguesResult> {

    private SpiceManager mSpiceManager = new SpiceManager(UncachedSpiceService.class);

    @Override
    public void onStart(){
        if (!mSpiceManager.isStarted()) {
            mSpiceManager.start(getActivity());
            onRefresh();
        }
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
        super.onDestroy();
    }
    @Override
    public RecyclerView.LayoutManager getLayoutManager(Context context) {
        return new GridLayoutManager(context,1);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setColumnSize();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setColumnSize();
    }

    private void setColumnSize() {
        if(getRecyclerView()!=null) {
            if (getResources().getBoolean(R.bool.is_tablet)) {
                ((GridLayoutManager) getRecyclerView().getLayoutManager()).setSpanCount(2);
            } else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    ((GridLayoutManager) getRecyclerView().getLayoutManager()).setSpanCount(2);
                } else {
                    ((GridLayoutManager) getRecyclerView().getLayoutManager()).setSpanCount(1);
                }
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        League entity=getAdapter().getItem(position);
        Intent intent=new Intent(getActivity(), TrackdotaLeagueInfoActivity.class);
        intent.putExtra("id",entity.getId());
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        setRefreshing(true);
        mSpiceManager.execute(new LeagueLoadRequest(), this);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        setRefreshing(false);
        Toast.makeText(getActivity(), spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(LeaguesResult leaguesResult) {
        setRefreshing(false);
        Context context=getActivity();
        if(leaguesResult!=null&&context!=null){
            setAdapter(new TrackdotaLeagueAdapter(context,leaguesResult.getLeagues()));
        }
    }

    public class LeagueLoadRequest extends TaskRequest<LeaguesResult>{

        public LeagueLoadRequest() {
            super(LeaguesResult.class);
        }

        @Override
        public LeaguesResult loadData() throws Exception {
            BeanContainer container=BeanContainer.getInstance();
            TrackdotaService trackdotaService=container.getTrackdotaService();
            return trackdotaService.getLeagues();
        }
    }
}
