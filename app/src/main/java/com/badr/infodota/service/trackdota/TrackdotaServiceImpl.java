package com.badr.infodota.service.trackdota;

import android.util.Log;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.trackdota.core.CoreResult;
import com.badr.infodota.api.trackdota.game.GamesResult;
import com.badr.infodota.api.trackdota.live.LiveGame;
import com.badr.infodota.remote.TrackdotaRestService;

/**
 * Created by ABadretdinov
 * 13.04.2015
 * 17:39
 */
public class TrackdotaServiceImpl implements TrackdotaService {
    private TrackdotaRestService service;

    @Override
    public LiveGame getLiveGame(long gameId) {
        try {
            return service.getLiveGame(gameId);
        } catch (Exception e) {
            String message = "Failed to get trackdota live game, cause:" + e.getMessage();
            Log.e(getClass().getName(), message);
        }
        return null;
    }
    @Override
    public CoreResult getGameCoreData(long gameId) {
        try {
            return service.getGameCoreData(gameId);
        } catch (Exception e) {
            String message = "Failed to get trackdota core data, cause:" + e.getMessage();
            Log.e(getClass().getName(), message);
        }
        return null;
    }

    @Override
    public GamesResult getGames() {
        try {
            return service.getGames();
        } catch (Exception e) {
            String message = "Failed to get trackdota games, cause:" + e.getMessage();
            Log.e(getClass().getName(), message);
        }
        return null;
    }

    @Override
    public void initialize() {
        service = BeanContainer.getInstance().getTrackdotaRestService();
    }
}
