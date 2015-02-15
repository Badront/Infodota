package com.badr.infodota.api.responses;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 05.02.14
 * Time: 17:17
 */
public class HeroResponse {
    private String title;
    private String url;
    private String localUrl;
    private List<String> others;

    public HeroResponse() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public List<String> getOthers() {
        return others;
    }

    public void setOthers(List<String> others) {
        this.others = others;
    }
}
