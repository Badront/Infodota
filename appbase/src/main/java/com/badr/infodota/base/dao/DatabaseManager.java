package com.badr.infodota.base.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ABadretdinov
 * 25.12.2014
 * 11:27
 */
public class DatabaseManager {
    private static DatabaseManager sInstance;
    private static DatabaseHelper sDatabaseHelper;
    private AtomicInteger mOpenCounter = new AtomicInteger();
    private SQLiteDatabase mDatabase;

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseManager();
            sDatabaseHelper = new DatabaseHelper(context.getApplicationContext());
        }
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (sInstance == null) {
            initializeInstance(context);
        }
        return sInstance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            mDatabase = sDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            mDatabase.close();
        }
    }
}
