package com.badr.infodota.base.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badr.infodota.base.R;

/**
 * Created by ABadretdinov
 * 16.03.2016
 * 14:42
 */
public abstract class BaseViewPagerFragment extends BaseFragment {
    protected ViewPager mViewPager;

    public int getLayoutId() {
        return R.layout.base_view_pager_fragment;
    }

    protected boolean isFabVisible() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        View fab = view.findViewById(R.id.fab);
        if (fab != null && !isFabVisible()) {
            fab.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewPager.setAdapter(getAdapter());
        View root = getView();
        if (root != null) {
            TabLayout tabLayout = (TabLayout) root.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
        }
    }

    public abstract PagerAdapter getAdapter();

    protected ViewPager getViewPager() {
        return mViewPager;
    }
}
