package com.badr.infodota.service.player;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.InitializingBean;
import com.badr.infodota.api.dotabuff.Unit;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 16:01
 */
public interface PlayerService extends InitializingBean {
    Pair<List<Unit>, String> loadAccounts(List<Long> ids);

    Pair<List<Unit>, String> loadAccounts(String name);

    List<Unit> loadFriends(long id);

    void saveAccount(Context context, Unit unit);

    Unit getAccountById(Context context, long id);

    void deleteAccount(Context context, Unit unit);

    List<Unit> getSearchedAccounts(Context context);

    List<Unit> getAccountsByGroup(Context context, Unit.Groups group);

}
