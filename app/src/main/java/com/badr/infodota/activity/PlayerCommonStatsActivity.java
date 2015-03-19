package com.badr.infodota.activity;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;

import com.badr.infodota.R;
import com.badr.infodota.adapter.pager.PlayerCommonStatsPagerAdapter;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.view.SlidingTabLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * User: ABadretdinov
 * Date: 27.03.14
 * Time: 18:05
 */
public class PlayerCommonStatsActivity extends BaseActivity {
    FragmentPagerAdapter adapter;
    private Unit account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_stats_result);
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoader imageLoader = ImageLoader.getInstance();

        Bundle bundle = getIntent().getExtras();
        account = (Unit) bundle.get("account");
        initPager(bundle.getBundle("args"));
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(account.getName());
        imageLoader.loadImage(account.getIcon(), options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
                int mActionBarSize = (int) styledAttributes.getDimension(0, 40) / 2;
                styledAttributes.recycle();
                Bitmap icon = loadedImage;
                if (icon != null) {
                    icon = Bitmap.createScaledBitmap(icon, mActionBarSize, mActionBarSize, false);
                    Drawable iconDrawable = new BitmapDrawable(getResources(), icon);
                    //actionBar.setDisplayShowHomeEnabled(true);
                    //actionBar.setIcon(iconDrawable);
                    mToolbar.setNavigationIcon(iconDrawable);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    private void initPager(Bundle args) {
        adapter = new PlayerCommonStatsPagerAdapter(getSupportFragmentManager(), this, args, account);

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(1);
        SlidingTabLayout indicator = (SlidingTabLayout) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }
}
