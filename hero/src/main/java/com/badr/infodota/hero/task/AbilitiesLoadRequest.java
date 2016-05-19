package com.badr.infodota.hero.task;

import android.content.Context;

import com.badr.infodota.base.service.TaskRequest;
import com.badr.infodota.hero.entity.FullAbility;
import com.badr.infodota.hero.util.HeroUtils;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 16:38
 */
public class AbilitiesLoadRequest extends TaskRequest<FullAbility.List> {

    private Context mContext;
    private String mHeroDotaId;

    public AbilitiesLoadRequest(Context context, String heroDotaId) {
        super(FullAbility.List.class);
        this.mContext = context;
        this.mHeroDotaId = heroDotaId;
    }

    @Override
    public FullAbility.List loadData() throws Exception {
        return HeroUtils.getHeroAbilities(mContext, mHeroDotaId);
    }
}
