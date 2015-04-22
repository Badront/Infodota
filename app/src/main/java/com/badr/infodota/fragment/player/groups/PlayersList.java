package com.badr.infodota.fragment.player.groups;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.PlayerInfoActivity;
import com.badr.infodota.adapter.BaseRecyclerAdapter;
import com.badr.infodota.adapter.PlayersAdapter;
import com.badr.infodota.adapter.holder.PlayerHolder;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.fragment.RecyclerFragment;
import com.badr.infodota.service.player.PlayerService;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * User: ABadretdinov
 * Date: 20.01.14
 * Time: 18:14
 */
public class PlayersList extends RecyclerFragment<Unit,PlayerHolder> implements TextView.OnEditorActionListener,RequestListener<Unit.List> {
    private TextView searchRequest;
    private View listHeader;
    private BeanContainer container = BeanContainer.getInstance();
    private PlayerService playerService = container.getPlayerService();
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);
    private boolean initialized=false;
    @Override
    public void onStart() {
        if(!spiceManager.isStarted()) {
            spiceManager.start(getActivity());
            if(!initialized){
                initData();
            }
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        initialized=false;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setLayoutId(R.layout.players_list);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public RecyclerView.LayoutManager getLayoutManager(Context context) {
        return new GridLayoutManager(context,1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View root = getView();
        if(root!=null) {
            setColumnSize();
            searchRequest = (TextView) root.findViewById(R.id.player_name);
            searchRequest.setOnEditorActionListener(this);
            listHeader=root.findViewById(R.id.search_history);
            root.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchRequest.setText("");
                    initData();
                }
            });
            root.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRefresh();
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

    private void initData() {
        spiceManager.execute(new SearchedPlayersLoadRequest(null),this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE || event == null ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            onRefresh();
            return true;
        }
        return false; // pass on to other listeners.
    }

    @Override
    public void onItemClick(View view, int position) {
        Unit unit = mAdapter.getItem(position);

        unit.setSearched(true);
        playerService = BeanContainer.getInstance().getPlayerService();
        playerService.saveAccount(getActivity(), unit);

        Intent intent = new Intent(getActivity(), PlayerInfoActivity.class);
        intent.putExtra("account", unit);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        setRefreshing(true);
        spiceManager.execute(new SearchedPlayersLoadRequest(searchRequest.getText().toString()), this);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        initialized=true;
        setRefreshing(false);
        Toast.makeText(getActivity(),spiceException.getLocalizedMessage(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void setAdapter(BaseRecyclerAdapter<Unit, PlayerHolder> adapter) {
        super.setAdapter(adapter);
        if(adapter.getItemCount()==0){
            listHeader.setVisibility(View.GONE);
        }
        else {
            mEmptyView.setVisibility(View.GONE);
            if(TextUtils.isEmpty(searchRequest.getText().toString())){
                listHeader.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRequestSuccess(Unit.List units) {
        initialized=true;
        setRefreshing(false);
        PlayersAdapter adapter = new PlayersAdapter(units, true, getResources().getStringArray(R.array.match_history_title));
        setAdapter(adapter);
    }

    public class SearchedPlayersLoadRequest extends TaskRequest<Unit.List>{
        private String searchText;
        public SearchedPlayersLoadRequest(String search) {
            super(Unit.List.class);
            this.searchText=search;
        }

        @Override
        public Unit.List loadData() throws Exception {
            Activity activity=getActivity();
            if(activity!=null) {
                if(searchText==null){
                    return playerService.getSearchedAccounts(activity);
                }
                else {
                    Unit.List result = playerService.loadAccounts(searchText);
                    Unit.List players = new Unit.List();
                    for (Unit unit : result) {
                        Unit local = playerService.getAccountById(activity, unit.getAccountId());
                        if (local != null) {
                            unit.setGroup(local.getGroup());
                            unit.setLocalName(local.getLocalName());
                        }
                        players.add(unit);
                    }
                    return players;
                }
            }
            return null;
        }
    }
}