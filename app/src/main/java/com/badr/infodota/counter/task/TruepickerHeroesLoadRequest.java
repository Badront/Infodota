package com.badr.infodota.counter.task;

import android.content.Context;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.counter.api.TruepickerHero;
import com.badr.infodota.hero.service.HeroService;
import com.badr.infodota.util.retrofit.TaskRequest;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 16:52
 */
public class TruepickerHeroesLoadRequest extends TaskRequest<TruepickerHero.List> {
    private String mFilter;
    private Context mContext;

    public TruepickerHeroesLoadRequest(Context context, String filter) {
        super(TruepickerHero.List.class);
        this.mFilter = filter;
        this.mContext = context;
    }

    @Override
    public TruepickerHero.List loadData() throws Exception {
        HeroService heroService = BeanContainer.getInstance().getHeroService();
        return heroService.getTruepickerHeroes(mContext, mFilter);
    }
}
