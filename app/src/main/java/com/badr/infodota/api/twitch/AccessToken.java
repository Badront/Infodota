package com.badr.infodota.api.twitch;

/**
 * User: Histler
 * Date: 27.02.14
 */
public class AccessToken {
    private String token;
    private String sig;

    public AccessToken() {
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
