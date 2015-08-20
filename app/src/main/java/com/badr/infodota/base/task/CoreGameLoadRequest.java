package com.badr.infodota.base.task;

import android.content.Context;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.base.api.trackdota.core.CoreResult;
import com.badr.infodota.base.service.trackdota.TrackdotaService;
import com.badr.infodota.util.retrofit.TaskRequest;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 15:10
 */
public class CoreGameLoadRequest extends TaskRequest<CoreResult> {
    private long mMatchId;
    private Context mContext;

    public CoreGameLoadRequest(Context context, long matchId) {
        super(CoreResult.class);
        this.mMatchId = matchId;
        this.mContext = context;
    }

    @Override
    public CoreResult loadData() throws Exception {
        BeanContainer container = BeanContainer.getInstance();
        TrackdotaService trackdotaService = container.getTrackdotaService();
        return trackdotaService.getGameCoreData(mContext, mMatchId);
    }
}
