package com.badr.infodota.base.service;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.badr.infodota.base.activity.BaseActivity;


/**
 * Created by ABadretdinov
 * 29.06.2015
 * 11:07
 */
public interface NavigationService {
    Class<? extends BaseActivity> getMainActivityClass();

    Class<? extends BaseActivity> getActivityClass();

    Class<? extends Activity> getIntroActivity();

    int getDefaultFragmentResId();

    int getNavigationMenuResId();

    Class<? extends Fragment> getMainFragment(int resId);
}
