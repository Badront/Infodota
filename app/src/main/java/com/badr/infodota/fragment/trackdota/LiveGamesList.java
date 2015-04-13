package com.badr.infodota.fragment.trackdota;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.ListHolderActivity;
import com.badr.infodota.adapter.LeaguesGamesAdapter;
import com.badr.infodota.adapter.LiveGamesAdapter;
import com.badr.infodota.api.trackdota.game.GamesResult;
import com.badr.infodota.api.trackdota.live.LiveGame;
import com.badr.infodota.fragment.ListFragment;
import com.badr.infodota.service.trackdota.TrackdotaService;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by ABadretdinov
 * 13.04.2015
 * 18:40
 */
public class LiveGamesList extends ListFragment implements RequestListener<GamesResult>{
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setLayoutId(R.layout.pinned_section_list);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        ActionMenuView actionMenuView = ((ListHolderActivity) getActivity()).getActionMenuView();
        Menu actionMenu = actionMenuView.getMenu();
        actionMenu.clear();
        actionMenuView.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        setListAdapter(new LiveGamesAdapter(getActivity(), null));
        onRefresh();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onRefresh() {
        setRefreshing(true);
        spiceManager.execute(new GamesResultLoadRequest(),this);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        setRefreshing(false);
        Toast.makeText(getActivity(),spiceException.getLocalizedMessage(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(GamesResult gamesResult) {
        setRefreshing(false);
        if(gamesResult!=null)
        {
            setListAdapter(new LiveGamesAdapter(getActivity(),gamesResult.getEnhancedMatches()));
        }
    }

    public class GamesResultLoadRequest extends TaskRequest<GamesResult>{
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
