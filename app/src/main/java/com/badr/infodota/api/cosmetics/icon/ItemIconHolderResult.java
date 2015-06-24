package com.badr.infodota.api.cosmetics.icon;

import java.io.Serializable;

/**
 * Created by ABadretdinov
 * 24.06.2015
 * 12:16
 */
public class ItemIconHolderResult implements Serializable{
    private ItemIconHolder result;

    public ItemIconHolder getResult() {
        return result;
    }

    public void setResult(ItemIconHolder result) {
        this.result = result;
    }
}
