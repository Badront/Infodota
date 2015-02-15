package com.badr.infodota.api.guide;

import java.io.Serializable;

/**
 * User: Histler
 * Date: 19.01.14
 */
public class GuideItems implements Serializable {
    private String[] startingItems;
    private String[] earlyGame;
    private String[] coreItems;
    private String[] luxury;

    public GuideItems() {
    }

    public String[] getStartingItems() {
        return startingItems;
    }

    public void setStartingItems(String[] startingItems) {
        this.startingItems = startingItems;
    }

    public String[] getEarlyGame() {
        return earlyGame;
    }

    public void setEarlyGame(String[] earlyGame) {
        this.earlyGame = earlyGame;
    }

    public String[] getCoreItems() {
        return coreItems;
    }

    public void setCoreItems(String[] coreItems) {
        this.coreItems = coreItems;
    }

    public String[] getLuxury() {
        return luxury;
    }

    public void setLuxury(String[] luxury) {
        this.luxury = luxury;
    }
}
