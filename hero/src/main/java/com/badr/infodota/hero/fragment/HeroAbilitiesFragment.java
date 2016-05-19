package com.badr.infodota.hero.fragment;

import android.app.Activity;
import android.os.Bundle;

import com.badr.infodota.base.fragment.RecyclerFragment;
import com.badr.infodota.base.service.LocalSpiceService;
import com.badr.infodota.base.util.Navigate;
import com.badr.infodota.hero.adapter.HeroAbilitiesAdapter;
import com.badr.infodota.hero.adapter.viewholder.HeroAbilityHolder;
import com.badr.infodota.hero.entity.FullAbility;
import com.badr.infodota.hero.entity.Hero;
import com.badr.infodota.hero.task.AbilitiesLoadRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * User: ABadretdinov
 * Date: 16.01.14
 * Time: 15:57
 */
public class HeroAbilitiesFragment extends RecyclerFragment<FullAbility, HeroAbilityHolder> implements RequestListener<FullAbility.List> {
    private SpiceManager mSpiceManager = new SpiceManager(LocalSpiceService.class);

    public static HeroAbilitiesFragment newInstance(Hero hero) {
        HeroAbilitiesFragment fragment = new HeroAbilitiesFragment();
        Bundle args = new Bundle();
        args.putSerializable(Navigate.PARAM_ENTITY, hero);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        if (!mSpiceManager.isStarted()) {
            Activity activity = getActivity();
            Hero hero = (Hero) getArguments().getSerializable(Navigate.PARAM_ENTITY);
            if (activity != null && hero != null) {
                mSpiceManager.start(activity);
                mSpiceManager.execute(new AbilitiesLoadRequest(activity.getApplicationContext(), hero.getDotaId()), this);
            }
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

    @Override
    public void onRequestFailure(SpiceException spiceException) {
    }

    @Override
    public void onRequestSuccess(FullAbility.List skills) {
        setAdapter(new HeroAbilitiesAdapter(skills));
    }
}
