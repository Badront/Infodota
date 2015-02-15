package com.badr.infodota.remote.match;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.R;
import com.badr.infodota.api.Constants;
import com.badr.infodota.api.matchdetails.MatchDetails;
import com.badr.infodota.api.matchhistory.ResultResponse;
import com.badr.infodota.remote.BaseRemoteServiceImpl;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 16:14
 */
public class MatchRemoteEntityServiceImpl extends BaseRemoteServiceImpl implements MatchRemoteEntityService {
    @Override
    public Pair<MatchDetails, String> getMatchDetails(Context context, String matchId) throws Exception {
        String url = Constants.Details.SUBURL + context.getString(R.string.api) + Constants.Details.MATCH_ID + matchId;
        return basicRequestSend(context, url, MatchDetails.class);
    }

    @Override
    public Pair<ResultResponse, String> getMatches(Context context, long accountId, long fromMatchId,
                                                   String extraParams) throws Exception {
        StringBuilder url = new StringBuilder(Constants.History.SUBURL);
        url.append(context.getString(R.string.api));
        url.append(Constants.Details.ACCOUNT_ID);
        url.append(accountId);
        if (fromMatchId != 0) {
            url.append(Constants.History.START_AT_MATCH_ID);
            url.append(fromMatchId);
        }
        if (extraParams != null) {
            url.append(extraParams);
        }
        return basicRequestSend(context, url.toString(), ResultResponse.class);
    }

    @Override
    public Pair<ResultResponse, String> getMatches(Context context, long fromMatchId, String extraParams) throws Exception {
        StringBuilder url = new StringBuilder(Constants.History.SUBURL);
        url.append(context.getString(R.string.api));
        if (fromMatchId != 0) {
            url.append(Constants.History.START_AT_MATCH_ID);
            url.append(fromMatchId);
        }
        url.append(extraParams);
        return basicRequestSend(context, url.toString(), ResultResponse.class);
    }
}
