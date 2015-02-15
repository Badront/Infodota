package com.badr.infodota.remote.cosmetic;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.R;
import com.badr.infodota.api.Constants;
import com.badr.infodota.api.cosmetics.player.PlayerCosmeticItem;
import com.badr.infodota.api.cosmetics.player.PlayerCosmeticItemsResult;
import com.badr.infodota.api.cosmetics.price.PricesResult;
import com.badr.infodota.api.cosmetics.store.StoreResult;
import com.badr.infodota.remote.BaseRemoteServiceImpl;

import java.text.MessageFormat;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 12:37
 */
public class CosmeticsRemoteEntityServiceImpl extends BaseRemoteServiceImpl implements CosmeticsRemoteEntityService {
    @Override
    public Pair<StoreResult, String> getCosmeticItems(Context context) throws Exception {
        String locale = context.getString(R.string.language);
        locale = locale.substring(0, 2);

        String url = MessageFormat.format(Constants.Cosmetics.SUBURL, context.getString(R.string.api), locale);
        return basicRequestSend(context, url, StoreResult.class);
        /*Pair<StoreUrlResult,String> result=basicRequestSend(context,url,StoreUrlResult.class);
        if(result!=null&&result.first!=null){
            url=result.first.getResult().getUrl();
            Pair<String,String> cosmeticItems=basicRequestSend(context,url);
            if(cosmeticItems!=null&&cosmeticItems.first!=null){
                String json = VDFtoJsonParser.parse(cosmeticItems.first);
                System.out.println(json);
                return Pair.create(new Gson().fromJson(json,StoreResult.class),cosmeticItems.second);
            }
        }
        return Pair.create(null, context.getString(R.string.error_loading));*/
    }

    @Override
    public Pair<PricesResult, String> getCosmeticItemsPrices(Context context) throws Exception {
        String url = Constants.Cosmetics.PRICES_URL + context.getString(R.string.api);
        return basicRequestSend(context, url, PricesResult.class);
    }

    @Override
    public Pair<List<PlayerCosmeticItem>, String> getPlayersCosmeticItems(Context context, long steam32Id)
            throws Exception {
        long steam64id = 76561197960265728L + steam32Id;
        String url = MessageFormat.format(Constants.Cosmetics.PLAYER_ITEMS_URL, context.getString(R.string.api), String.valueOf(steam64id));

        Pair<PlayerCosmeticItemsResult, String> result = basicRequestSend(context, url, PlayerCosmeticItemsResult.class);
        return Pair.create(result.first.getResult().getItems(), result.second);//not null?
    }
}
