package com.badr.infodota.base.api.team;

import java.io.Serializable;

/**
 * User: ABadretdinov
 * Date: 15.05.14
 * Time: 17:01
 */
public class LogoDataHolder implements Serializable {
    private LogoData data;

    public LogoData getData() {
        return data;
    }

    public void setData(LogoData data) {
        this.data = data;
    }

    public String getUrl() {
        if (data != null) {
            return data.getUrl();
        }
        return null;
    }
}
