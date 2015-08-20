package com.badr.infodota.base.adapter.pager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.badr.infodota.R;
import com.badr.infodota.base.api.dotabuff.Unit;
import com.badr.infodota.base.fragment.player.details.ByHeroStats;
import com.badr.infodota.base.fragment.player.details.CommonStatsFilter;
import com.badr.infodota.base.fragment.player.details.CosmeticItems;
import com.badr.infodota.base.fragment.player.details.FriendsList;
import com.badr.infodota.base.fragment.player.details.MatchHistory;

/**
 * User: ABadretdinov
 * Date: 18.02.14
 * Time: 16:50
 */
public class PlayerInfoPagerAdapter extends FragmentPagerAdapter {
    private Unit account;
    private String[] titles;

    public PlayerInfoPagerAdapter(Context context, FragmentManager fm, Unit account) {
        super(fm);
        this.account = account;
        titles = context.getResources().getStringArray(R.array.player_info_tabs);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FriendsList.newInstance(account);
            case 1:
                return CosmeticItems.newInstance(account);
            case 2:
                return MatchHistory.newInstance(account);
            case 3:
                return CommonStatsFilter.newInstance(account);
            default:
                return ByHeroStats.newInstance(account);
        }
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
