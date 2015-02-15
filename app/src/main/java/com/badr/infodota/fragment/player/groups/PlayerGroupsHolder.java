package com.badr.infodota.fragment.player.groups;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badr.infodota.R;
import com.badr.infodota.activity.ListHolderActivity;
import com.badr.infodota.adapter.pager.MatchHistoryPagerAdapter;
import com.badr.infodota.view.SlidingTabLayout;

/**
 * User: ABadretdinov
 * Date: 04.02.14
 * Time: 19:48
 */
public class PlayerGroupsHolder extends Fragment {
    private MatchHistoryPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.match_history_holder, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        ((ListHolderActivity) getActivity()).getActionMenuView().setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        initPager();
    }

    public MatchHistoryPagerAdapter getAdapter() {
        return adapter;
    }

    private void initPager() {
        adapter = new MatchHistoryPagerAdapter(getChildFragmentManager(), getActivity());

        ViewPager pager = (ViewPager) getView().findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);

        SlidingTabLayout indicator = (SlidingTabLayout) getView().findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }
}
