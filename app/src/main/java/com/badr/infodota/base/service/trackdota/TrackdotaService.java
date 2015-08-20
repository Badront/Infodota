package com.badr.infodota.base.service.trackdota;

import android.content.Context;

import com.badr.infodota.InitializingBean;
import com.badr.infodota.base.api.trackdota.LeaguesResult;
import com.badr.infodota.base.api.trackdota.core.CoreResult;
import com.badr.infodota.base.api.trackdota.game.GamesResult;
import com.badr.infodota.base.api.trackdota.league.LeagueGameResult;
import com.badr.infodota.base.api.trackdota.live.LiveGame;

/**
 * Created by ABadretdinov
 * 13.04.2015
 * 17:17
 */
public interface TrackdotaService extends InitializingBean {

    LiveGame getLiveGame(Context context, long gameId);

    CoreResult getGameCoreData(Context context, long gameId);

    GamesResult getGames();

    LeaguesResult getLeagues();

    LeagueGameResult getLeagueGames(long leagueId);
}
