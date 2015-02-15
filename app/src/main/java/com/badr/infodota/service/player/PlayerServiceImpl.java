package com.badr.infodota.service.player;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.dao.AccountDao;
import com.badr.infodota.dao.DatabaseManager;
import com.badr.infodota.remote.player.PlayerRemoteService;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 16:01
 */
public class PlayerServiceImpl implements PlayerService {
    private PlayerRemoteService service;
    private AccountDao accountDao;

    @Override
    public void initialize() {
        BeanContainer container = BeanContainer.getInstance();
        service = container.getPlayerRemoteService();
        accountDao = container.getAccountDao();
    }

    @Override
    public Pair<List<Unit>, String> loadAccounts(Context context, List<Long> ids) {
        try {
            Pair<List<Unit>, String> result = service.getAccounts(context, ids);
            if (result.first == null) {
                String message = "Failed to get players, cause: " + result.second;
                Log.e(PlayerServiceImpl.class.getName(), message);
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get players, cause: " + e.getMessage();
            Log.e(PlayerServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public Pair<List<Unit>, String> loadAccounts(Context context, String name) {
        try {
            Pair<List<Unit>, String> result = service.getAccounts(context, name);
            if (result.first == null) {
                String message = "Failed to get players, cause: " + result.second;
                Log.e(PlayerServiceImpl.class.getName(), message);
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get players, cause: " + e.getMessage();
            Log.e(PlayerServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public Pair<List<Unit>, String> loadFriends(Context context, long id) {
        try {
            Pair<List<Unit>, String> result = service.getFriends(context, id);
            if (result.first == null) {
                String message = "Failed to get friends, cause: " + result.second;
                Log.e(PlayerServiceImpl.class.getName(), message);
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get friends, cause: " + e.getMessage();
            Log.e(PlayerServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public void saveAccount(Context context, Unit unit) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            accountDao.saveOrUpdate(database, unit);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public Unit getAccountById(Context context, long id) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return accountDao.getById(database, id);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public void deleteAccount(Context context, Unit unit) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            accountDao.delete(database, unit);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public List<Unit> getSearchedAccounts(Context context) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return accountDao.getSearchedEntities(database);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public List<Unit> getAccountsByGroup(Context context, Unit.Groups group) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return accountDao.getEntitiesByGroup(database, group);
        } finally {
            manager.closeDatabase();
        }
    }
}
