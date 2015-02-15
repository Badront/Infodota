package com.badr.infodota.remote.ti4;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.R;
import com.badr.infodota.api.Constants;
import com.badr.infodota.api.ti4.PrizePoolHolder;
import com.badr.infodota.remote.BaseRemoteServiceImpl;

import java.text.MessageFormat;

/**
 * User: ABadretdinov
 * Date: 14.05.14
 * Time: 18:48
 */
public class TI4RemoteServiceImpl extends BaseRemoteServiceImpl implements TI4RemoteService {
    @Override
    public Pair<Long, String> getPrizePool(Context context) throws Exception {
        String url = MessageFormat.format(Constants.TI4.PRIZEPOOL, context.getString(R.string.api));
        Pair<PrizePoolHolder, String> result = basicRequestSend(context, url, PrizePoolHolder.class);
        if (result.first != null && result.first.getResult() != null) {
            Long prizePool = result.first.getResult().getPrize_pool();
            return Pair.create(prizePool, result.second);
        }
        return Pair.create(null, result.second);
    }
}
