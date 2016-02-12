package com.badr.infodota.base.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.badr.infodota.base.BaseBeanContainer;
import com.badr.infodota.base.activity.BaseActivity;
import com.badr.infodota.base.service.NavigationService;

import java.util.List;

/**
 * Created by ABadretdinov
 * 10.02.2016
 * 18:26
 */
public final class Navigate {
    public static final String PARAM_CLASS = "class";
    public static final String PARAM_MENU_ID = "menu_id";
    public static final String PARAM_ARGS = "args";
    public static final String PARAM_HAS_MENU = "has_menu";
    public static final String PARAM_THEME = "theme";
    public static final String PARAM_FOR_SELECT = "for_select";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_EMPTYTEXT = "empty";
    public static final String PARAM_LOADER = "loader";
    public static final String PARAM_ID = "id";
    public static final String PARAM_ENTITY = "entity";


    public static void toMain(Context context, Class fragmentClass) {
        toMain(context, fragmentClass, 0);
    }

    public static void toMain(Context context, Class fragmentClass, int menuId) {
        toMain(context, fragmentClass, menuId, null);
    }

    public static void toMain(Context context, Class fragmentClass, int menuId, Bundle bundle) {
        toMain(context, fragmentClass, menuId, bundle, false);
    }

    public static void toMain(Context context, Class fragmentClass, int menuId, Bundle bundle, boolean reOpen) {
        NavigationService navigationService = BaseBeanContainer.getInstance().getNavigationService();
        Class mainActivity = navigationService.getMainActivityClass();
        Intent intent = new Intent(context, mainActivity);
        intent.putExtra(PARAM_CLASS, fragmentClass.getName());
        if (menuId != 0) {
            intent.putExtra(PARAM_MENU_ID, menuId);
        }
        if (bundle == null) {
            bundle = new Bundle();
        }
        intent.putExtra(PARAM_ARGS, bundle);
        if (!reOpen && (context.getClass().equals(mainActivity))) {
            ((BaseActivity) context).initFragment(intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

    public static void to(Context context, Class fragmentClass, Bundle bundle, boolean hasMenu, int... flags) {
        to(context, fragmentClass, bundle, 0, hasMenu, flags);
    }

    public static void to(Context context, Class fragmentClass, Bundle bundle, int theme, boolean hasMenu, int... flags) {
        NavigationService navigationService = BaseBeanContainer.getInstance().getNavigationService();
        Intent intent = new Intent(context, navigationService.getActivityClass());
        intent.putExtra(PARAM_CLASS, fragmentClass.getName());
        if (flags != null && flags.length > 0) {
            for (int flag : flags) {
                intent.addFlags(flag);
            }
        }
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putBoolean(PARAM_HAS_MENU, hasMenu);
        if (theme != 0) {
            intent.putExtra(PARAM_THEME, theme);
        }
        intent.putExtra(PARAM_ARGS, bundle);
        context.startActivity(intent);
    }

    public static void toForResult(Fragment fragment, Class fragmentClass, Bundle bundle, int requestCode) {
        toForResult(fragment, fragmentClass, bundle, requestCode, 0);
    }

    public static void toForResult(Fragment fragment, Class fragmentClass, Bundle bundle, int requestCode, int theme) {
        toForResult(fragment, fragmentClass, bundle, false, requestCode, theme);
    }

    public static void toForResult(Fragment fragment, Class fragmentClass, Bundle bundle, boolean hasMenu, int requestCode) {
        toForResult(fragment, fragmentClass, bundle, hasMenu, requestCode, 0);
    }

    public static void toForResult(Fragment fragment, Class fragmentClass, Bundle bundle, boolean hasMenu, int requestCode, int theme) {
        NavigationService navigationService = BaseBeanContainer.getInstance().getNavigationService();
        Intent intent = new Intent(fragment.getActivity(), navigationService.getActivityClass());
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putBoolean(PARAM_HAS_MENU, hasMenu);
        if (theme != 0) {
            intent.putExtra(PARAM_THEME, theme);
        }
        intent.putExtra(PARAM_CLASS, fragmentClass.getName());
        intent.putExtra(PARAM_ARGS, bundle);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void toForEntityResult(Fragment fragment, Class fragmentClass, Bundle bundle, int requestCode) {
        toForEntityResult(fragment, fragmentClass, bundle, requestCode, 0);
    }

    public static void toForEntityResult(Fragment fragment, Class fragmentClass, Bundle bundle, int requestCode, int theme) {
        NavigationService navigationService = BaseBeanContainer.getInstance().getNavigationService();
        Intent intent = new Intent(fragment.getActivity(), navigationService.getActivityClass());
        intent.putExtra(PARAM_CLASS, fragmentClass.getName());
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putBoolean(PARAM_FOR_SELECT, true);
        bundle.putBoolean(PARAM_HAS_MENU, false);
        if (theme != 0) {
            intent.putExtra(PARAM_THEME, theme);
        }
        intent.putExtra(PARAM_FOR_SELECT, true);
        intent.putExtra(PARAM_ARGS, bundle);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void toForEntityResult(Activity activity, Class fragmentClass, Bundle bundle, int requestCode) {
        NavigationService navigationService = BaseBeanContainer.getInstance().getNavigationService();
        Intent intent = new Intent(activity, navigationService.getActivityClass());
        intent.putExtra(PARAM_CLASS, fragmentClass.getName());
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putBoolean(PARAM_FOR_SELECT, true);
        bundle.putBoolean(PARAM_HAS_MENU, false);
        intent.putExtra(PARAM_FOR_SELECT, true);
        intent.putExtra(PARAM_ARGS, bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    public static boolean intentCanBeHandled(Context context, Intent intent) {
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        return infos.size() > 0;
    }
}
