package com.badr.infodota.service.cosmetic;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.InitializingBean;
import com.badr.infodota.R;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.api.truepicker.Counter;
import com.badr.infodota.remote.counterpicker.CounterRemoteEntityService;
import com.badr.infodota.service.hero.HeroService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 14:54
 */
public class CounterServiceImpl implements CounterService, InitializingBean {
    private CounterRemoteEntityService service;
    private HeroService heroService;

    @Override
    public Pair<List<Hero>, String> getCounters(Context context, List<Integer> allies, List<Integer> enemies,
                                                int roleCodes) {
        try {
            Pair<List<Counter>, String> serviceResult = service.getCounters(context, allies, enemies, roleCodes);
            Pair<List<Hero>, String> result;
            if (serviceResult.first == null) {
                String message;
                if (serviceResult.second.contains("\"controller\":\"pick\"")) {
                    message = context.getString(R.string.empty_truepicker);
                } else {
                    message = serviceResult.second;
                }
                Log.e(CounterServiceImpl.class.getName(), message);
                result = Pair.create(null, message);
            } else {
                List<Hero> heroes = new ArrayList<Hero>();
                for (Counter counter : serviceResult.first) {
                    Hero hero = heroService.getTruepickerHero(context, Integer.valueOf(counter.getHero()));
                    if (hero != null) {
                        heroes.add(hero);
                    }
                }
                result = Pair.create(heroes, null);
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get counters, cause: " + e.getMessage();
            Log.e(CounterServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public void initialize() {
        BeanContainer container = BeanContainer.getInstance();
        service = container.getCounterRemoteEntityService();
        heroService = container.getHeroService();
    }
}
