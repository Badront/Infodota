package com.badr.infodota.task;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.joindota.MatchItem;
import com.badr.infodota.service.joindota.JoinDotaService;
import com.badr.infodota.util.retrofit.TaskRequest;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 14:49
 */
public class MatchItemLoadRequest extends TaskRequest<MatchItem> {

    private MatchItem mMatchItem;

    public MatchItemLoadRequest(MatchItem matchItem) {
        super(MatchItem.class);
        this.mMatchItem = matchItem;
    }

    @Override
    public MatchItem loadData() throws Exception {
        JoinDotaService service = BeanContainer.getInstance().getJoinDotaService();
        return service.updateMatchItem(mMatchItem);
    }
}