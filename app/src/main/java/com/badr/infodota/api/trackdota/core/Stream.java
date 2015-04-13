package com.badr.infodota.api.trackdota.core;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ABadretdinov
 * 13.04.2015
 * 17:51
 */
public class Stream implements Serializable {
    @Expose
    private String channel;
    @Expose
    private long viewers;
    @Expose
    @SerializedName("embed_id")
    private String embedId;
    @Expose
    private String title;
    @Expose
    private String language;
    @Expose
    private String provider;

    @Expose
    @SerializedName("hls_streams")
    private List<StreamQuality> qualities;
    @Expose
    @SerializedName("lang_confirmed")
    private boolean isLangConfirmed;
    @Expose
    @SerializedName("hls_enabled")
    private boolean isHlsEnabled;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public long getViewers() {
        return viewers;
    }

    public void setViewers(long viewers) {
        this.viewers = viewers;
    }

    public String getEmbedId() {
        return embedId;
    }

    public void setEmbedId(String embedId) {
        this.embedId = embedId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public List<StreamQuality> getQualities() {
        return qualities;
    }

    public void setQualities(List<StreamQuality> qualities) {
        this.qualities = qualities;
    }

    public boolean isLangConfirmed() {
        return isLangConfirmed;
    }

    public void setLangConfirmed(boolean isLangConfirmed) {
        this.isLangConfirmed = isLangConfirmed;
    }

    public boolean isHlsEnabled() {
        return isHlsEnabled;
    }

    public void setHlsEnabled(boolean isHlsEnabled) {
        this.isHlsEnabled = isHlsEnabled;
    }
}
