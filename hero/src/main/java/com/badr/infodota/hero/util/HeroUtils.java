package com.badr.infodota.hero.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.badr.infodota.base.util.FileUtils;
import com.badr.infodota.hero.entity.FullAbility;
import com.google.gson.Gson;

/**
 * Created by ABadretdinov
 * 15.03.2016
 * 16:09
 */
public class HeroUtils {

    public static String getHeroFullImage(String heroDotaId) {
        return "file:///android_asset/heroes/" + heroDotaId + "/full.png";
    }

    public static String getHeroPortraitImage(String heroDotaId) {
        return "file:///android_asset/heroes/" + heroDotaId + "/vert.jpg";
    }

    public static String getHeroMiniImage(String heroDotaId) {
        return "file:///android_asset/heroes/" + heroDotaId + "/mini.png";
    }

    public static String getSkillImage(String skillName) {
        return "file:///android_asset/skills/" + skillName + ".png";
    }

    public static FullAbility.List getHeroAbilities(Context context, String heroDotaId) {
        String locale = context.getString(R.string.language);
        String json = FileUtils
                .getTextFromAsset(context, "heroes/" + heroDotaId + "/skills_" + locale + ".json");
        return new Gson().fromJson(json, FullAbility.List.class);
    }

    public static Drawable getHeroMiniIcon(Context context, String heroDotaId) {
        final TypedArray styledAttributes = context.getTheme()
                .obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        int mActionBarSize = (int) styledAttributes.getDimension(0, 40) / 2;
        styledAttributes.recycle();
        Bitmap icon = FileUtils.getBitmapFromAsset(context, "heroes/" + heroDotaId + "/mini.png");
        if (icon != null) {
            icon = Bitmap.createScaledBitmap(icon, mActionBarSize, mActionBarSize, false);
            return new BitmapDrawable(context.getResources(), icon);
        }
        return null;
    }
}
