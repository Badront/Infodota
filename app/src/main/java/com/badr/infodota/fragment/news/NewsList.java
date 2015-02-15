package com.badr.infodota.fragment.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ActionMenuView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.BaseActivity;
import com.badr.infodota.activity.ListHolderActivity;
import com.badr.infodota.activity.NewsItemActivity;
import com.badr.infodota.adapter.NewsAdapter;
import com.badr.infodota.api.news.NewsItem;
import com.badr.infodota.fragment.ListFragment;
import com.badr.infodota.service.news.NewsService;
import com.badr.infodota.util.EndlessScrollListener;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 21.04.14
 * Time: 18:29
 */
public class NewsList extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private BeanContainer container = BeanContainer.getInstance();
    private NewsService newsService = container.getNewsService();

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
        loadNews(0, 0);
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
        setRefreshing(true);
        if (getListAdapter() == null) {
            setListAdapter(new NewsAdapter(activity, null));
        }
        new LoaderProgressTask<Pair<List<NewsItem>, String>>(new ProgressTask<Pair<List<NewsItem>, String>>() {

            @Override
            public Pair<List<NewsItem>, String> doTask(OnPublishProgressListener listener) throws Exception {
                Long fromDate = null;
                if (page != 0 && totalItemsCount > 0) {
                    NewsItem lastItem = ((NewsAdapter) getListAdapter()).getItem(totalItemsCount - 1);
                    fromDate = lastItem.getDate();
                }
                return newsService.getNews(activity, fromDate);
            }

            @Override
            public void doAfterTask(Pair<List<NewsItem>, String> result) {
                if (result.first != null) {
                    ((NewsAdapter) getListAdapter()).addNewsItems(result.first);
                } else if (!TextUtils.isEmpty(result.second)) {
                    handleError(result.second);
                } else {
                    handleError(activity.getString(R.string.no_more_news));
                }
                setRefreshing(false);
            }

            @Override
            public void handleError(String error) {
                if (!TextUtils.isEmpty(error)) {
                    Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                }
                setRefreshing(false);
            }

            @Override
            public String getName() {
                return null;
            }
        }, null).execute();
    }

    @Override
    public void onRefresh() {
        loadNews(0, getListAdapter().getCount());
    }
}
