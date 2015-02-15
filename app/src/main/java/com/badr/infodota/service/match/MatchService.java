package com.badr.infodota.service.match;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.InitializingBean;
import com.badr.infodota.api.matchdetails.MatchDetails;
import com.badr.infodota.api.matchhistory.ResultResponse;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 16:18
 */
public interface MatchService extends InitializingBean {
    Pair<MatchDetails, String> getMatchDetails(Context context, String matchId);

    Pair<ResultResponse, String> getMatches(Context context, long accountId, long fromMatchId, String extraParams);

    Pair<ResultResponse, String> getMatches(Context context, long fromMatchId, String extraParams);
}
