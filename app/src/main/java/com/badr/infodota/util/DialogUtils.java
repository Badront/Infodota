package com.badr.infodota.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.badr.infodota.fragment.LoaderDialogFragment;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 13:47
 */
public class DialogUtils {
    public static <T> void showLoaderDialog(FragmentManager fragmentManager, ProgressTask<T> progressTask) {
        showLoaderDialog(fragmentManager, progressTask, null);
    }

    public static <T> void showLoaderDialog(FragmentManager fragmentManager, ProgressTask<T> progressTask, String loadingMessage) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);//todo вызывать dismiss мб сперва?
        }
        //ft.addToBackStack(null);
        LoaderDialogFragment<T> fragment = new LoaderDialogFragment<T>(progressTask, loadingMessage);
        fragment.show(ft, "dialog");
    }
}
