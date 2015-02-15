package com.badr.infodota.remote.cosmetic;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.api.cosmetics.player.PlayerCosmeticItem;
import com.badr.infodota.api.cosmetics.price.PricesResult;
import com.badr.infodota.api.cosmetics.store.StoreResult;
import com.badr.infodota.remote.BaseRemoteService;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 12:32
 */
public interface CosmeticsRemoteEntityService extends BaseRemoteService {
    Pair<StoreResult, String> getCosmeticItems(Context context) throws Exception;

    Pair<PricesResult, String> getCosmeticItemsPrices(Context context) throws Exception;

    Pair<List<PlayerCosmeticItem>, String> getPlayersCosmeticItems(Context context, long steam32Id) throws Exception;
}
