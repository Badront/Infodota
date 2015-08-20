package com.badr.infodota.base.api.playersummaries;

import com.google.gson.annotations.SerializedName;

/**
 * User: Histler
 * Date: 16.04.14
 */
public class PlayersResult {
    @SerializedName("response")
    private PlayersHolder playersHolder;

    public PlayersHolder getPlayersHolder() {
        return playersHolder;
    }

    public void setPlayersHolder(PlayersHolder playersHolder) {
        this.playersHolder = playersHolder;
    }
}
