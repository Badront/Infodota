package com.badr.infodota.adapter.pager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.badr.infodota.R;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.fragment.player.groups.GroupPlayersList;
import com.badr.infodota.fragment.player.groups.PlayersList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: ABadretdinov
 * Date: 04.02.14
 * Time: 19:56
 */
public class MatchHistoryPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private Map<Integer, GroupPlayersList> groupMap = new HashMap<Integer, GroupPlayersList>();

    public MatchHistoryPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
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
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getStringArray(R.array.match_history_title)[position];
    }

    public void update() {
        Set<Integer> keySet = groupMap.keySet();
        for (Integer key : keySet) {
            groupMap.get(key).onRefresh();
        }
    }
}
