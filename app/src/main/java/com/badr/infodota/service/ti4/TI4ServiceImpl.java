package com.badr.infodota.service.ti4;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.ti4.PrizePoolHolder;

/**
 * User: ABadretdinov
 * Date: 14.05.14
 * Time: 18:55
 */
public class TI4ServiceImpl implements TI4Service {

    @Override
    public Pair<Long, String> getPrizePool(Context context) {
        try {
            PrizePoolHolder result =BeanContainer.getInstance().getSteamService().getLeaguePrizePool(600);
            if (result == null) {
                String message = "Failed to get prizepool";
                Log.e(TI4ServiceImpl.class.getName(), message);
                return Pair.create(null,message);
            }
            else {
                return Pair.create(result.getResult().getPrize_pool(),null);
            }
        } catch (Exception e) {
            String message = "Failed to get prizepool, cause:" + e.getMessage();
            Log.e(TI4ServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }
}
