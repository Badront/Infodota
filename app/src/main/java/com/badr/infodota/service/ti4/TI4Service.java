package com.badr.infodota.service.ti4;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.InitializingBean;

/**
 * User: ABadretdinov
 * Date: 14.05.14
 * Time: 18:55
 */
public interface TI4Service extends InitializingBean {
    Pair<Long, String> getPrizePool(Context context);
}
