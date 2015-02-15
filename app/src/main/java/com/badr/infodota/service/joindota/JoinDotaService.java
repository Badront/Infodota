package com.badr.infodota.service.joindota;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.InitializingBean;
import com.badr.infodota.api.joindota.LiveStream;
import com.badr.infodota.api.joindota.MatchItem;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 22.04.14
 * Time: 18:12
 */
public interface JoinDotaService extends InitializingBean {
    Pair<List<MatchItem>, String> getMatchItems(Context context, int page, String extraParams);

    Pair<MatchItem, String> updateMatchItem(Context context, MatchItem item);

    String fillChannelName(Context context, List<LiveStream> streams);
}
