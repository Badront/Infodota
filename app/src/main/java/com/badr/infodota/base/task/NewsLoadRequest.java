package com.badr.infodota.base.task;

import android.content.Context;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.base.api.news.AppNews;
import com.badr.infodota.base.service.news.NewsService;
import com.badr.infodota.util.retrofit.TaskRequest;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 15:53
 */
public class NewsLoadRequest extends TaskRequest<AppNews> {
    private Long mFromDate;
    private Context mContext;

    public NewsLoadRequest(Context context, Long fromDate) {
        super(AppNews.class);
        this.mContext = context;
        this.mFromDate = fromDate;
    }

    @Override
    public AppNews loadData() throws Exception {

        BeanContainer container = BeanContainer.getInstance();
        NewsService newsService = container.getNewsService();
        return newsService.getNews(mContext, mFromDate);
    }
}
