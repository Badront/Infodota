package com.badr.infodota.task;

import android.content.Context;
import android.os.Bundle;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.CommonStat;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.fragment.player.details.CommonStats;
import com.badr.infodota.service.hero.HeroService;
import com.badr.infodota.util.ResourceUtils;
import com.badr.infodota.util.retrofit.TaskRequest;

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
 * Created by ABadretdinov
 * 20.08.2015
 * 15:06
 */
public class CommonStatsLoadRequest extends TaskRequest<CommonStats.CommonInfo> {

    private long mAccountId;
    private String mMetric;
    private Bundle mArgs;
    private Context mContext;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm  dd.MM.yyyy");
    private SimpleDateFormat fromSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public CommonStatsLoadRequest(Context context, long accountId, String metric, Bundle args) {
        super(CommonStats.CommonInfo.class);
        this.mContext = context;
        this.mAccountId = accountId;
        this.mMetric = metric;
        this.mArgs = args;
    }

    @Override
    public CommonStats.CommonInfo loadData() throws Exception {
        StringBuilder url = new StringBuilder("http://dotabuff.com/players/");
        url.append(mAccountId);
        url.append("/records");
        url.append("?metric=");
        url.append(mMetric);
        Set<String> keySet = mArgs.keySet();
        for (String key : keySet) {
            url.append("&").append(key).append("=");
            url.append(mArgs.getString(key));
        }

        Document doc = Jsoup.connect(url.toString()).get();
        Elements headerElements = doc.select("div[class=header-content-secondary]");
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
        CommonStats.CommonInfo result = new CommonStats.CommonInfo();
        result.winRate = winRateElement != null ? winRateElement.html() : "NaN%";
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
                    entity.setHeader(ResourceUtils.getStatsHeader(mContext, boxHeader));


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
                    entity.setResult(MessageFormat.format(ResourceUtils.getStatsResultTitle(mContext, boxHeader), gameResult));


                    HeroService heroService = BeanContainer.getInstance().getHeroService();
                    List<Hero> heroes = heroService.getHeroesByName(mContext, heroName);
                    if (heroes != null && heroes.size() > 0) {
                        entity.setHero(heroes.get(0));
                        //add this entity, only if it has hero
                        entities.add(entity);
                    }
                }
                result.stats = entities;
                return result;
            }
        }
        return null;
    }
}
