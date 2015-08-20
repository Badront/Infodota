package com.badr.infodota.base.adapter.pager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.badr.infodota.R;
import com.badr.infodota.base.api.dotabuff.Unit;
import com.badr.infodota.base.fragment.player.groups.GroupPlayersList;
import com.badr.infodota.base.fragment.player.groups.PlayersList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: ABadretdinov
 * Date: 04.02.14
 * Time: 19:56
 */
public class MatchHistoryPagerAdapter extends FragmentPagerAdapter {
    private String[] titles;
    private Map<Integer, GroupPlayersList> groupMap = new HashMap<Integer, GroupPlayersList>();

    public MatchHistoryPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        titles = context.getResources().getStringArray(R.array.match_history_title);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new PlayersList();
            case 1:
                GroupPlayersList friend = groupMap.get(i);
                if (friend == null) {
                    friend = GroupPlayersList.newInstance(Unit.Groups.FRIEND);
                    groupMap.put(i, friend);
                }
                return friend;
            default:
            case 2:
                GroupPlayersList pro = groupMap.get(i);
                if (pro == null) {
                    pro = GroupPlayersList.newInstance(Unit.Groups.PRO);
                    groupMap.put(i, pro);
                }
                return pro;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        groupMap.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public void update() {
        Set<Integer> keySet = groupMap.keySet();
        for (Integer key : keySet) {
            groupMap.get(key).onRefresh();
        }
    }
}
