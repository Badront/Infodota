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
import com.badr.infodota.api.trackdota.game.EnhancedMatch;
import com.badr.infodota.api.trackdota.game.GamesResult;
import com.badr.infodota.api.trackdota.live.LiveGame;
import com.badr.infodota.fragment.ListFragment;
import com.badr.infodota.service.trackdota.TrackdotaService;
import com.badr.infodota.util.Refresher;
import com.badr.infodota.util.Updatable;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

/**
 * Created by ABadretdinov
 * 13.04.2015
 * 18:40
 */
public class LiveGamesList extends ListFragment implements Updatable<List<EnhancedMatch>> {
    private Refresher refresher;
    public static LiveGamesList newInstance(Refresher refresher){
        LiveGamesList fragment=new LiveGamesList();
        fragment.refresher=refresher;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setLayoutId(R.layout.pinned_section_list);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        setListAdapter(new LiveGamesAdapter(getActivity(), null));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onRefresh() {
        if(refresher!=null) {
            setRefreshing(true);
            refresher.onRefresh();
        }

    }
    @Override
    public void onUpdate(List<EnhancedMatch> entity) {
        setRefreshing(false);
        setListAdapter(new LiveGamesAdapter(getActivity(),entity));
    }
}
