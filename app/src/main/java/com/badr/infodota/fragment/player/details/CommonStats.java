package com.badr.infodota.fragment.player.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.MatchInfoActivity;
import com.badr.infodota.adapter.CommonStatsAdapter;
import com.badr.infodota.adapter.holder.CommonStatHolder;
import com.badr.infodota.api.CommonStat;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.fragment.RecyclerFragment;
import com.badr.infodota.service.hero.HeroService;
import com.badr.infodota.util.ResourceUtils;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

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
public class CommonStats extends RecyclerFragment<CommonStat,CommonStatHolder> implements RequestListener<CommonStats.CommonInfo>{
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);
    protected ImageLoader imageLoader;
    DisplayImageOptions options;
    private String metric;
    private Unit account;
    private boolean initialized=false;
    public static CommonStats newInstance(Unit account, Bundle args, String metric) {
        CommonStats fragment = new CommonStats();
        fragment.setArguments(args);
        fragment.setMetric(metric);
        fragment.setAccount(account);
        return fragment;
    }

    @Override
    public void onStart() {
        if(!spiceManager.isStarted()) {
            spiceManager.start(getActivity());
            if(!initialized){
                onRefresh();
            }
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        initialized=false;
        super.onDestroy();
    }

    public void setAccount(Unit account) {
        this.account = account;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager(Context context) {
        return new GridLayoutManager(context,1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader = ImageLoader.getInstance();
        setColumnSize();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setColumnSize();
    }

    private void setColumnSize() {
        if(getRecyclerView()!=null) {
            if (getResources().getBoolean(R.bool.is_tablet)) {
                ((GridLayoutManager) getRecyclerView().getLayoutManager()).setSpanCount(2);
            } else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    ((GridLayoutManager) getRecyclerView().getLayoutManager()).setSpanCount(2);
                } else {
                    ((GridLayoutManager) getRecyclerView().getLayoutManager()).setSpanCount(1);
                }
            }
        }
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        initialized=true;
        setRefreshing(false);
        Toast.makeText(getActivity(),spiceException.getLocalizedMessage(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(CommonInfo commonInfo) {
        initialized=true;
        setRefreshing(false);
        ActionBarActivity activity= (ActionBarActivity) getActivity();
        if(activity!=null&&commonInfo!=null) {
            activity.getSupportActionBar().setSubtitle(MessageFormat
                    .format(getString(R.string.record_with_win_rate), commonInfo.wins, commonInfo.loses, commonInfo.winRate));
            setAdapter(new CommonStatsAdapter(activity,commonInfo.stats));
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        CommonStat entity=getAdapter().getItem(position);
        Intent intent = new Intent(view.getContext(), MatchInfoActivity.class);
        intent.putExtra("matchId", entity.getMatchId());
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        setRefreshing(true);
        spiceManager.execute(new CommonStatLoadRequest(account.getAccountId(), metric, getArguments()), this);
    }

    public class CommonStatLoadRequest extends TaskRequest<CommonInfo>{

        private long accountId;
        private String metric;
        private Bundle args;
        private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm  dd.MM.yyyy");
        private SimpleDateFormat fromSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        public CommonStatLoadRequest(long accountId, String metric,Bundle args) {
            super(CommonInfo.class);
            this.accountId=accountId;
            this.metric=metric;
            this.args=args;
        }

        @Override
        public CommonInfo loadData() throws Exception {
            Activity activity=getActivity();
            if(activity!=null) {
                StringBuilder url = new StringBuilder("http://dotabuff.com/players/");
                url.append(accountId);
                url.append("/records");
                url.append("?metric=");
                url.append(metric);
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
                CommonInfo result=new CommonInfo();
                result.winRate=winRateElement != null ? winRateElement.html() : "NaN%";
                result.wins = wins;
                result.loses = lost;
                Elements recordsElements = doc.select("div[class=player-records]");
                if (recordsElements != null && recordsElements.size() != 0) {
                    Elements boxes = recordsElements.select("div[class=record]");
                    if (boxes != null && boxes.size() > 0) {
                        CommonStat.List entities = new CommonStat.List();
                        for (Element box : boxes) {
                            CommonStat entity = new CommonStat();
                            String boxHeader = box.select("div[class=title]").first().html();
                            entity.setHeader(ResourceUtils.getStatsHeader(activity, boxHeader));


                            Element details = box.select("div[class=details]").first();
                            String matchUrl = box.select("a").first().attr("href");
                            String[] parts = matchUrl.split("/");
                            String matchId = parts[parts.length - 1];
                            entity.setMatchId(matchId);

                            List<TextNode> detailsNodes = details.textNodes();
                            TextNode wonNode = detailsNodes.get(0);
                            String wonText = wonNode.text();
                            boolean won = wonText.contains("Won");
                            entity.setWon(won);

                            Element timeAgo = details.select("time").first();
                            String dateTimeWas = timeAgo.attr("datetime");
                            Date date = fromSdf.parse(dateTimeWas);
                            String dateTime = sdf.format(date);
                            entity.setDateTime(dateTime);

                            String heroName = box.select("div[class=hero]").first().text();
                            heroName = heroName.substring(0, heroName.indexOf(" (") - 1);

                            String gameResult = box.select("div[class=value]").first().text();
                            entity.setResult(MessageFormat.format(ResourceUtils.getStatsResultTitle(activity, boxHeader), gameResult));


                            HeroService heroService = BeanContainer.getInstance().getHeroService();
                            List<Hero> heroes = heroService.getHeroesByName(activity, heroName);
                            if (heroes != null && heroes.size() > 0) {
                                entity.setHero(heroes.get(0));
                                //add this entity, only if it has hero
                                entities.add(entity);
                            }
                        }
                        result.stats=entities;
                        return result;
                    }
                }
            }
            return null;
        }
    }
    public static class CommonInfo{
        CommonStat.List stats;
        String wins;
        String loses;
        String winRate;
    }
}
