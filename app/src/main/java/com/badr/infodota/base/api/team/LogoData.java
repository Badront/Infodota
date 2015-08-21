package com.badr.infodota.base.api.team;

import java.io.Serializable;

/**
 * User: ABadretdinov
 * Date: 15.05.14
 * Time: 17:01
 */
public class LogoData implements Serializable {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
