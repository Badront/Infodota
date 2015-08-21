package com.badr.infodota.news.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badr.infodota.R;
import com.badr.infodota.news.api.NewsItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * User: ABadretdinov
 * Date: 21.04.14
 * Time: 18:30
 */
public class NewsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<NewsItem> newsItems;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm  dd.MM.yyyy");

    public NewsAdapter(Context context, List<NewsItem> newsItems) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.newsItems = newsItems != null ? newsItems : new ArrayList<NewsItem>();
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        sdf.setTimeZone(tz);
    }

    public void addNewsItems(List<NewsItem> newsItems) {
        if (newsItems != null) {
            for (NewsItem newsItem : newsItems) {
                if (!this.newsItems.contains(newsItem)) {
                    this.newsItems.add(newsItem);
                }
            }
            Collections.sort(this.newsItems);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return newsItems.size();
    }

    @Override
    public NewsItem getItem(int position) {
        return newsItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        NewsItemHolder holder;
        if (vi == null) {
            vi = inflater.inflate(R.layout.news_item_row, parent, false);
            holder = new NewsItemHolder();
            holder.title = (TextView) vi.findViewById(R.id.title);
            holder.author = (TextView) vi.findViewById(R.id.author);
            holder.date = (TextView) vi.findViewById(R.id.date);
            vi.setTag(holder);
        } else {
            holder = (NewsItemHolder) vi.getTag();
        }
        NewsItem item = getItem(position);
        holder.title.setText(item.getTitle());
        String author;
        if (TextUtils.isEmpty(item.getAuthor())) {
            author = item.getFeedLabel();
        } else {
            author = item.getAuthor() + " (" + item.getFeedLabel() + ")";
        }
        holder.author.setText(author);
        long timestamp = item.getDate();
        String localTime = sdf.format(new Date(timestamp * 1000));
        holder.date.setText(localTime);
        return vi;
    }

    private class NewsItemHolder {
        TextView title;
        TextView author;
        TextView date;
    }
}
