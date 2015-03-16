package com.badr.infodota.service.match;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.matchdetails.MatchDetails;
import com.badr.infodota.api.matchhistory.MatchHistoryResultResponse;
import com.badr.infodota.util.FileUtils;
import com.google.gson.Gson;

import java.io.File;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 16:18
 */
public class MatchServiceImpl implements MatchService {

    @Override
    public Pair<MatchDetails, String> getMatchDetails(Context context, String matchId) {
        try {
            File externalFilesDir = FileUtils.externalFileDir(context);
            String matchResult = FileUtils.getTextFromFile(externalFilesDir.getAbsolutePath() + File.separator + "matches" + File.separator + matchId + ".json");
            MatchDetails result;
            String message=null;
            if (TextUtils.isEmpty(matchResult)) {
                result = BeanContainer.getInstance().getSteamService().getMatchDetails(matchId);
                if (result == null) {
                    message = "Failed to get match id:"+matchId;
                    Log.e(MatchServiceImpl.class.getName(), message);
                } else {
                    FileUtils.saveJsonFile(externalFilesDir.getAbsolutePath() + File.separator + "matches" + File.separator + matchId + ".json",
                            result);
                }
            } else {
                result = new Gson().fromJson(matchResult, MatchDetails.class);
            }
            return Pair.create(result,message);
        } catch (Exception e) {
            String message = "Failed to get match, cause: " + e.getMessage();
            Log.e(MatchServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public Pair<MatchHistoryResultResponse, String> getMatches(Context context, Long accountId, Long fromMatchId,
                                                   Long heroId) {
        try {
            MatchHistoryResultResponse result = BeanContainer.getInstance().getSteamService().getMatchHistory(accountId, fromMatchId, heroId);
            String message = null;
            if (result == null) {
                message = "Failed to get matches for accountId=" + accountId;
                Log.e(MatchServiceImpl.class.getName(), message);
            }
            return Pair.create(result, message);
        } catch (Exception e) {
            String message = "Failed to get matches, cause: " + e.getMessage();
            Log.e(MatchServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }
}
