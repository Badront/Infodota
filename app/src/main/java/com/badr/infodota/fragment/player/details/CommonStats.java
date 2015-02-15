package com.badr.infodota.fragment.player.details;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.BaseActivity;
import com.badr.infodota.activity.MatchInfoActivity;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.service.hero.HeroService;
import com.badr.infodota.util.ResourceUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * User: ABadretdinov
 * Date: 27.03.14
 * Time: 18:27
 */
public class CommonStats extends Fragment {
    protected ImageLoader imageLoader;
    DisplayImageOptions options;
    private String metric;
    private Unit account;
    private LinearLayout recordsHolder;
    private LinearLayout.LayoutParams layoutParams;

    private LinearLayout orientationHolder;
    private LinearLayout.LayoutParams orientationParams;
    private int size = 0;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm  dd.MM.yyyy");
    private SimpleDateFormat fromSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static CommonStats newInstance(Unit account, Bundle args, String metric) {
        CommonStats fragment = new CommonStats();
        fragment.setArguments(args);
        fragment.setMetric(metric);
        fragment.setAccount(account);
        return fragment;
    }

    public void setAccount(Unit account) {
        this.account = account;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.common_stats, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.antimage_vert)
                .cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader = ImageLoader.getInstance();
        View root = getView();
        recordsHolder = (LinearLayout) root.findViewById(R.id.row_holder);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1f;
        orientationParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        loadData();
    }

    private void loadData() {
        final BaseActivity activity = (BaseActivity) getActivity();
        activity.setSupportProgressBarIndeterminateVisibility(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder url = new StringBuilder("http://dotabuff.com/players/");
                    url.append(account.getAccountId());
                    url.append("/records");
                    url.append("?metric=");
                    url.append(metric);
                    Bundle args = getArguments();
                    Set<String> keySet = args.keySet();
                    for (String key : keySet) {
                        url.append("&").append(key).append("=");
                        url.append(args.getString(key));
                    }

                    Document doc = Jsoup.connect(url.toString()).get();
                    Elements headerElements = doc.select("div[id=content-header-secondary]");
                    Element headerElement = headerElements.first();
                    Elements winsElement = headerElement.select("span[class=wins]");
                    String wins = null;
                    if (winsElement != null && winsElement.size() != 0) {
                        wins = winsElement.get(0).html();
                    }
                    Elements lostElement = headerElement.select("span[class=losses]");
                    String lost = null;
                    if (lostElement != null && lostElement.size() != 0) {
                        lost = lostElement.get(0).html();
                    }
                    Elements dls = headerElement.select("dl");
                    Element winRateElement = null;
                    for (Element dl : dls) {
                        String dt = dl.select("dt").first().html();
                        if (dt.contains("Win Rate")) {
                            winRateElement = dl.select("dd").first();
                            break;
                        }
                    }
                    final String winRate = winRateElement != null ? winRateElement.html() : "NaN%";
                    final String finalWins = wins;
                    final String finalLost = lost;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //todo - переделать на Loader
                            activity.getSupportActionBar().setSubtitle(MessageFormat
                                    .format(getString(R.string.record_with_win_rate), finalWins, finalLost, winRate));
                        }
                    });
                    Elements recordsElements = doc.select("div[class=player-records]");
                    if (recordsElements != null && recordsElements.size() != 0) {
                        Elements boxes = recordsElements.select("div[class=record]");
                        if (boxes != null && boxes.size() > 0) {
                            for (Element box : boxes) {
                                final String boxHeader = box.select("div[class=title]").first().html();
                                Element details = box.select("div[class=details]").first();
                                String matchUrl = box.select("a").first().attr("href");
                                String[] parts = matchUrl.split("/");
                                final String matchId = parts[parts.length - 1];

                                List<TextNode> detailsNodes = details.textNodes();
                                TextNode wonNode = detailsNodes.get(0);
                                String wonText = wonNode.text();
                                final boolean won = wonText.contains("Won");

                                Element timeAgo = details.select("time").first();
                                String dateTimeWas = timeAgo.attr("datetime");
                                Date date = fromSdf.parse(dateTimeWas);
                                final String dateTime = sdf.format(date);

                                String heroName = box.select("div[class=hero]").first().text();
                                heroName = heroName.substring(0, heroName.indexOf(" (") - 1);

                                final String result = box.select("div[class=value]").first().text();
                                HeroService heroService = BeanContainer.getInstance().getHeroService();
                                List<Hero> heroes = heroService.getHeroesByName(activity, heroName);
                                if (heroes != null && heroes.size() > 0) {
                                    final Hero hero = heroes.get(0);
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TextView header = new TextView(activity);
                                            header.setTextSize(16);
                                            header.setText(ResourceUtils.getStatsHeader(activity, boxHeader));

                                            LinearLayout holder = new LinearLayout(activity);
                                            holder.setLayoutParams(layoutParams);
                                            holder.setOrientation(LinearLayout.VERTICAL);
                                            holder.addView(header);

                                            LinearLayout row = (LinearLayout) activity.getLayoutInflater()
                                                    .inflate(R.layout.player_common_stats_row, null, false);
                                            imageLoader
                                                    .displayImage("assets://heroes/" + hero.getDotaId() + "/full.png",
                                                            (ImageView) row.findViewById(R.id.hero_img), options);

                                            ((TextView) row.findViewById(R.id.hero_name))
                                                    .setText(hero.getLocalizedName());

                                            int color = won ? Color.GREEN : Color.RED;
                                            int winloseRId = won ? R.string.win : R.string.lost;
                                            ((TextView) row.findViewById(R.id.win_lose)).setText(winloseRId);
                                            ((TextView) row.findViewById(R.id.win_lose)).setTextColor(color);

                                            ((TextView) row.findViewById(R.id.game_start_time)).setText(dateTime);

                                            String displayResult = MessageFormat
                                                    .format(ResourceUtils.getStatsResultTitle(activity, boxHeader),
                                                            result);
                                            ((TextView) row.findViewById(R.id.result)).setText(displayResult);

                                            row.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(activity, MatchInfoActivity.class);
                                                    intent.putExtra("matchId", matchId);
                                                    startActivity(intent);
                                                }
                                            });
                                            holder.addView(row);
                                            if (orientationHolder == null) {
                                                size = 1;
                                                orientationHolder = new LinearLayout(activity);
                                                orientationHolder.setLayoutParams(orientationParams);
                                                if (getResources().getBoolean(R.bool.is_tablet) || getResources()
                                                        .getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                                    orientationHolder.setOrientation(LinearLayout.HORIZONTAL);
                                                } else {
                                                    orientationHolder.setOrientation(LinearLayout.VERTICAL);
                                                }
                                            } else {
                                                size++;
                                            }
                                            orientationHolder.addView(holder);
                                            if (size == 2) {
                                                recordsHolder.addView(orientationHolder);
                                                orientationHolder = null;
                                            }
                                        }
                                    });
                                }
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (orientationHolder != null) {
                                        recordsHolder.addView(orientationHolder);
                                        orientationHolder = null;
                                    }
                                }
                            });
                        }
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    /*activity.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							Utils.showErrorDialog(activity,getString(R.string.error_loading));
						}
					});*/
                } finally {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.setSupportProgressBarIndeterminateVisibility(false);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int size = recordsHolder.getChildCount();
        for (int i = 0; i < size; i++) {
            LinearLayout orientationHolder = (LinearLayout) recordsHolder.getChildAt(i);
            orientationHolder.setOrientation(getResources().getBoolean(
                    R.bool.is_tablet) || newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        }
    }
}
