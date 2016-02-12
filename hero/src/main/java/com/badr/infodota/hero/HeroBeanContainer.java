package com.badr.infodota.hero;

import com.badr.infodota.base.dao.CreateTableDao;
import com.badr.infodota.hero.dao.AbilityDao;
import com.badr.infodota.hero.dao.HeroDao;
import com.badr.infodota.hero.dao.HeroStatsDao;
import com.badr.infodota.hero.service.HeroServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ABadretdinov
 * 12.02.2016
 * 16:46
 */
public class HeroBeanContainer {
    private static final Object MONITOR = new Object();
    private static HeroBeanContainer sInstance = null;

    private final HeroServiceImpl heroService;
    private final HeroDao heroDao;
    private final HeroStatsDao heroStatsDao;
    private final AbilityDao abilityDao;
    private final List<CreateTableDao> allDaos;

    public HeroBeanContainer() {
        allDaos = new ArrayList<>();

        heroDao = new HeroDao();
        heroStatsDao = new HeroStatsDao();
        abilityDao = new AbilityDao();

        allDaos.add(heroDao);
        allDaos.add(heroStatsDao);
        allDaos.add(abilityDao);

        heroService = new HeroServiceImpl();
    }

    public static HeroBeanContainer getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (MONITOR) {
            if (sInstance == null) {
                sInstance = new HeroBeanContainer();
            }
        }
        return sInstance;
    }

    public HeroServiceImpl getHeroService() {
        return heroService;
    }

    public HeroDao getHeroDao() {
        return heroDao;
    }

    public HeroStatsDao getHeroStatsDao() {
        return heroStatsDao;
    }

    public AbilityDao getAbilityDao() {
        return abilityDao;
    }

    public List<CreateTableDao> getAllDaos() {
        return allDaos;
    }
}
