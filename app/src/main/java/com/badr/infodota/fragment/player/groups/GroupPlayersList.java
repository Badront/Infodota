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

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.PlayerInfoActivity;
import com.badr.infodota.adapter.OnItemClickListener;
import com.badr.infodota.adapter.PlayersAdapter;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.service.player.PlayerService;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;

import java.util.List;

/**
 * User: Histler
 * Date: 04.02.14
 */
public class GroupPlayersList extends Fragment implements GroupList, OnItemClickListener, TextWatcher {
    private Unit.Groups group;
    private PlayerService playerService = BeanContainer.getInstance().getPlayerService();
    private RecyclerView gridView;
    private EditText search;
    private PlayersAdapter mAdapter;
    private String query = null;
    private Filter filter;
    private View help;

    public static GroupPlayersList newInstance(Unit.Groups group) {
        GroupPlayersList fragment = new GroupPlayersList();
        fragment.setGroup(group);
        return fragment;
    }

    public void setGroup(Unit.Groups group) {
        this.group = group;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.group_players_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View root = getView();
        gridView = (RecyclerView) root.findViewById(R.id.gridView);
        gridView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        gridView.setLayoutManager(layoutManager);
        setColumnSize();
        search = (EditText) root.findViewById(R.id.search);
        help = root.findViewById(R.id.help);
        updateList();
        search.addTextChangedListener(this);
        root.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
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
            ((GridLayoutManager) gridView.getLayoutManager()).setSpanCount(2);
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ((GridLayoutManager) gridView.getLayoutManager()).setSpanCount(2);
            } else {
                ((GridLayoutManager) gridView.getLayoutManager()).setSpanCount(1);
            }
        }
    }

    @Override
    public void updateList() {
        final Activity activity = getActivity();
        if (activity != null) {
            new LoaderProgressTask<List<Unit>>(new ProgressTask<List<Unit>>() {
                @Override
                public List<Unit> doTask(OnPublishProgressListener listener) throws Exception {
                    return playerService.getAccountsByGroup(activity, group);
                }

                @Override
                public void doAfterTask(List<Unit> result) {
                    if (result != null && result.size() > 0) {
                        help.setVisibility(View.GONE);
                    } else {
                        help.setVisibility(View.VISIBLE);
                    }
                    mAdapter = new PlayersAdapter(result, false, getResources().getStringArray(R.array.match_history_title));
                    filter = mAdapter.getFilter();
                    filter.filter(query);
                    gridView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(GroupPlayersList.this);
                }

                @Override
                public void handleError(String error) {

                }

                @Override
                public String getName() {
                    return null;
                }
            }, null).execute();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Unit unit = mAdapter.getItem(position);
        unit.setSearched(true);

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
            updateList();
        }
    }
}
