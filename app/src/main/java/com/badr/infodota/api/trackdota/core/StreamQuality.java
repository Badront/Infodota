package com.badr.infodota.api.trackdota.core;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by ABadretdinov
 * 13.04.2015
 * 17:53
 */
public class StreamQuality implements Serializable{
    @Expose
    private String protocol;
    @Expose
    private String quality;
    @Expose
    private String url;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
