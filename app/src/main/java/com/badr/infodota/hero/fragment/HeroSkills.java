package com.badr.infodota.hero.fragment;

import android.app.Activity;
import android.support.v4.app.ListFragment;

import com.badr.infodota.base.util.retrofit.LocalSpiceService;
import com.badr.infodota.hero.adapter.SkillsAdapter;
import com.badr.infodota.hero.api.Hero;
import com.badr.infodota.hero.api.Skill;
import com.badr.infodota.hero.task.SkillsLoadRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * User: ABadretdinov
 * Date: 16.01.14
 * Time: 15:57
 */
public class HeroSkills extends ListFragment implements RequestListener<Skill.List> {
    private SpiceManager mSpiceManager = new SpiceManager(LocalSpiceService.class);

    private Hero hero;

    public static HeroSkills newInstance(Hero hero) {
        HeroSkills fragment = new HeroSkills();
        fragment.hero = hero;
        return fragment;
    }

    @Override
    public void onStart() {
        if (!mSpiceManager.isStarted()) {
            Activity activity = getActivity();
            if (activity != null) {
                mSpiceManager.start(activity);
                mSpiceManager.execute(new SkillsLoadRequest(activity.getApplicationContext(), hero.getDotaId()), this);
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
    public void onRequestSuccess(Skill.List skills) {
        setListAdapter(new SkillsAdapter(getActivity(), skills));

    }
}
