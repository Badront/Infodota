package com.badr.infodota.service.match;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.InitializingBean;
import com.badr.infodota.api.matchdetails.MatchDetails;
import com.badr.infodota.api.matchhistory.MatchHistoryResultResponse;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 16:18
 */
public interface MatchService{
    Pair<MatchDetails, String> getMatchDetails(Context context, String matchId);

    Pair<MatchHistoryResultResponse, String> getMatches(Context context, Long accountId, Long fromMatchId, Long heroId);

}
