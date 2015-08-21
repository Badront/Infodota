package com.badr.infodota.counter.service;

import android.content.Context;

import com.badr.infodota.counter.api.TruepickerHero;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 14:53
 */
public interface CounterService {
    TruepickerHero.List getCounters(
            Context context,
            List<Integer> allies,
            List<Integer> enemies,
            int roleCodes);
}
