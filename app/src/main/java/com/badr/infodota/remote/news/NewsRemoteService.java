package com.badr.infodota.remote.news;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.api.news.AppNewsResult;
import com.badr.infodota.remote.BaseRemoteService;

/**
 * User: Histler
 * Date: 21.04.14
 */
public interface NewsRemoteService extends BaseRemoteService {
    Pair<AppNewsResult, String> getNews(Context context, Long endDate) throws Exception;
}
