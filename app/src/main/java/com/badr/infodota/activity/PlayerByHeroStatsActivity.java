package com.badr.infodota.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.service.hero.HeroService;
import com.badr.infodota.util.ResourceUtils;
import com.badr.infodota.view.HorizontalScrollViewListener;
import com.badr.infodota.view.ObservableHorizontalScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 19.02.14
 * Time: 17:11
 */
public class PlayerByHeroStatsActivity extends BaseActivity implements HorizontalScrollViewListener {
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private ObservableHorizontalScrollView obs1;
    private ObservableHorizontalScrollView obs2;
    private LinearLayout content;
    private LinearLayout verticalHeader;
    private LinearLayout horizontalHeader;
    private Unit account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.player_by_hero_stats);
        setSupportProgressBarIndeterminateVisibility(false);

        obs1 = (ObservableHorizontalScrollView) findViewById(R.id.observable1);
        obs2 = (ObservableHorizontalScrollView) findViewById(R.id.observable2);
        obs1.setScrollViewListener(this);
        obs2.setScrollViewListener(this);

        content = (LinearLayout) findViewById(R.id.content_holder);
        horizontalHeader = (LinearLayout) findViewById(R.id.horizontal_header_holder);
        verticalHeader = (LinearLayout) findViewById(R.id.vertical_header_holder);

        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader = ImageLoader.getInstance();

        Bundle bundle = getIntent().getExtras();
        account = (Unit) bundle.get("account");
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(account.getName());
        imageLoader.loadImage(account.getIcon(), options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
                int mActionBarSize = (int) styledAttributes.getDimension(0, 40) / 2;
                styledAttributes.recycle();
                Bitmap icon = loadedImage;
                if (icon != null) {
                    icon = Bitmap.createScaledBitmap(icon, mActionBarSize, mActionBarSize, false);
                    Drawable iconDrawable = new BitmapDrawable(getResources(), icon);
                    //actionBar.setDisplayShowHomeEnabled(true);
                    //actionBar.setIcon(iconDrawable);
                    mToolbar.setNavigationIcon(iconDrawable);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        StringBuilder urlBuilder = new StringBuilder("http://dotabuff.com/players/");
        urlBuilder.append(account.getAccountId());
        urlBuilder.append("/heroes?");
        urlBuilder.append("date=");
        urlBuilder.append(bundle.getString("date"));
        urlBuilder.append("&game_mode=");
        urlBuilder.append(bundle.getString("game_mode"));
        urlBuilder.append("&match_type=");
        urlBuilder.append(bundle.getString("match_type"));
        urlBuilder.append("&metric=");
        urlBuilder.append(bundle.getString("metric"));
        final String url = urlBuilder.toString();

        setSupportProgressBarIndeterminateVisibility(true);
        horizontalHeader.removeAllViews();
        verticalHeader.removeAllViews();
        content.removeAllViews();

        //todo fix this shit
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeroService heroService = BeanContainer.getInstance().getHeroService();
                    Document doc = Jsoup.connect(url).get();
                    Element table = doc.select("table").first();
                    Element tableHeader = table.select("thead").first();
                    Elements headers = tableHeader.select("th");
                    //get(i).text()
                    final List<String> horizontalHeaders = new ArrayList<String>();
                    for (Element elementHeader : headers) {
                        String horizontalHeader = elementHeader.text();
                        horizontalHeaders.add(horizontalHeader);
                        System.out.println(horizontalHeader);
                    }
                    headers = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (String header : horizontalHeaders) {
                                int headerId = ResourceUtils.getByHeroStatsHeaders(header);
                                String realHeader;
                                if (headerId != 0) {
                                    realHeader = getString(headerId);
                                } else {
                                    realHeader = header;
                                }
                                View v = getLayoutInflater().inflate(R.layout.player_by_hero_stats_header, horizontalHeader, false);
                                ((TextView) v.findViewById(android.R.id.text1)).setText(realHeader);
                                horizontalHeader.addView(v);
                            }
                        }
                    });
                    tableHeader = null;
                    Element tableBody = table.select("tbody").first();
                    table = null;
                    Elements rows = tableBody.select("tr");
                    for (Element row : rows) {
                        Hero hero = null;
                        final List<String> heroResults = new ArrayList<String>();
                        if (TextUtils.isEmpty(row.attr("class"))) {
                            Elements columns = row.select("td");
                            for (Element column : columns) {
                                String className = column.attr("class");
                                if (!TextUtils.isEmpty(className)) {
                                    if ("cell-xlarge".equals(className)) {
                                        Element a = column.select("a").first();
                                        String heroName = a.text();
                                        List<Hero> possibleHeroes = heroService.getHeroesByName(PlayerByHeroStatsActivity.this, heroName);
                                        if (possibleHeroes != null && possibleHeroes.size() > 0) {
                                            hero = possibleHeroes.get(0);
                                            heroResults.add(hero.getLocalizedName());
                                        }
                                    }
                                } else {
                                    /*Element divResult=column.select("div") .first();*/
                                    String columnResult = column.text();
                                    heroResults.add(columnResult);
                                }
                            }
                        }
                        //
                        final Hero finalHero = hero;
                        if (finalHero != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    View verticalHeaderRow = getLayoutInflater().inflate(R.layout.player_by_hero_stats_vertical, null, false);
                                    imageLoader.displayImage("assets://heroes/" + finalHero.getDotaId() + "/full.png",
                                            (ImageView) verticalHeaderRow.findViewById(R.id.image), options);
                                    verticalHeader.addView(verticalHeaderRow);
                                    verticalHeaderRow.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(PlayerByHeroStatsActivity.this,
                                                    HeroInfoActivity.class);
                                            intent.putExtra("id", (long) finalHero.getId());
                                            startActivity(intent);
                                        }
                                    });
                                    LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.player_by_hero_stats_row, null, false);
                                    for (String verticalResult : heroResults) {
                                        LinearLayout cell = (LinearLayout) getLayoutInflater().inflate(R.layout.player_by_hero_stats_cell, null, false);
                                        TextView resultTV = (TextView) cell.findViewById(android.R.id.text1);
                                        //resultTV.setBackgroundColor(getResources().getColor(R.color.dota_gray));
                                        resultTV.setText(verticalResult);
                                        row.addView(cell);
                                    }
                                    content.addView(row);
                                }
                            });
                        }
                    }
                    rows = null;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSupportProgressBarIndeterminateVisibility(false);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onScrollChanged(ObservableHorizontalScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (scrollView == obs1) {
            obs2.scrollTo(x, y);
        } else if (scrollView == obs2) {
            obs1.scrollTo(x, y);
        }
    }
}
