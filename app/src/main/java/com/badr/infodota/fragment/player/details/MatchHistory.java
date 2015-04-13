package com.badr.infodota.fragment.player.details;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.badr.infodota.adapter.holder.PlayerMatchHolder;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.api.matchhistory.Match;
import com.badr.infodota.api.matchhistory.MatchHistoryResultResponse;
import com.badr.infodota.api.matchhistory.PlayerMatch;
import com.badr.infodota.api.matchhistory.PlayerMatchResult;
import com.badr.infodota.api.matchhistory.Result;
import com.badr.infodota.fragment.RecyclerFragment;
import com.badr.infodota.service.hero.HeroService;
import com.badr.infodota.service.match.MatchService;
import com.badr.infodota.util.EndlessRecycleScrollListener;
import com.badr.infodota.util.EndlessScrollListener;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.retry.RetryPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 18.02.14
 * Time: 16:46
 */
public class MatchHistory extends RecyclerFragment<PlayerMatch,PlayerMatchHolder> implements RequestListener<PlayerMatchResult> {
    HeroService heroService = BeanContainer.getInstance().getHeroService();
    private Unit account;
    private long total = 1;
    private Long heroId = null;
    private AutoCompleteTextView heroView;
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);

    public static MatchHistory newInstance(Unit account) {
        MatchHistory fragment = new MatchHistory();
        fragment.account = account;
        return fragment;
    }

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
        setLayoutId(R.layout.match_history);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(getActivity(),1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View root=getView();
        Activity activity = getActivity();
        if (root != null&&activity!=null) {
            setColumnSize();
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
            getRecyclerView().setOnScrollListener(new EndlessRecycleScrollListener() {
                @Override
                public void onLoadMore() {
                    long lastMatchId = 0;
                    MatchAdapter adapter = (MatchAdapter) getAdapter();
                    int count = adapter.getItemCount();
                    if (count > 0) {
                        PlayerMatch last = adapter.getItem(adapter.getItemCount() - 1);
                        lastMatchId = last.getMatchId() - 1;
                    }
                    loadHistory(lastMatchId, false);
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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

    private void loadHistory(long fromMatchId, boolean reCreateAdapter) {
        /*if(reCreateAdapter){
            setAdapter(new MatchAdapter(null));
            total=1;
        }*/
        if(reCreateAdapter||total>getAdapter().getItemCount()){
            setRefreshing(true);
            spiceManager.execute(new PlayerMatchLoadRequest(account.getAccountId(),fromMatchId,heroId),this);
        }
    }

    @Override
    public void onRefresh() {
        loadHistory(0, true);
    }

    @Override
    public void onItemClick(View view, int position) {
        PlayerMatch entity=getAdapter().getItem(position);
        Intent intent=new Intent(getActivity(),MatchInfoActivity.class);
        intent.putExtra("matchId",String.valueOf(entity.getMatchId()));
        startActivity(intent);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        setRefreshing(false);
        Toast.makeText(getActivity(), spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(PlayerMatchResult playerMatchResult) {
        setRefreshing(false);
        if (playerMatchResult!=null) {
            total = playerMatchResult.getTotalMatches();
            if(getAdapter()==null){
                setAdapter(new MatchAdapter(playerMatchResult.getPlayerMatches()));
            }
            else{
                ((MatchAdapter)getAdapter()).addMatches(playerMatchResult.getPlayerMatches());
            }
            if (playerMatchResult.getStatus() == 15){
                Toast.makeText(getActivity(), getString(R.string.match_history_closed),
                        Toast.LENGTH_LONG).show();
            }
            else if(!TextUtils.isEmpty(playerMatchResult.getStatusDetails())) {
                Toast.makeText(getActivity(), playerMatchResult.getStatusDetails(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public class PlayerMatchLoadRequest extends TaskRequest<PlayerMatchResult>{
        private BeanContainer container = BeanContainer.getInstance();
        private MatchService matchService = container.getMatchService();

        private long fromMatchId;
        private long accountId;
        private Long heroId;
        public PlayerMatchLoadRequest(long accountId, long fromMatchId, Long heroId) {
            super(PlayerMatchResult.class);
            this.fromMatchId=fromMatchId;
            this.accountId=accountId;
            this.heroId=heroId;
        }

        @Override
        public PlayerMatchResult loadData() throws Exception {
            Activity activity=getActivity();
            if(activity!=null)
            {
                return matchService.getMatches(activity,accountId, fromMatchId, heroId);
            }
            return null;
        }
    }
}
