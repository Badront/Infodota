package com.badr.infodota.service.joindota;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.joindota.LiveStream;
import com.badr.infodota.api.joindota.MatchItem;
import com.badr.infodota.remote.joindota.JoinDotaRemoteService;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 22.04.14
 * Time: 18:13
 */
public class JoinDotaServiceImpl implements JoinDotaService {
    private JoinDotaRemoteService service;

    @Override
    public MatchItem.List getMatchItems(Context context, int page, String extraParams) {
        try {
            MatchItem.List result = service.getMatchItems(context, page, extraParams);
            if (result == null) {
                Log.e(JoinDotaServiceImpl.class.getName(), "Failed to get joinDota match list");
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get joinDota match list, cause: " + e.getMessage();
            Log.e(JoinDotaServiceImpl.class.getName(), message, e);
            return null;
        }
    }

    @Override
    public Pair<MatchItem, String> updateMatchItem(Context context, MatchItem item) {
        try {
            String errorMsg = null;
            MatchItem result = service.updateMatchItem(context, item);
            if (result == null) {
                errorMsg = "Failed to get joinDota match item";
                Log.e(JoinDotaServiceImpl.class.getName(), errorMsg);
            }
            return Pair.create(result, errorMsg);
        } catch (Exception e) {
            String message = "Failed to get joinDota match item, cause: " + e.getMessage();
            Log.e(JoinDotaServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public String fillChannelName(Context context, List<LiveStream> streams) {
        String message = "";
        try {
            service.getChannelsNames(context, streams);
        } catch (Exception e) {
            message = "Failed to get live streams, cause: " + e.getMessage();
            Log.e(JoinDotaServiceImpl.class.getName(), message, e);
        }
        return message;
    }

    @Override
    public void initialize() {
        BeanContainer container = BeanContainer.getInstance();
        service = container.getJoinDotaRemoteService();
    }
}
