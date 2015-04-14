package com.badr.infodota.activity;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.adapter.pager.MatchHistoryPagerAdapter;
import com.badr.infodota.adapter.pager.PlayerInfoPagerAdapter;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.service.player.PlayerService;
import com.badr.infodota.util.Utils;
import com.badr.infodota.view.SlidingTabLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * User: Histler
 * Date: 21.01.14
 */
public class PlayerInfoActivity extends BaseActivity {
    protected ImageLoader imageLoader;
    DisplayImageOptions options;
    PlayerService playerService = BeanContainer.getInstance().getPlayerService();
    private Unit account;
    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        if (account.getGroup() == Unit.Groups.NONE) {
            menuForUnknown();
        } else {
            menuForSaved();
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void menuForUnknown() {
        MenuItem add = menu.add(0, R.id.add_btn, 1, R.string.add_player_title);
        add.setIcon(R.drawable.ic_menu_add);
        MenuItemCompat.setShowAsAction(add, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
    }

    private void menuForSaved() {
        MenuItem group = menu.add(0, R.id.group_id, 1, getResources().getStringArray(R.array.match_history_title)[account.getGroup().ordinal()]);
        MenuItemCompat.setShowAsAction(group, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        MenuItem delete = menu.add(0, R.id.delete_btn, 2, R.string.delete_player_title);
        delete.setIcon(R.drawable.ic_menu_delete);
        MenuItemCompat.setShowAsAction(delete, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_btn:
                Utils.addPlayerToListDialog(this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            account.setGroup(Unit.Groups.FRIEND);
                        } else {
                            account.setGroup(Unit.Groups.PRO);
                        }
                        playerService.saveAccount(PlayerInfoActivity.this, account);
                        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
                        if (viewPager != null && viewPager.getAdapter() instanceof MatchHistoryPagerAdapter) {
                            MatchHistoryPagerAdapter adapter = (MatchHistoryPagerAdapter) viewPager.getAdapter();
                            adapter.update();
                        }
                        menu.removeItem(R.id.add_btn);
                        menuForSaved();
                        dialog.dismiss();
                    }
                });
                return true;
            case R.id.delete_btn:
                Utils.deletePlayerFromListDialog(this, account, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playerService.deleteAccount(PlayerInfoActivity.this, account);
                        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
                        if (viewPager != null && viewPager.getAdapter() instanceof MatchHistoryPagerAdapter) {
                            MatchHistoryPagerAdapter adapter = (MatchHistoryPagerAdapter) viewPager.getAdapter();
                            adapter.update();
                        }
                        menu.removeItem(R.id.delete_btn);
                        menu.removeItem(R.id.group_id);
                        menuForUnknown();
                        dialog.dismiss();
                    }
                });

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_info);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("account")) {
            account = (Unit) bundle.get("account");
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
                    final TypedArray styledAttributes = getTheme()
                            .obtainStyledAttributes(new int[]{R.attr.actionBarSize});
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
            FragmentPagerAdapter adapter = new PlayerInfoPagerAdapter(this, getSupportFragmentManager(), account);
            final ViewPager pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(adapter);
            pager.setOffscreenPageLimit(5);
            SlidingTabLayout indicator = (SlidingTabLayout) findViewById(R.id.indicator);
            indicator.setViewPager(pager);
            pager.setCurrentItem(2);
        }

    }
}
