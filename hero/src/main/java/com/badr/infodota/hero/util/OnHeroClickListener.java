package com.badr.infodota.hero.util;

import android.os.Bundle;
import android.view.View;

import com.badr.infodota.base.util.Navigate;
import com.badr.infodota.hero.fragment.HeroInfoFragment;

/**
 * Created by ABadretdinov
 * 16.03.2016
 * 14:10
 */
public class OnHeroClickListener implements View.OnClickListener {
    private long mHeroId;

    public OnHeroClickListener(long heroId) {
        this.mHeroId = heroId;
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong(Navigate.PARAM_ID, mHeroId);
        Navigate.to(v.getContext(), HeroInfoFragment.class, bundle, true);
    }
}
