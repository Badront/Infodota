package com.badr.infodota.fragment.player.groups;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.BaseActivity;
import com.badr.infodota.activity.PlayerInfoActivity;
import com.badr.infodota.adapter.OnItemClickListener;
import com.badr.infodota.adapter.PlayersAdapter;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.service.player.PlayerService;
import com.badr.infodota.util.DialogUtils;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 20.01.14
 * Time: 18:14
 */
public class PlayersList extends Fragment implements TextView.OnEditorActionListener, OnItemClickListener {
    TextView searchRequest;
    private RecyclerView gridView;
    private PlayersAdapter mAdapter;
    private BeanContainer container = BeanContainer.getInstance();
    private PlayerService playerService = container.getPlayerService();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.players_list, container, false);
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
        searchRequest = (TextView) root.findViewById(R.id.player_name);
        searchRequest.setOnEditorActionListener(this);
        root.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRequest.setText("");
                initList();
            }
        });
        root.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForPlayer();
            }
        });
        initList();
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

    private void initList() {
        final Activity activity = getActivity();
        if (activity != null) {
            new LoaderProgressTask<List<Unit>>(new ProgressTask<List<Unit>>() {
                @Override
                public List<Unit> doTask(OnPublishProgressListener listener) throws Exception {
                    return playerService.getSearchedAccounts(activity);
                }

                @Override
                public void doAfterTask(List<Unit> result) {
                    mAdapter = new PlayersAdapter(result, true, getResources().getStringArray(R.array.match_history_title));
                    gridView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(PlayersList.this);
                    View root = getView();
                    if (result.size() > 0 && root != null) {
                        root.findViewById(R.id.search_history).setVisibility(View.VISIBLE);
                        root.findViewById(R.id.help).setVisibility(View.GONE);
                    }
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

    private void searchForPlayer() {
        final BaseActivity activity = (BaseActivity) getActivity();
        activity.setSupportProgressBarIndeterminateVisibility(true);
        DialogUtils.showLoaderDialog(getFragmentManager(), new ProgressTask<Pair<List<Unit>, String>>() {
            @Override
            public Pair<List<Unit>, String> doTask(OnPublishProgressListener listener) throws Exception {
                Pair<List<Unit>, String> result = playerService.loadAccounts(activity, searchRequest.getText().toString());
                if (result.first == null) {
                    throw new Exception(result.second);
                } else {
                    List<Unit> units = result.first;
                    List<Unit> players = new ArrayList<Unit>();
                    for (Unit unit : units) {
                        Unit local = playerService.getAccountById(activity, unit.getAccountId());
                        if (local != null) {
                            unit.setGroup(local.getGroup());
                            unit.setLocalName(local.getLocalName());
                        }
                        players.add(unit);
                    }
                    return Pair.create(players, result.second);
                }
            }

            @Override
            public void doAfterTask(Pair<List<Unit>, String> result) {
                activity.setSupportProgressBarIndeterminateVisibility(false);
                List<Unit> players = result.first;
                View root = getView();
                if (root != null) {
                    root.findViewById(R.id.search_history).setVisibility(View.GONE);
                    mAdapter = new PlayersAdapter(players, true, getResources().getStringArray(R.array.match_history_title));
                    gridView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(PlayersList.this);
                    if (players.size() > 0) {
                        root.findViewById(R.id.help).setVisibility(View.GONE);
                    } else {
                        root.findViewById(R.id.help).setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void handleError(String error) {
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                activity.setSupportProgressBarIndeterminateVisibility(false);
            }

            @Override
            public String getName() {
                return null;
            }
        });
    }

    @Override
    public void onDestroy() {
        ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
        super.onDestroy();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE || event == null ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            searchForPlayer();
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
}