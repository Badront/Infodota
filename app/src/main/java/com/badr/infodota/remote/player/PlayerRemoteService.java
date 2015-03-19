package com.badr.infodota.remote.player;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.remote.BaseRemoteService;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 15:49
 */
public interface PlayerRemoteService{

    List<Unit> getAccounts(List<Long> ids) throws Exception;

    List<Unit> getAccounts(String name) throws Exception;

    List<Unit> getFriends(long id) throws Exception;
}
