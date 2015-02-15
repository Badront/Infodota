package com.badr.infodota.remote.player;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.remote.BaseRemoteService;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 15:49
 */
public interface PlayerRemoteService extends BaseRemoteService {
    Pair<List<Unit>, String> getAccounts(Context context, List<Long> ids) throws Exception;

    Pair<Unit, String> getAccount(Context context, long id) throws Exception;

    Pair<List<Unit>, String> getAccounts(Context context, String name) throws Exception;

    Pair<List<Unit>, String> getFriends(Context context, long id) throws Exception;
}
