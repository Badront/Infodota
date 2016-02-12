package com.badr.infodota.base;


import com.badr.infodota.base.dao.CreateTableDao;
import com.badr.infodota.base.service.NavigationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ABadretdinov
 * 24.06.2015
 * 16:17
 */
public class BaseBeanContainer {
    private static final Object MONITOR = new Object();
    private static BaseBeanContainer instance = null;
    private final List<CreateTableDao> mAllDaos;
    private NavigationService navigationService;

    private BaseBeanContainer() {
        mAllDaos = new ArrayList<>();
    }

    public static BaseBeanContainer getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (MONITOR) {
            if (instance == null) {
                instance = new BaseBeanContainer();
            }
        }
        return instance;
    }

    public List<CreateTableDao> getAllDaos() {
        return mAllDaos;
    }

    public NavigationService getNavigationService() {
        return navigationService;
    }

    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }
}