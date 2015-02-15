package com.badr.infodota.service.ti4;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.remote.ti4.TI4RemoteService;

/**
 * User: ABadretdinov
 * Date: 14.05.14
 * Time: 18:55
 */
public class TI4ServiceImpl implements TI4Service {
    private TI4RemoteService service;

    @Override
    public Pair<Long, String> getPrizePool(Context context) {
        try {
            Pair<Long, String> result = service.getPrizePool(context);
            if (result.first == null) {
                String message = "Failed to get prizepool, cause:" + result.second;
                Log.e(TI4ServiceImpl.class.getName(), message);
            }
            return result;
        } catch (Exception e) {
            String message = "Failed to get prizepool, cause:" + e.getMessage();
            Log.e(TI4ServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public void initialize() {
        BeanContainer container = BeanContainer.getInstance();
        service = container.getTi4RemoteService();
    }
}
