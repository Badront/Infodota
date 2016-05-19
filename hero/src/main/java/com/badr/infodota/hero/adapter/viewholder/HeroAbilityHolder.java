package com.badr.infodota.hero.adapter.viewholder;

import android.view.View;

import com.badr.infodota.base.adapter.holder.BaseViewHolder;
import com.badr.infodota.hero.view.HeroAbilityView;

/**
 * Created by ABadretdinov
 * 24.08.2015
 * 13:02
 */
public class HeroAbilityHolder extends BaseViewHolder {
    public HeroAbilityView abilityView;

    public HeroAbilityHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {
        abilityView = (HeroAbilityView) itemView;
    }
}
