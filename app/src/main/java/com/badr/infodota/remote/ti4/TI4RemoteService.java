package com.badr.infodota.remote.ti4;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.remote.BaseRemoteService;

/**
 * User: ABadretdinov
 * Date: 14.05.14
 * Time: 18:48
 */
public interface TI4RemoteService extends BaseRemoteService {
    Pair<Long, String> getPrizePool(Context context) throws Exception;
}
