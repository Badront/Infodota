package com.badr.infodota.service.news;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.news.AppNews;
import com.badr.infodota.api.news.AppNewsResult;
import com.badr.infodota.api.news.NewsItem;
import com.badr.infodota.remote.news.NewsRemoteService;

import java.util.List;

/**
 * User: Histler
 * Date: 21.04.14
 */
public class NewsServiceImpl implements NewsService {

    @Override
    public Pair<List<NewsItem>, String> getNews(Context context, Long fromDate) {
        BeanContainer container = BeanContainer.getInstance();
        NewsRemoteService service = container.getNewsRemoteService();
        try {
            Pair<AppNewsResult, String> result = service.getNews(context, fromDate);
            if (result.first == null) {
                String message = "Failed to get news, cause: " + result.second;
                Log.e(NewsServiceImpl.class.getName(), message);
            } else {
                AppNews appNews = result.first.getAppnews();
                if (appNews != null && appNews.getNewsitems() != null) {
                    return Pair.create(appNews.getNewsitems(), result.second);
                }
            }
            return Pair.create(null, result.second);
        } catch (Exception e) {
            String message = "Failed to get news, cause: " + e.getMessage();
            Log.e(NewsServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public void initialize() {
    }
}
