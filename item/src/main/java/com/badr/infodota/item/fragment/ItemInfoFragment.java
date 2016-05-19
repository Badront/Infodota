package com.badr.infodota.item.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.badr.infodota.base.fragment.BaseFragment;
import com.badr.infodota.item.entity.Item;

/**
 * Created by ABadretdinov
 * 16.03.2016
 * 17:42
 */
public class ItemInfoFragment extends BaseFragment {
    private Item mItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_info);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        changeOrientation();


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeOrientation();
    }

    private void changeOrientation() {
        if (getResources().getBoolean(R.bool.is_tablet)) {
            ((LinearLayout) getView().findViewById(R.id.main_holder)).setOrientation(LinearLayout.HORIZONTAL);
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ((LinearLayout) getView().findViewById(R.id.main_holder)).setOrientation(LinearLayout.HORIZONTAL);
            } else {
                ((LinearLayout) getView().findViewById(R.id.main_holder)).setOrientation(LinearLayout.VERTICAL);
            }
        }
    }

    @Override
    protected String getTitle() {
        return null;
    }
}
