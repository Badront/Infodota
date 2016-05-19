package com.badr.infodota.item.util;

import android.text.TextUtils;

/**
 * Created by ABadretdinov
 * 17.03.2016
 * 15:06
 */
public final class ItemUtils {
    public static String getItemImage(String itemDotaId) {
        String name = itemDotaId;
        if (TextUtils.isEmpty(name)) {
            name = "recipe";
        }
        return "file:///android_asset/items/" + name + ".png";
    }
}
