package com.badr.infodota.base.task;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.trackdota.api.LeaguesHolder;
import com.badr.infodota.trackdota.service.TrackdotaService;
import com.badr.infodota.util.retrofit.TaskRequest;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 15:37
 */
public class LeagueLoadRequest extends TaskRequest<LeaguesHolder> {

    public LeagueLoadRequest() {
        super(LeaguesHolder.class);
    }

    @Override
    public LeaguesHolder loadData() throws Exception {
        BeanContainer container = BeanContainer.getInstance();
        TrackdotaService trackdotaService = container.getTrackdotaService();
        return trackdotaService.getLeagues();
    }
}
