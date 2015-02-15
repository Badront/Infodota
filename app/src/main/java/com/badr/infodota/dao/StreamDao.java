package com.badr.infodota.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.badr.infodota.api.twitch.Channel;

/**
 * Created by ABadretdinov
 * 26.12.2014
 * 16:55
 */
public class StreamDao extends GeneralDaoImpl<Channel> {
    public static final String TABLE_NAME = "streams";

    public static final String COLUMN_CHANNEL = "channel";

    private static final String CREATE_TABLE_QUERY = "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_CHANNEL + " text default null);";

    private static final String[] ALL_COLUMNS = {
            COLUMN_ID,
            COLUMN_CHANNEL
    };

    @Override
    protected String getNoTableNameDataBaseCreateQuery() {
        return CREATE_TABLE_QUERY;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getAllColumns() {
        return ALL_COLUMNS;
    }

    @Override
    public Channel cursorToEntity(Cursor cursor, int index) {
        Channel channel = new Channel();
        int i = index;
        channel.setId(cursor.getLong(i));
        i++;
        channel.setName(cursor.getString(i));
        return channel;
    }

    @Override
    protected ContentValues entityToContentValues(Channel entity) {
        ContentValues values = new ContentValues();
        if (!TextUtils.isEmpty(entity.getName())) {
            values.put(COLUMN_CHANNEL, entity.getName());
        } else {
            values.putNull(COLUMN_CHANNEL);
        }
        return values;
    }

    @Override
    public void delete(SQLiteDatabase database, Channel entity) {
        database.delete(getTableName(), COLUMN_CHANNEL + " =? ", new String[]{entity.getName()});
    }

    @Override
    public void saveOrUpdate(SQLiteDatabase database, Channel entity) {
        Channel entityFromDB = getByName(database, entity.getName());
        if (entityFromDB == null) {
            save(database, entity);
        }
    }

    public Channel getByName(SQLiteDatabase database, String channel) {
        Cursor cursor = database.query(
                getTableName()
                , getAllColumns(), COLUMN_CHANNEL + " = ?"
                , new String[]{channel}, null, null, getDefaultOrderColumns()
        );
        try {
            if (cursor.moveToFirst()) {
                return cursorToEntity(cursor, 0);
            }
            return null;
        } finally {
            cursor.close();
        }
    }
}
