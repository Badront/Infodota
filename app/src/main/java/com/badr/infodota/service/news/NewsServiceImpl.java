package com.badr.infodota.service.news;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.news.AppNews;
import com.badr.infodota.api.news.AppNewsResult;
import com.badr.infodota.api.news.NewsItem;

import java.util.List;

/**
 * User: Histler
 * Date: 21.04.14
 */
public class NewsServiceImpl implements NewsService {

    @Override
    public Pair<List<NewsItem>, String> getNews(Context context, Long fromDate) {
        try {
            AppNewsResult result = BeanContainer.getInstance().getSteamService().getNews(fromDate);
            String message=null;
            if (result== null) {
                message = "Failed to get news";
                Log.e(NewsServiceImpl.class.getName(), message);
            } else {
                AppNews appNews = result.getAppnews();
                if (appNews != null && appNews.getNewsitems() != null) {
                    return Pair.create(appNews.getNewsitems(), null);
                }
            }
            return Pair.create(null, message);
        } catch (Exception e) {
            String message = "Failed to get news, cause: " + e.getMessage();
            Log.e(NewsServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }
}
