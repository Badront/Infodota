package com.badr.infodota.fragment.player.details;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.BaseActivity;
import com.badr.infodota.activity.PlayerInfoActivity;
import com.badr.infodota.adapter.OnItemClickListener;
import com.badr.infodota.adapter.PlayersAdapter;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.service.player.PlayerService;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 21.04.14
 * Time: 17:05
 */
public class FriendsList extends Fragment implements OnItemClickListener {
    private Unit account;
    private RecyclerView gridView;
    private PlayersAdapter mAdapter;
    private BeanContainer container = BeanContainer.getInstance();
    private PlayerService playerService = container.getPlayerService();

    public static FriendsList newInstance(Unit unit) {
        FriendsList fragment = new FriendsList();
        fragment.setAccount(unit);
        return fragment;
    }

    public void setAccount(Unit account) {
        this.account = account;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.players_friend_list, container, false);
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
        updateList();
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

    public void updateList() {
        final BaseActivity activity = (BaseActivity) getActivity();
        if (activity != null) {
            activity.setSupportProgressBarIndeterminateVisibility(true);
            new LoaderProgressTask<Pair<List<Unit>, String>>(new ProgressTask<Pair<List<Unit>, String>>() {
                @Override
                public Pair<List<Unit>, String> doTask(OnPublishProgressListener listener) throws Exception {
                    return playerService.loadFriends(activity, account.getAccountId());
                }

                @Override
                public void doAfterTask(Pair<List<Unit>, String> result) {
                    activity.setSupportProgressBarIndeterminateVisibility(false);
                    if (result != null) {
                        if (result.first != null) {
                            mAdapter = new PlayersAdapter(result.first, true, activity.getResources().getStringArray(R.array.match_history_title));
                            gridView.setAdapter(mAdapter);
                            mAdapter.setOnItemClickListener(FriendsList.this);
                        } else if (!TextUtils.isEmpty(result.second)) {
                            handleError(result.second);
                        }
                    }
                }

                @Override
                public void handleError(String error) {
                    activity.setSupportProgressBarIndeterminateVisibility(false);
                    Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
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
        Activity activity = getActivity();
        playerService.saveAccount(activity, unit);

        Intent intent = new Intent(activity, PlayerInfoActivity.class);
        intent.putExtra("account", unit);
        startActivity(intent);
    }
}
