package com.badr.infodota.activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.service.hero.HeroService;
import com.badr.infodota.util.ResourceUtils;
import com.badr.infodota.util.Utils;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.badr.infodota.view.HorizontalScrollViewListener;
import com.badr.infodota.view.ObservableHorizontalScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: ABadretdinov
 * Date: 19.02.14
 * Time: 17:11
 */
public class PlayerByHeroStatsActivity extends BaseActivity implements HorizontalScrollViewListener,RequestListener<PlayerByHeroStatsActivity.PlayerHeroesStats> {
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private ObservableHorizontalScrollView obs1;
    private ObservableHorizontalScrollView obs2;
    private LinearLayout content;
    private LinearLayout verticalHeader;
    private LinearLayout horizontalHeader;
    private View contentHolder;
    private View progressBarHolder;
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);
    private boolean initialized=false;
    @Override
    protected void onStart() {
        super.onStart();
        if(!spiceManager.isStarted()) {
            spiceManager.start(this);
            if(!initialized) {
                Bundle bundle = getIntent().getExtras();
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

                spiceManager.execute(new PlayerHeroesStatsLoadRequest(this, urlBuilder.toString()), this);
            }
        }
    }

    @Override
    protected void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onStop();
    }
    private Unit account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_by_hero_stats);

        contentHolder=findViewById(R.id.content_holder);
        progressBarHolder=findViewById(R.id.progressBarHolder);

        obs1 = (ObservableHorizontalScrollView) findViewById(R.id.observable1);
        obs2 = (ObservableHorizontalScrollView) findViewById(R.id.observable2);
        obs1.setScrollViewListener(this);
        obs2.setScrollViewListener(this);

        content = (LinearLayout) findViewById(R.id.content);
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

        horizontalHeader.removeAllViews();
        verticalHeader.removeAllViews();
        content.removeAllViews();
    }

    @Override
    public void onScrollChanged(ObservableHorizontalScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (scrollView == obs1) {
            obs2.scrollTo(x, y);
        } else if (scrollView == obs2) {
            obs1.scrollTo(x, y);
        }
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        spiceException.printStackTrace();
        progressBarHolder.setVisibility(View.GONE);
        Toast.makeText(this,spiceException.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(PlayerHeroesStats playerHeroesStats) {
        initialized=true;
        progressBarHolder.setVisibility(View.GONE);
        contentHolder.setVisibility(View.VISIBLE);
        if(playerHeroesStats!=null){
            LayoutInflater inflater=getLayoutInflater();
            if(playerHeroesStats.horizontalHeaders!=null){
                for (String header : playerHeroesStats.horizontalHeaders) {
                    int headerId = ResourceUtils.getByHeroStatsHeaders(header);
                    String realHeader;
                    if (headerId != 0) {
                        realHeader = getString(headerId);
                    } else {
                        realHeader = header;
                    }
                    View v = inflater.inflate(R.layout.player_by_hero_stats_header, horizontalHeader, false);
                    ((TextView) v.findViewById(android.R.id.text1)).setText(realHeader);
                    horizontalHeader.addView(v);
                }
            }
            if(playerHeroesStats.heroResults!=null){
                Set<Hero> heroes=playerHeroesStats.heroResults.keySet();
                for(Hero hero:heroes){
                    List<String> results=playerHeroesStats.heroResults.get(hero);
                    View verticalHeaderRow = inflater.inflate(R.layout.player_by_hero_stats_vertical, verticalHeader, false);
                    imageLoader.displayImage(Utils.getHeroFullImage(hero.getDotaId()),
                            (ImageView) verticalHeaderRow.findViewById(R.id.image), options);
                    verticalHeader.addView(verticalHeaderRow);
                    verticalHeaderRow.setOnClickListener(new HeroInfoActivity.OnDotaHeroClickListener(hero.getId()));
                    LinearLayout row = (LinearLayout) inflater.inflate(R.layout.player_by_hero_stats_row, content, false);
                    for (String verticalResult : results) {
                        LinearLayout cell = (LinearLayout) inflater.inflate(R.layout.player_by_hero_stats_cell, row, false);
                        TextView resultTV = (TextView) cell.findViewById(android.R.id.text1);
                        resultTV.setText(verticalResult);
                        row.addView(cell);
                    }
                    content.addView(row);
                }
            }
        }
    }

    public static class PlayerHeroesStats{
        List<String> horizontalHeaders;
        Map<Hero,List<String>> heroResults;
    }
    public static class PlayerHeroesStatsLoadRequest extends TaskRequest<PlayerHeroesStats>{
        private String url;
        private Context context;
        public PlayerHeroesStatsLoadRequest(Context context, String url) {
            super(PlayerHeroesStats.class);
            this.context=context;
            this.url=url;
        }

        @Override
        public PlayerHeroesStats loadData() throws Exception {
            HeroService heroService = BeanContainer.getInstance().getHeroService();
            PlayerHeroesStats result=new PlayerHeroesStats();
            Document doc = Jsoup.connect(url).get();
            Element table = doc.select("table").first();
            Element tableHeader = table.select("thead").first();
            Elements headers = tableHeader.select("th");
            result.horizontalHeaders = new ArrayList<String>();
            for (Element elementHeader : headers) {
                String horizontalHeader = elementHeader.text();
                result.horizontalHeaders.add(horizontalHeader);
            }
            Element tableBody = table.select("tbody").first();
            Elements rows = tableBody.select("tr");
            result.heroResults=new LinkedHashMap<>();
            for (Element row : rows) {
                Hero hero = null;
                List<String> heroResults = new ArrayList<String>();
                if (TextUtils.isEmpty(row.attr("class"))) {
                    Elements columns = row.select("td");
                    for (Element column : columns) {
                        String className = column.attr("class");
                        if (!TextUtils.isEmpty(className)) {
                            if ("cell-xlarge".equals(className)) {
                                Element a = column.select("a").first();
                                String heroName = a.text();
                                List<Hero> possibleHeroes = heroService.getHeroesByName(context, heroName);
                                if (possibleHeroes != null && possibleHeroes.size() > 0) {
                                    hero = possibleHeroes.get(0);
                                    heroResults.add(hero.getLocalizedName());
                                }
                            }
                        } else {
                            String columnResult = column.text();
                            heroResults.add(columnResult);
                        }
                    }
                }
                if(hero!=null){
                    result.heroResults.put(hero,heroResults);
                }
            }
            return result;
        }
    }
}
