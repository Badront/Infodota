package com.badr.infodota.item;

import com.badr.infodota.base.dao.CreateTableDao;
import com.badr.infodota.item.dao.ItemDao;
import com.badr.infodota.item.service.ItemServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ABadretdinov
 * 16.03.2016
 * 17:30
 */
public class ItemBeanContainer {
    private static final Object MONITOR = new Object();
    private static ItemBeanContainer sInstance = null;

    private final ItemServiceImpl itemService;
    private final ItemDao itemDao;
    private final List<CreateTableDao> allDaos;

    public ItemBeanContainer() {
        allDaos = new ArrayList<>();

        itemDao = new ItemDao();

        allDaos.add(itemDao);

        itemService = new ItemServiceImpl();
    }

    public static ItemBeanContainer getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (MONITOR) {
            if (sInstance == null) {
                sInstance = new ItemBeanContainer();
            }
        }
        return sInstance;
    }

    public ItemServiceImpl getItemService() {
        return itemService;
    }

    public ItemDao getItemDao() {
        return itemDao;
    }

    public List<CreateTableDao> getAllDaos() {
        return allDaos;
    }
}
