package com.util.infoparser;

/**
 * Created by ABadretdinov
 * 23.06.2015
 * 15:46
 */
public interface Constants {
    String COSMETIC_ITEM_BASE_URL="http://cdn.dota2.com/apps/570/{0}";
    String COSMETIC_ITEM_ICON_URL="https://api.steampowered.com/IEconDOTA2_570/GetItemIconPath/v1/?key={0}&format=json&iconname={1}";
    String COSMETIC_ITEMS_SCHEME_URL="https://api.steampowered.com/IEconItems_570/GetSchemaURL/v1/?key={0}";
}