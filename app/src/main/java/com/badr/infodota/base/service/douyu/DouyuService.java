package com.badr.infodota.base.service.douyu;

import com.badr.infodota.InitializingBean;
import com.badr.infodota.base.api.streams.Stream;

/**
 * Created by ABadretdinov
 * 26.05.2015
 * 14:47
 */
public interface DouyuService extends InitializingBean {
    Stream getStream(Stream stream);
}
