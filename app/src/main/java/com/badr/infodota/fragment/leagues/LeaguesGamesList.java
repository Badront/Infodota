package com.badr.infodota.fragment.leagues;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.BaseActivity;
import com.badr.infodota.activity.LeagueGameActivity;
import com.badr.infodota.activity.ListHolderActivity;
import com.badr.infodota.adapter.LeaguesGamesAdapter;
import com.badr.infodota.api.joindota.MatchItem;
import com.badr.infodota.fragment.ListFragment;
import com.badr.infodota.service.joindota.JoinDotaService;
import com.badr.infodota.util.EndlessScrollListener;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 22.04.14
 * Time: 18:30
 */
public class LeaguesGamesList extends ListFragment {
    private String extraParams;
    private BeanContainer container = BeanContainer.getInstance();
    private JoinDotaService joinDotaService = container.getJoinDotaService();

    public LeaguesGamesList() {
    }

    public static LeaguesGamesList newInstance(String extraParams) {
        LeaguesGamesList fragment = new LeaguesGamesList();
        fragment.extraParams = extraParams;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setLayoutId(R.layout.leagues_games_list);
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
        getListView().setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadGames(page, totalItemsCount);
            }
        });
        loadGames(1, 0);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        MatchItem item = ((LeaguesGamesAdapter) getListAdapter()).getItem(position);
        if (!item.isSection()) {
            Intent intent = new Intent(getActivity(), LeagueGameActivity.class);
            intent.putExtra("matchItem", item);
            startActivity(intent);
        }
    }

    private void loadGames(final int page, final int totalItemsCount) {

        final BaseActivity activity = (BaseActivity) getActivity();
        setRefreshing(true);
        if (getListAdapter() == null) {
            setListAdapter(new LeaguesGamesAdapter(activity, null));
        }
        new LoaderProgressTask<Pair<List<MatchItem>, String>>(new ProgressTask<Pair<List<MatchItem>, String>>() {

            @Override
            public Pair<List<MatchItem>, String> doTask(OnPublishProgressListener listener) throws Exception {
                return joinDotaService.getMatchItems(activity, page, extraParams);
            }

            @Override
            public void doAfterTask(Pair<List<MatchItem>, String> result) {
                if (result.first != null) {
                    if (page == 1) {
                        setListAdapter(new LeaguesGamesAdapter(activity, result.first));
                    } else {
                        ((LeaguesGamesAdapter) getListAdapter()).addMatchItems(result.first);
                    }
                } else if (!TextUtils.isEmpty(result.second)) {
                    handleError(result.second);
                } else {
                    handleError(activity.getString(R.string.no_more_games));
                }
                setRefreshing(false);
            }

            @Override
            public void handleError(String error) {
                if (!TextUtils.isEmpty(error)) {
                    Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                }
                setRefreshing(false);
            }

            @Override
            public String getName() {
                return null;
            }
        }, null).execute();
    }

    @Override
    public void onRefresh() {
        loadGames(1, getListAdapter().getCount());
    }
}
