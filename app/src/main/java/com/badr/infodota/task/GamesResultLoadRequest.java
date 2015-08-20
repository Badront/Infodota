package com.badr.infodota.task;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.trackdota.game.GamesResult;
import com.badr.infodota.service.trackdota.TrackdotaService;
import com.badr.infodota.util.retrofit.TaskRequest;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 15:15
 */
public class GamesResultLoadRequest extends TaskRequest<GamesResult> {
    public GamesResultLoadRequest() {
        super(GamesResult.class);
    }

    @Override
    public GamesResult loadData() throws Exception {
        BeanContainer container = BeanContainer.getInstance();
        TrackdotaService trackdotaService = container.getTrackdotaService();
        return trackdotaService.getGames();
    }
}
