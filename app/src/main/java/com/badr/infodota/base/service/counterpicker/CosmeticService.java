package com.badr.infodota.base.service.counterpicker;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.base.api.cosmetics.player.PlayerCosmeticItem;
import com.badr.infodota.base.api.cosmetics.price.PricesResult;
import com.badr.infodota.base.api.cosmetics.store.StoreResult;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 13:04
 */
public interface CosmeticService {
    Pair<StoreResult, String> getCosmeticItems(Context context);

    Pair<StoreResult, String> getUpdatedCosmeticItems(Context context);

    Pair<PricesResult, String> getCosmeticItemsPrices(Context context);

    Pair<PricesResult, String> getUpdatedCosmeticItemsPrices(Context context);

    Pair<List<PlayerCosmeticItem>, String> getPlayersCosmeticItems(Context context, long steam32Id);

}
