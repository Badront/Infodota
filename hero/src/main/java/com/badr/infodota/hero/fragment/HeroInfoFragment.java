package com.badr.infodota.hero.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.badr.infodota.base.fragment.BaseViewPagerFragment;
import com.badr.infodota.base.util.Navigate;
import com.badr.infodota.hero.HeroBeanContainer;
import com.badr.infodota.hero.adapter.pager.HeroPagerAdapter;
import com.badr.infodota.hero.entity.Hero;
import com.badr.infodota.hero.service.HeroService;
import com.badr.infodota.hero.util.HeroUtils;

/**
 * Created by ABadretdinov
 * 16.03.2016
 * 14:41
 */
public class HeroInfoFragment extends BaseViewPagerFragment {

    private Hero mHero;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.hero_info, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.guides:
                Bundle bundle = new Bundle();
                bundle.putLong(Navigate.PARAM_ID, mHero.getId());
                Navigate.to(getActivity(), GuideFragment.class, bundle, true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        long id = getArguments().getLong(Navigate.PARAM_ID);
        HeroService heroService = HeroBeanContainer.getInstance().getHeroService();
        mHero = heroService.getHeroWithStatsById(getContext(), id);
        super.onActivityCreated(savedInstanceState);
        getToolbar().setNavigationIcon(HeroUtils.getHeroMiniIcon(getContext(), mHero.getDotaId()));
        getViewPager().setOffscreenPageLimit(1);
    }

    @Override
    public PagerAdapter getAdapter() {
        return new HeroPagerAdapter(getChildFragmentManager(), getContext(), mHero);
    }

    @Override
    protected String getTitle() {
        return mHero.getLocalizedName();
    }
}