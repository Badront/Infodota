package com.badr.infodota.news.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ActionMenuView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.badr.infodota.base.activity.BaseActivity;
import com.badr.infodota.base.activity.ListHolderActivity;
import com.badr.infodota.base.fragment.UpdatableListFragment;
import com.badr.infodota.base.util.EndlessScrollListener;
import com.badr.infodota.news.activity.NewsItemActivity;
import com.badr.infodota.news.adapter.NewsAdapter;
import com.badr.infodota.news.api.AppNews;
import com.badr.infodota.news.api.NewsItem;
import com.badr.infodota.news.task.NewsLoadRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * User: ABadretdinov
 * Date: 21.04.14
 * Time: 18:29
 */
public class NewsList extends UpdatableListFragment implements SwipeRefreshLayout.OnRefreshListener, RequestListener<AppNews> {

    private SpiceManager mSpiceManager = new SpiceManager(UncachedSpiceService.class);


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        ActionMenuView actionMenuView = ((ListHolderActivity) getActivity()).getActionMenuView();
        Menu actionMenu = actionMenuView.getMenu();
        actionMenu.clear();
        actionMenuView.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        View root = getView();
        if (root == null) {
            return;
        }
        getListView().setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadNews(page, totalItemsCount);
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        NewsItem item = ((NewsAdapter) getListAdapter()).getItem(position);
        Intent intent = new Intent(getActivity(), NewsItemActivity.class);
        intent.putExtra("newsItem", item);
        startActivity(intent);
    }

    private void loadNews(final int page, final int totalItemsCount) {
        final BaseActivity activity = (BaseActivity) getActivity();
        if (activity != null) {
            setRefreshing(true);
            if (getListAdapter() == null) {
                setListAdapter(new NewsAdapter(activity, null));
            }
            Long fromDate = null;
            if (page != 0 && totalItemsCount > 0) {
                NewsItem lastItem = ((NewsAdapter) getListAdapter()).getItem(totalItemsCount - 1);
                fromDate = lastItem.getDate();
            }
            mSpiceManager.execute(new NewsLoadRequest(activity.getApplicationContext(), fromDate), this);
        }
    }

    @Override
    public void onRefresh() {
        loadNews(0, getListAdapter().getCount());
    }

    @Override
    public void onStart() {
        if (!mSpiceManager.isStarted()) {
            mSpiceManager.start(getActivity());
            loadNews(0, 0);
        }
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(getActivity(), spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        setRefreshing(false);
    }

    @Override
    public void onRequestSuccess(AppNews newsItems) {
        ((NewsAdapter) getListAdapter()).addNewsItems(newsItems.getNewsItems());
        setRefreshing(false);

    }
}
