package com.badr.infodota.fragment.player.groups;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.PlayerInfoActivity;
import com.badr.infodota.adapter.OnItemClickListener;
import com.badr.infodota.adapter.PlayersAdapter;
import com.badr.infodota.adapter.holder.PlayerHolder;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.fragment.RecyclerFragment;
import com.badr.infodota.fragment.player.details.FriendsList;
import com.badr.infodota.service.player.PlayerService;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

/**
 * User: Histler
 * Date: 04.02.14
 */
public class GroupPlayersList extends RecyclerFragment<Unit,PlayerHolder> implements TextWatcher,RequestListener<Unit.List> {
    private Unit.Groups group;
    private EditText search;
    private String query = null;
    private Filter filter;
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);

    public static GroupPlayersList newInstance(Unit.Groups group) {
        GroupPlayersList fragment = new GroupPlayersList();
        fragment.setGroup(group);
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

    public void setGroup(Unit.Groups group) {
        this.group = group;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setLayoutId(R.layout.group_players_list);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View root = getView();
        if(root!=null){
            getRecyclerView().setHasFixedSize(true);
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
            getRecyclerView().setLayoutManager(layoutManager);
            setColumnSize();
            search = (EditText) root.findViewById(R.id.search);

            onRefresh();
            search.addTextChangedListener(this);
            root.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search.setText("");
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

    @Override
    public void onItemClick(View view, int position) {
        Unit unit = mAdapter.getItem(position);
        unit.setSearched(true);
        PlayerService playerService = BeanContainer.getInstance().getPlayerService();
        playerService.saveAccount(getActivity(), unit);

        Intent intent = new Intent(getActivity(), PlayerInfoActivity.class);
        intent.putExtra("account", unit);
        startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        query = s.toString();
        if (filter != null) {
            filter.filter(query);
        } else {
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        setRefreshing(true);
        spiceManager.execute(new UnitsLoadRequest(group),this);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        setRefreshing(false);
        Toast.makeText(getActivity(),spiceException.getLocalizedMessage(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(Unit.List units) {
        setRefreshing(false);
        PlayersAdapter adapter = new PlayersAdapter(units, false, getResources().getStringArray(R.array.match_history_title));
        filter = adapter.getFilter();
        filter.filter(query);
        setAdapter(adapter);
    }

    public class UnitsLoadRequest extends TaskRequest<Unit.List>{

        private PlayerService playerService = BeanContainer.getInstance().getPlayerService();
        private Unit.Groups group;
        public UnitsLoadRequest(Unit.Groups group) {
            super(Unit.List.class);
            this.group=group;
        }

        @Override
        public Unit.List loadData() throws Exception {
            Activity activity=getActivity();
            if(activity!=null)
            {
                return playerService.getAccountsByGroup(activity, group);
            }
            return null;
        }
    }
}
