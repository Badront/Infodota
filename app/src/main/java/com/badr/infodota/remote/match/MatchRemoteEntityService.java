package com.badr.infodota.remote.match;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.api.matchdetails.MatchDetails;
import com.badr.infodota.api.matchhistory.ResultResponse;
import com.badr.infodota.remote.BaseRemoteService;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 16:07
 */
public interface MatchRemoteEntityService extends BaseRemoteService {
    Pair<MatchDetails, String> getMatchDetails(Context context, String matchId) throws Exception;

    Pair<ResultResponse, String> getMatches(Context context, long accountId, long fromMatchId, String extraParams) throws Exception;

    Pair<ResultResponse, String> getMatches(Context context, long fromMatchId, String extraParams) throws Exception;
}
