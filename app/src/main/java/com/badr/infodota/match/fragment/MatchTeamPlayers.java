package com.badr.infodota.match.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.badr.infodota.match.activity.MatchPlayerDetailsActivity;
import com.badr.infodota.match.adapter.MatchPlayersAdapter;
import com.badr.infodota.match.api.Player;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 21.01.14
 * Time: 15:32
 */
public class MatchTeamPlayers extends ListFragment {
    private List<Player> players;
    private boolean randomSkills;

    public static MatchTeamPlayers newInstance(boolean isRandomSkills, List<Player> players) {
        MatchTeamPlayers fragment = new MatchTeamPlayers();
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
            setListAdapter(new MatchPlayersAdapter(context, players));
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Player player = (Player) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), MatchPlayerDetailsActivity.class);
        intent.putExtra("player", player);
        intent.putExtra("randomSkills", randomSkills);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (players != null) {
            setListAdapter(new MatchPlayersAdapter(getActivity(), players));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ListAdapter adapter = getListAdapter();
        if (adapter != null) {
            ((MatchPlayersAdapter) adapter).notifyDataSetChanged();
        }
    }

}
