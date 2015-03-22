package com.badr.infodota.service.match;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.InitializingBean;
import com.badr.infodota.api.matchdetails.MatchDetails;
import com.badr.infodota.api.matchhistory.MatchHistoryResultResponse;
import com.badr.infodota.api.matchhistory.PlayerMatch;
import com.badr.infodota.api.matchhistory.PlayerMatchResult;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 16:18
 */
public interface MatchService{
    MatchDetails getMatchDetails(Context context, String matchId);

    PlayerMatchResult getMatches(Context context, Long accountId, Long fromMatchId, Long heroId);
}
