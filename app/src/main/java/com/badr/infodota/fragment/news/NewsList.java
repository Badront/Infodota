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
import com.badr.infodota.api.Constants;
import com.badr.infodota.api.news.AppNews;
import com.badr.infodota.api.news.NewsItem;
import com.badr.infodota.fragment.ListFragment;
import com.badr.infodota.service.news.NewsService;
import com.badr.infodota.util.EndlessScrollListener;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 21.04.14
 * Time: 18:29
 */
public class NewsList extends ListFragment implements SwipeRefreshLayout.OnRefreshListener,RequestListener<AppNews> {

    private BeanContainer container = BeanContainer.getInstance();
    private NewsService newsService = container.getNewsService();
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);


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
        spiceManager.execute(new NewsLoadRequest(page,totalItemsCount),this);
    }

    @Override
    public void onRefresh() {
        loadNews(0, getListAdapter().getCount());
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(getActivity(), spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        setRefreshing(false);
    }

    @Override
    public void onRequestSuccess(AppNews newsItems) {
        ((NewsAdapter) getListAdapter()).addNewsItems(newsItems.getNewsitems());
        setRefreshing(false);

    }
    public class NewsLoadRequest extends TaskRequest<AppNews>{
        private int page;
        private int totalItemsCount;
        public NewsLoadRequest(int page, int totalItemsCount) {
            super(AppNews.class);
            this.page=page;
            this.totalItemsCount=totalItemsCount;
        }

        @Override
        public AppNews loadData() throws Exception {
            Long fromDate = null;
            if (page != 0 && totalItemsCount > 0) {
                NewsItem lastItem = ((NewsAdapter) getListAdapter()).getItem(totalItemsCount - 1);
                fromDate = lastItem.getDate();
            }
            return newsService.getNews(getActivity(), fromDate);
        }
    }
}
