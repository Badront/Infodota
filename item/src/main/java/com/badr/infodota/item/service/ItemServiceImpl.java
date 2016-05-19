package com.badr.infodota.item.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.badr.infodota.base.dao.DatabaseManager;
import com.badr.infodota.item.ItemBeanContainer;
import com.badr.infodota.item.dao.ItemDao;
import com.badr.infodota.item.entity.Item;

import java.util.List;

/**
 * Created by ABadretdinov
 * 17.03.2016
 * 16:02
 */
public class ItemServiceImpl implements ItemService {

    @Override
    public Item.List getItems(Context context, String filter) {
        ItemBeanContainer beanContainer = ItemBeanContainer.getInstance();
        ItemDao itemDao = beanContainer.getItemDao();
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return new Item.List(itemDao.getEntities(database, filter));
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public List<Item> getAllItems(Context context) {
        ItemBeanContainer beanContainer = ItemBeanContainer.getInstance();
        ItemDao itemDao = beanContainer.getItemDao();
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return itemDao.getAllEntities(database);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public List<Item> getItemsByName(Context context, String name) {
        ItemBeanContainer beanContainer = ItemBeanContainer.getInstance();
        ItemDao itemDao = beanContainer.getItemDao();
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return itemDao.getEntitiesByName(database, name);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public Item getItemById(Context context, long id) {
        ItemBeanContainer beanContainer = ItemBeanContainer.getInstance();
        ItemDao itemDao = beanContainer.getItemDao();
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return itemDao.getById(database, id);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public Item getItemByDotaId(Context context, String dotaId) {
        ItemBeanContainer beanContainer = ItemBeanContainer.getInstance();
        ItemDao itemDao = beanContainer.getItemDao();
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return itemDao.getByDotaId(database, dotaId);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public void saveItem(Context context, Item item) {
        ItemBeanContainer beanContainer = ItemBeanContainer.getInstance();
        ItemDao itemDao = beanContainer.getItemDao();
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            itemDao.saveOrUpdate(database, item);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public void saveFromToItems(Context context, List<Item> items) {
        ItemBeanContainer beanContainer = ItemBeanContainer.getInstance();
        ItemDao itemDao = beanContainer.getItemDao();
        DatabaseManager manager = DatabaseManager.getInstance(context);
        for (Item item : items) {
            SQLiteDatabase database = manager.openDatabase();
            try {
                itemDao.bindItems(database, item);
            } finally {
                manager.closeDatabase();
            }
        }
    }

    @Override
    public List<Item> getComplexItems(Context context) {
        ItemBeanContainer beanContainer = ItemBeanContainer.getInstance();
        ItemDao itemDao = beanContainer.getItemDao();
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return itemDao.getComplexItems(database);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public List<Item> getItemsFromThis(Context context, Item item) {
        ItemBeanContainer beanContainer = ItemBeanContainer.getInstance();
        ItemDao itemDao = beanContainer.getItemDao();
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return itemDao.getParentItems(database, item);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public List<Item> getItemsToThis(Context context, Item item) {
        ItemBeanContainer beanContainer = ItemBeanContainer.getInstance();
        ItemDao itemDao = beanContainer.getItemDao();
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return itemDao.getChildItems(database, item);
        } finally {
            manager.closeDatabase();
        }
    }
}
