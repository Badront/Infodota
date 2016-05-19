package com.badr.infodota.item.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

/**
 * User: Histler
 * Date: 18.01.14
 */
public class ItemsHolder implements Serializable {
    @SerializedName("itemdata")
    Map<String, Item> items;

    public Map<String, Item> getItems() {
        return items;
    }

    public void setItems(Map<String, Item> items) {
        this.items = items;
    }
}
