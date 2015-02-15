package com.badr.infodota.remote.team;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.remote.BaseRemoteService;

/**
 * User: ABadretdinov
 * Date: 15.05.14
 * Time: 16:53
 */
public interface TeamRemoteService extends BaseRemoteService {
    Pair<String, String> getTeamLogo(Context context, Long logoId) throws Exception;
}
