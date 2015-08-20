package com.badr.infodota.base.fragment.match;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.badr.infodota.base.activity.MatchPlayerInfoActivity;
import com.badr.infodota.base.adapter.MatchDetailsAdapter;
import com.badr.infodota.base.api.matchdetails.Player;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 21.01.14
 * Time: 15:32
 */
public class MatchPlayersForSideDetails extends ListFragment {
    private List<Player> players;
    private boolean randomSkills;

    public static MatchPlayersForSideDetails newInstance(boolean isRandomSkills, List<Player> players) {
        MatchPlayersForSideDetails fragment = new MatchPlayersForSideDetails();
        fragment.setPlayers(players);
        fragment.setRandomSkills(isRandomSkills);
        return fragment;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setRandomSkills(boolean randomSkills) {
        this.randomSkills = randomSkills;
    }

    public void setPlayersWithListUpdate(boolean randomSkills, List<Player> players) {
        this.players = players;
        this.randomSkills = randomSkills;
        Context context = getActivity();
        if (context != null) {
            setListAdapter(new MatchDetailsAdapter(context, players));
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Player player = (Player) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), MatchPlayerInfoActivity.class);
        intent.putExtra("player", player);
        intent.putExtra("randomSkills", randomSkills);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (players != null) {
            setListAdapter(new MatchDetailsAdapter(getActivity(), players));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ListAdapter adapter = getListAdapter();
        if (adapter != null) {
            ((MatchDetailsAdapter) adapter).notifyDataSetChanged();
        }
    }

}
