package com.badr.infodota.service.trackdota;

import com.badr.infodota.InitializingBean;
import com.badr.infodota.api.trackdota.core.CoreResult;
import com.badr.infodota.api.trackdota.game.GamesResult;
import com.badr.infodota.api.trackdota.live.LiveGame;

/**
 * Created by ABadretdinov
 * 13.04.2015
 * 17:17
 */
public interface TrackdotaService extends InitializingBean {

    LiveGame getLiveGame(long gameId);

    CoreResult getGameCoreData(long gameId);

    GamesResult getGames();
}
