package com.badr.infodota.hero.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badr.infodota.base.adapter.BaseRecyclerAdapter;
import com.badr.infodota.hero.R;
import com.badr.infodota.hero.adapter.viewholder.HeroAbilityHolder;
import com.badr.infodota.hero.entity.FullAbility;

import java.util.List;

/**
 * Created by ABadretdinov
 * 24.08.2015
 * 13:15
 */
public class HeroAbilitiesAdapter extends BaseRecyclerAdapter<FullAbility, HeroAbilityHolder> {

    public HeroAbilitiesAdapter(List<FullAbility> data) {
        super(data);
    }

    @Override
    public HeroAbilityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hero_ability_row, parent, false);
        return new HeroAbilityHolder(view);
    }

    @Override
    public void onBindViewHolder(HeroAbilityHolder holder, int position) {
        FullAbility ability = getItem(position);
        holder.abilityView.setAbility(ability);
    }
}
