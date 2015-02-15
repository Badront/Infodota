package com.badr.infodota.service.match;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.matchdetails.MatchDetails;
import com.badr.infodota.api.matchhistory.ResultResponse;
import com.badr.infodota.remote.match.MatchRemoteEntityService;
import com.badr.infodota.util.FileUtils;
import com.google.gson.Gson;

import java.io.File;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 16:18
 */
public class MatchServiceImpl implements MatchService {
    private MatchRemoteEntityService service;

    @Override
    public Pair<MatchDetails, String> getMatchDetails(Context context, String matchId) {
        try {
            File externalFilesDir = FileUtils.externalFileDir(context);
            String matchResult = FileUtils.getTextFromFile(externalFilesDir.getAbsolutePath() + File.separator + "matches" + File.separator + matchId + ".json");
            Pair<MatchDetails, String> result;
            if (TextUtils.isEmpty(matchResult)) {
                result = service.getMatchDetails(context, matchId);
                if (result.first == null) {
                    String message = "Failed to get match, cause: " + result.second;
                    Log.e(MatchServiceImpl.class.getName(), message);
                } else {
                    FileUtils.saveJsonFile(externalFilesDir.getAbsolutePath() + File.separator + "matches" + File.separator + matchId + ".json",
                            result.first);
                }
            } else {
                result = Pair.create(new Gson().fromJson(matchResult, MatchDetails.class), "");
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get match, cause: " + e.getMessage();
            Log.e(MatchServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public Pair<ResultResponse, String> getMatches(Context context, long accountId, long fromMatchId,
                                                   String extraParams) {
        try {
            Pair<ResultResponse, String> result = service.getMatches(context, accountId, fromMatchId, extraParams);
            if (result.first == null) {
                String message = "Failed to get matches ";
                Log.e(MatchServiceImpl.class.getName(), message);
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get matches, cause: " + e.getMessage();
            Log.e(MatchServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public Pair<ResultResponse, String> getMatches(Context context, long fromMatchId, String extraParams) {

        try {
            Pair<ResultResponse, String> result = service.getMatches(context, fromMatchId, extraParams);
            if (result.first == null) {
                String message = "Failed to get matches ";
                Log.e(MatchServiceImpl.class.getName(), message);
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get matches, cause: " + e.getMessage();
            Log.e(MatchServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public void initialize() {
        BeanContainer container = BeanContainer.getInstance();
        service = container.getMatchRemoteEntityService();
    }
}
