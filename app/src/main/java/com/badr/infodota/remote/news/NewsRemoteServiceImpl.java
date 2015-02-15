package com.badr.infodota.remote.news;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.api.Constants;
import com.badr.infodota.api.news.AppNewsResult;
import com.badr.infodota.remote.BaseRemoteServiceImpl;

import java.text.MessageFormat;

/**
 * User: Histler
 * Date: 21.04.14
 */
public class NewsRemoteServiceImpl extends BaseRemoteServiceImpl implements NewsRemoteService {
    @Override
    public Pair<AppNewsResult, String> getNews(Context context, Long endDate) throws Exception {
        String url = Constants.News.SUBURL;
        if (endDate != null) {
            url += MessageFormat.format(Constants.News.ENDDATE, String.valueOf(endDate));
        }
        return basicRequestSend(context, url, AppNewsResult.class);
    }
}
