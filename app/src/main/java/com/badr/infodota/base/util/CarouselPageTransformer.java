package com.badr.infodota.base.util;

import android.os.Build;
import android.view.View;

import com.badr.infodota.base.view.TransformableViewPager;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by ABadretdinov
 * 26.12.2014
 * 13:46
 */
public class CarouselPageTransformer implements TransformableViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();
        float offset = 0;
        float fakePosition = position;
        if (Math.abs(position) < 5) {
            if (fakePosition < 0) {
                while (fakePosition < 0) {
                    offset += fakePosition;
                    fakePosition++;
                }
            } else if (fakePosition > 0) {
                while (fakePosition > 0) {
                    offset += fakePosition;
                    fakePosition--;
                }
            }
        }
        offset -= position;
        offset *= 2;
        offset += position;
        float scale = (float) (1 - 0.2 * Math.abs(position));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // > 11 version
            page.setTranslationX((float) (-pageWidth * 0.2 * offset) / 2);
            page.setScaleX(scale);
            page.setScaleY(scale);
            //page.setRotationY(position * -5);
        } else {
            // Nine Old Androids version
            ViewHelper.setTranslationX(page, (float) (-pageWidth * 0.2 * offset) / 2);
            ViewHelper.setScaleX(page, scale);
            ViewHelper.setScaleY(page, scale);
            //ViewHelper.setRotationY(page,position * -5);
        }

    }
}
