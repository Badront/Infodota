package com.badr.infodota.fragment.player.details;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.BaseActivity;
import com.badr.infodota.activity.MatchInfoActivity;
import com.badr.infodota.adapter.HeroesAutoCompleteAdapter;
import com.badr.infodota.adapter.MatchAdapter;
import com.badr.infodota.api.Constants;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.api.matchhistory.Match;
import com.badr.infodota.api.matchhistory.MatchHistoryResultResponse;
import com.badr.infodota.api.matchhistory.Result;
import com.badr.infodota.fragment.GridFragment;
import com.badr.infodota.service.hero.HeroService;
import com.badr.infodota.service.match.MatchService;
import com.badr.infodota.util.EndlessScrollListener;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 18.02.14
 * Time: 16:46
 */
public class MatchHistory extends GridFragment {
    HeroService heroService = BeanContainer.getInstance().getHeroService();
    private Unit account;
    private long total = 1;
    private Long heroId = null;
    private AutoCompleteTextView heroView;
    private BeanContainer container = BeanContainer.getInstance();
    private MatchService matchService = container.getMatchService();

    public static MatchHistory newInstance(Unit account) {
        MatchHistory fragment = new MatchHistory();
        fragment.account = account;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setLayoutId(R.layout.match_history);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View root = getView();
        if (root == null) {
            return;
        }
        setColumnSize();
        Activity activity = getActivity();
        heroView = (AutoCompleteTextView) root.findViewById(R.id.hero_search);
        List<Hero> heroes = heroService.getAllHeroes(activity);
        heroView.setAdapter(new HeroesAutoCompleteAdapter(activity, heroes));
        heroView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hero hero = ((HeroesAutoCompleteAdapter) heroView.getAdapter()).getItem(position);
                heroId = hero.getId();
                heroView.setText(hero.getLocalizedName());
                loadHistory(0, true);
            }
        });
        getView().findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heroId = null;
                heroView.setText("");
                loadHistory(0, true);
            }
        });
        loadHistory(0, true);
        getGridView().setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                long lastMatchId = 0;
                MatchAdapter adapter = (MatchAdapter) getListAdapter();
                if (adapter.getCount() > 0) {
                    Match last = adapter.getItem(adapter.getCount() - 1);
                    lastMatchId = last.getMatch_id() - 1;
                }
                loadHistory(lastMatchId, false);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setColumnSize();
    }

    private void setColumnSize() {
        if (getResources().getBoolean(R.bool.is_tablet)) {
            getGridView().setNumColumns(2);
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getGridView().setNumColumns(2);
            } else {
                getGridView().setNumColumns(1);
            }
        }
    }

    private void loadHistory(final long fromMatchId, final boolean reCreateAdapter) {
        final BaseActivity activity = (BaseActivity) getActivity();
        setRefreshing(true);
        if (getListAdapter() == null || reCreateAdapter) {
            setListAdapter(new MatchAdapter(activity, new ArrayList<Match>(), account.getAccountId()));
            total = 1;
        }
        if (total > getListAdapter().getCount()) {
            new LoaderProgressTask<Pair<MatchHistoryResultResponse, String>>(new ProgressTask<Pair<MatchHistoryResultResponse, String>>() {
                @Override
                public Pair<MatchHistoryResultResponse, String> doTask(OnPublishProgressListener listener) {
                    return matchService.getMatches(activity, account.getAccountId(), fromMatchId, heroId);
                }

                @Override
                public void doAfterTask(Pair<MatchHistoryResultResponse, String> resultResponse) {
                    if (resultResponse.first != null) {
                        Result result = resultResponse.first.getResult();
                        total = result.getTotal_results();
                        ((MatchAdapter) getListAdapter()).addMatches(result.getMatches());
                        if (result.getStatus() == 15 || !TextUtils.isEmpty(result.getStatusDetail())) {
                            Toast.makeText(activity, getString(R.string.match_history_closed),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else if (!TextUtils.isEmpty(resultResponse.second)) {
                        Toast.makeText(activity, resultResponse.second, Toast.LENGTH_LONG).show();

                    }
                    setRefreshing(false);
                }

                @Override
                public void handleError(String error) {
                    Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                    setRefreshing(false);
                }

                @Override
                public String getName() {
                    return null;
                }
            }, null).execute();
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    setRefreshing(false);
                }
            }, 1000);
        }
    }

    @Override
    public void onListItemClick(GridView l, View v, int position, long id) {
        Match match = ((MatchAdapter) getListAdapter()).getItem(position);
        Intent intent = new Intent(getActivity(), MatchInfoActivity.class);
        intent.putExtra("matchId", String.valueOf(match.getMatch_id()));
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        loadHistory(0, true);
    }
}
