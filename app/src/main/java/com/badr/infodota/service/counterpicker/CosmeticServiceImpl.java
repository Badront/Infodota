package com.badr.infodota.service.counterpicker;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.InitializingBean;
import com.badr.infodota.api.cosmetics.player.PlayerCosmeticItem;
import com.badr.infodota.api.cosmetics.price.PricesResult;
import com.badr.infodota.api.cosmetics.store.StoreResult;
import com.badr.infodota.remote.cosmetic.CosmeticsRemoteEntityService;
import com.badr.infodota.util.FileUtils;

import java.io.File;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 13:11
 */
public class CosmeticServiceImpl implements CosmeticService, InitializingBean {
    private CosmeticsRemoteEntityService service;

    @Override
    public Pair<StoreResult, String> getUpdatedCosmeticItems(Context context) {
        try {
            Pair<StoreResult, String> result = service.getCosmeticItems(context);
            if (result.first == null) {
                String message = "Failed to get cosmetic items, cause: " + result.second;
                Log.e(CosmeticServiceImpl.class.getName(), message);
            } else {
                //String storeResults=new Gson().toJson(result.first);
                File externalFilesDir = FileUtils.externalFileDir(context);
                FileUtils.saveJsonFile(externalFilesDir.getAbsolutePath() + File.separator + "store" + File.separator + "storeItems.json",
                        result.first);
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get cosmetic items, cause: " + e.getMessage();
            Log.e(CosmeticServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public Pair<StoreResult, String> getCosmeticItems(Context context) {
        StoreResult storeResult = null;
        String message = null;
        try {
            File externalFilesDir = FileUtils.externalFileDir(context);
            String fileName = externalFilesDir.getAbsolutePath() + File.separator + "store" + File.separator + "storeItems.json";
            if (new File(fileName).exists()) {
                storeResult = FileUtils.entityFromFile(
                        fileName,
                        StoreResult.class);
            }
        } catch (Exception e) {
            message = e.getLocalizedMessage();
            Log.e(CosmeticServiceImpl.class.getName(), message, e);
        }
        return Pair.create(storeResult, message);
    }

    @Override
    public Pair<PricesResult, String> getCosmeticItemsPrices(Context context) {
        PricesResult pricesResult = null;
        String message = null;
        try {
            File externalFilesDir = FileUtils.externalFileDir(context);
            String fileName = externalFilesDir.getAbsolutePath() + File.separator + "store" + File.separator + "storePrices.json";
            if (new File(fileName).exists()) {
                pricesResult = FileUtils.entityFromFile(
                        fileName,
                        PricesResult.class);
            }
        } catch (Exception e) {
            message = e.getMessage();
            Log.e(CosmeticServiceImpl.class.getName(), message, e);
        }
        return Pair.create(pricesResult, message);
    }

    @Override
    public Pair<PricesResult, String> getUpdatedCosmeticItemsPrices(Context context) {
        try {
            Pair<PricesResult, String> result = service.getCosmeticItemsPrices(context);
            if (result.first == null) {
                String message = "Failed to get cosmetic item prices, cause: " + result.second;
                Log.e(CosmeticServiceImpl.class.getName(), message);
            } else {
                //String pricesResults=new Gson().toJson(result.first);
                File externalFilesDir = FileUtils.externalFileDir(context);
                FileUtils.saveJsonFile(externalFilesDir.getAbsolutePath() + File.separator + "store" + File.separator + "storePrices.json",
                        result.first);
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get cosmetic item prices, cause: " + e.getMessage();
            Log.e(CosmeticServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public Pair<List<PlayerCosmeticItem>, String> getPlayersCosmeticItems(Context context, long steam32Id) {
        try {
            Pair<List<PlayerCosmeticItem>, String> result = service.getPlayersCosmeticItems(context, steam32Id);
            if (result.first == null) {
                String message = "Failed to get cosmetic items for player, cause: " + result.second;
                Log.e(CosmeticServiceImpl.class.getName(), message);
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get cosmetic items for player, cause: " + e.getMessage();
            Log.e(CosmeticServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public void initialize() {
        BeanContainer container = BeanContainer.getInstance();
        service = container.getCosmeticsRemoteEntityService();
    }
}
