package com.badr.infodota.base.service.news;

import android.content.Context;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.base.api.news.AppNews;
import com.badr.infodota.base.api.news.AppNewsHolder;

/**
 * User: Histler
 * Date: 21.04.14
 */
public class NewsServiceImpl implements NewsService {

    @Override
    public AppNews getNews(Context context, Long fromDate) {
        AppNewsHolder result = BeanContainer.getInstance().getSteamService().getNews(fromDate);
        if (result != null) {
            return result.getAppNews();
        }
        return null;
    }
}
