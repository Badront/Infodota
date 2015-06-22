package com.util.infoparser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.LoaderActivity;
import com.badr.infodota.api.Constants;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.api.items.Item;
import com.badr.infodota.api.responses.HeroResponse2;
import com.badr.infodota.api.responses.HeroResponses2Section;
import com.badr.infodota.service.hero.HeroService;
import com.badr.infodota.service.item.ItemService;
import com.badr.infodota.util.FileUtils;
import com.badr.infodota.util.retrofit.TaskRequest;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ABadretdinov
 * 19.06.2015
 * 15:20
 */
public class InfoParserActivity extends Activity implements RequestListener {
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }

    @Override
    protected void onStart() {
        if (!spiceManager.isStarted()) {
            spiceManager.start(this);
            spiceManager.execute(new ResponseLoadRequest(getApplicationContext()), this);
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onDestroy();
    }


    @Override
    public void onRequestFailure(SpiceException spiceException) {

    }

    @Override
    public void onRequestSuccess(Object o) {
        Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, LoaderActivity.class));
        finish();
    }

    public static class ResponseLoadRequest extends TaskRequest<String> {
        private Context mContext;
        HeroService heroService = BeanContainer.getInstance().getHeroService();
        ItemService itemService = BeanContainer.getInstance().getItemService();

        public ResponseLoadRequest(Context context) {
            super(String.class);
            this.mContext = context;
        }

        @Override
        public String loadData() throws Exception {
            List<Hero> heroes = heroService.getAllHeroes(mContext);
            for (Hero hero : heroes) {
                List<HeroResponses2Section> responses = loadHeroResponses(hero);
                FileUtils.saveJsonFile(Environment.getExternalStorageDirectory().getPath() + "/dota/" + hero.getDotaId() + "/responses.json", responses);
            }
            return "";
        }

        /*do not use /ru, otherwise you won't have items*/
        private List<HeroResponses2Section> loadHeroResponses(Hero hero) throws Exception {
            String heroName = hero.getLocalizedName().replace("'", "%27").replace(' ', '_');

            String url = MessageFormat.format(Constants.Heroes.DOTA2_WIKI_RESPONSES_URL, heroName);
            System.out.println("hero url: " + url);
            Document doc = Jsoup.connect(url).get();
            Element content = doc.select("div#content").first();
            Elements spanList = content.select("span[class=mw-headline]");
            List<HeroResponses2Section> heroResponsesList = new ArrayList<>();
            String code=null;
            if (spanList != null && !spanList.isEmpty()) {
                Element firstH2 = spanList.first().parent(); //h2
                if (firstH2 != null) {
                    Elements contentChildren = content.children();
                    int index = contentChildren.indexOf(firstH2);
                    boolean isH2 = true;
                    if(index<0){//mb change for while index<0 and change from first to get(i)
                        contentChildren=contentChildren.first().children();
                        index=contentChildren.indexOf(firstH2);
                    }
                    for (int i = index, size = contentChildren.size() - 1; i < size && isH2; i++) {
                        Element h2 = contentChildren.get(i);
                        if ("h2".equals(h2.tagName())) {
                            i++;
                            Element ul = contentChildren.get(i).select("ul").first();
                            String sectionName=h2.child(0).text();
                            //todo тут могут быть вложенные span с предметами и скилами. исследуй это
                            if(ul!=null) {
                                HeroResponses2Section section = new HeroResponses2Section();
                                section.setCode(code);
                                section.setName(sectionName);
                                Elements headerResponses = ul.select("li");
                                section.setResponses(new ArrayList<HeroResponse2>());
                                for (Element elResponse : headerResponses) {
                                    Elements alist = elResponse.select("a[title=Play]");
                                    if (alist != null && !alist.isEmpty()) {
                                        Element aWithLink = alist.first();
                                        HeroResponse2 response2 = getHeroResponse2(aWithLink);
                                        if (response2 != null) {
                                            section.getResponses().add(response2);
                                        }
                                    }
                                }
                                heroResponsesList.add(section);
                            }
                            else {
                                code=sectionName;
                            }
                        }
                        else if("h1".equals(h2.tagName())) {
                            code=h2.child(0).text();
                        }
                        else {
                            isH2 = false;
                        }
                    }
                }
            } else {
                HeroResponses2Section section=new HeroResponses2Section();
                section.setName("All responses");
                section.setResponses(new ArrayList<HeroResponse2>());
                Elements elements = doc.select("a[title=Play]");
                for(Element element:elements){
                    HeroResponse2 response2=getHeroResponse2(element);
                    if(response2!=null){
                        section.getResponses().add(response2);
                    }
                }
                heroResponsesList.add(section);
            }
            return heroResponsesList;
        }

        private HeroResponse2 getHeroResponse2(Element aWithLink) {
            HeroResponse2 response2 = null;
            if (aWithLink != null && aWithLink.hasAttr("href")) {
                Element elResponse = aWithLink.parent();
                response2 = new HeroResponse2();
                response2.setUrl(aWithLink.attr("href"));
                String title = null;
                List<TextNode> nodes = elResponse.textNodes();
                if (nodes.size() > 0) {
                    title = nodes.get(nodes.size() - 1).toString();
                }
                response2.setTitle(title);
                response2.setHeroes(new ArrayList<String>());
                response2.setItems(new ArrayList<String>());
                Elements children = elResponse.children();
                for (int j = 1, elResponseChildSize = children.size(); j < elResponseChildSize; j++) {
                    Element child = children.get(j);
                    String extraFieldName = child.attr("title");
                    if (extraFieldName.contains("Runes")) {
                        Element img = child.select("img").first();
                        String imgAlt = img.attr("alt");
                        if (imgAlt.contains("Double Damage")) {
                            response2.setRune("dd");
                        } else if (imgAlt.contains("Haste")) {
                            response2.setRune("haste");
                        } else if (imgAlt.contains("Illusion")) {
                            response2.setRune("illus");
                        } else if (imgAlt.contains("Invisibility")) {
                            response2.setRune("invis");
                        } else if (imgAlt.contains("Regeneration")) {
                            response2.setRune("regen");
                        }
                    } else {
                        List<Hero> heroesWithThisName = heroService.getHeroesByName(mContext, extraFieldName);
                        if (heroesWithThisName != null && heroesWithThisName.size() > 0) {
                            response2.getHeroes().add(heroesWithThisName.get(0).getDotaId());
                        } else {
                            List<Item> itemsWithThisName = itemService.getItemsByName(mContext, extraFieldName);
                            if (itemsWithThisName != null && itemsWithThisName.size() > 0) {
                                response2.getItems().add(itemsWithThisName.get(0).getDotaId());
                            }
                        }
                    }
                }
            }
            return response2;
        }

    }
}
