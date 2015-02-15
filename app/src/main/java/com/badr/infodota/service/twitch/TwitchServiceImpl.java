package com.badr.infodota.service.twitch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.twitch.AccessToken;
import com.badr.infodota.api.twitch.Channel;
import com.badr.infodota.dao.DatabaseManager;
import com.badr.infodota.dao.StreamDao;
import com.badr.infodota.remote.twitch.TwitchRemoteService;
import com.parser.Playlist;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 16.05.14
 * Time: 17:58
 */
public class TwitchServiceImpl implements TwitchService {
    private TwitchRemoteService service;
    private StreamDao streamDao;

    @Override
    public Pair<AccessToken, String> getAccessToken(Context context, String channelName) {
        try {
            Pair<AccessToken, String> result = service.getAccessToken(context, channelName);
            if (result.first == null) {
                String message = "Failed to get twitch access token, cause: " + result.second;
                Log.e(TwitchServiceImpl.class.getName(), message);
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get twitch access token, cause: " + e.getMessage();
            Log.e(TwitchServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public Pair<Playlist, String> getPlaylist(Context context, String channelName, AccessToken accessToken) {
        try {
            Pair<Playlist, String> result = service.getPlaylist(context, channelName, accessToken);
            if (result.first == null) {
                String message = "Failed to get twitch channel playlist, cause: " + result.second;
                Log.e(TwitchServiceImpl.class.getName(), message);
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get twitch channel playlist, cause: " + e.getMessage();
            Log.e(TwitchServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public boolean isStreamFavourite(Context context, Channel channel) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return streamDao.getByName(database, channel.getName()) != null;
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public void addStream(Context context, Channel channel) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            streamDao.saveOrUpdate(database, channel);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public void deleteStream(Context context, Channel channel) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            streamDao.delete(database, channel);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public List<Channel> getFavouriteStreams(Context context) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return streamDao.getAllEntities(database);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public void initialize() {
        BeanContainer container = BeanContainer.getInstance();
        service = container.getTwitchRemoteService();
        streamDao = container.getStreamDao();
    }
}
