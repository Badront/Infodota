package com.badr.infodota.base.task;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.base.api.trackdota.league.LeagueGameResult;
import com.badr.infodota.base.service.trackdota.TrackdotaService;
import com.badr.infodota.util.retrofit.TaskRequest;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 15:36
 */
public class TrackdotaLeagueGamesLoadRequest extends TaskRequest<LeagueGameResult> {
    private long mLeagueId;

    public TrackdotaLeagueGamesLoadRequest(long leagueId) {
        super(LeagueGameResult.class);
        this.mLeagueId = leagueId;
    }

    @Override
    public LeagueGameResult loadData() throws Exception {
        TrackdotaService trackdotaService = BeanContainer.getInstance().getTrackdotaService();
        return trackdotaService.getLeagueGames(mLeagueId);
    }
}
