package com.badr.infodota.task;

import android.content.Context;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.joindota.MatchItem;
import com.badr.infodota.service.joindota.JoinDotaService;
import com.badr.infodota.util.retrofit.TaskRequest;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 15:49
 */
public class MatchItemsLoadRequest extends TaskRequest<MatchItem.List> {

    private int mPage;
    private Context mContext;
    private String mExtraParams;

    public MatchItemsLoadRequest(Context context, String extraParams, int page) {
        super(MatchItem.List.class);
        this.mPage = page;
        this.mContext = context;
        this.mExtraParams = extraParams;
    }

    @Override
    public MatchItem.List loadData() throws Exception {
        BeanContainer container = BeanContainer.getInstance();
        JoinDotaService joinDotaService = container.getJoinDotaService();
        return joinDotaService.getMatchItems(mContext, mPage, mExtraParams);
    }
}
