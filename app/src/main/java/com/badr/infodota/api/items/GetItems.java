package com.badr.infodota.api.items;

import java.util.Map;

/**
 * User: Histler
 * Date: 18.01.14
 */
public class GetItems {
    Map<String, Item> itemdata;

    public GetItems() {
    }

    public Map<String, Item> getItemdata() {
        return itemdata;
    }

    public void setItemdata(Map<String, Item> itemdata) {
        this.itemdata = itemdata;
    }
}
