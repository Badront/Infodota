package com.badr.infodota.base.task;

import android.content.Context;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.base.api.dotabuff.Unit;
import com.badr.infodota.base.service.player.PlayerService;
import com.badr.infodota.util.retrofit.TaskRequest;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 16:58
 */
public class UtilsGroupLoadRequest extends TaskRequest<Unit.List> {

    private Unit.Groups mGroup;
    private Context mContext;

    public UtilsGroupLoadRequest(Context context, Unit.Groups group) {
        super(Unit.List.class);
        mContext = context;
        mGroup = group;
    }

    @Override
    public Unit.List loadData() throws Exception {
        PlayerService playerService = BeanContainer.getInstance().getPlayerService();
        return playerService.getAccountsByGroup(mContext, mGroup);

    }
}