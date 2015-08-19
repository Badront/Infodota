package com.badr.infodota.fragment.player.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.PlayerInfoActivity;
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
 * Date: 21.04.14
 * Time: 17:05
 */
public class FriendsList extends RecyclerFragment<Unit, PlayerHolder> implements RequestListener<Unit.List>{
    private Unit account;
    private BeanContainer container = BeanContainer.getInstance();
    private PlayerService playerService = container.getPlayerService();
    private SpiceManager mSpiceManager = new SpiceManager(UncachedSpiceService.class);

    public static FriendsList newInstance(Unit unit) {
        FriendsList fragment = new FriendsList();
        fragment.setAccount(unit);
        return fragment;
    }

    @Override
    public void onStart() {
        if (!mSpiceManager.isStarted()) {
            mSpiceManager.start(getActivity());
            onRefresh();
        }
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
        super.onDestroy();
    }

    public void setAccount(Unit account) {
        this.account = account;
    }

    @Override
    public void onRefresh() {
        mSpiceManager.execute(new UnitsLoadRequest(), this);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager(Context context) {
        return new GridLayoutManager(context,1);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setColumnSize();
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
        Unit unit = getAdapter().getItem(position);

        unit.setSearched(true);
        Activity activity = getActivity();
        playerService.saveAccount(activity, unit);

        Intent intent = new Intent(activity, PlayerInfoActivity.class);
        intent.putExtra("account", unit);
        startActivity(intent);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        setRefreshing(false);
        Toast.makeText(getActivity(), spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(Unit.List units) {
        setRefreshing(false);
        Activity activity=getActivity();
        if (units != null &&activity!=null) {
            PlayersAdapter adapter = new PlayersAdapter(units, true, activity.getResources().getStringArray(R.array.match_history_title));
            setAdapter(adapter);
        }
    }
    public class UnitsLoadRequest extends TaskRequest<Unit.List>{

        public UnitsLoadRequest() {
            super(Unit.List.class);
        }

        @Override
        public Unit.List loadData() throws Exception {
            return new Unit.List(playerService.loadFriends(account.getAccountId()));
        }
    }
}
