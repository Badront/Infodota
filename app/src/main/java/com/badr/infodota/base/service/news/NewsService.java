package com.badr.infodota.base.service.news;

import android.content.Context;

import com.badr.infodota.base.api.news.AppNews;

/**
 * User: Histler
 * Date: 21.04.14
 */
public interface NewsService {
    AppNews getNews(Context context, Long fromDate);
}
