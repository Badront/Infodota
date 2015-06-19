package com.util.infoparser;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.api.Constants;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.api.items.Item;
import com.badr.infodota.api.responses.HeroResponse2;
import com.badr.infodota.api.responses.HeroResponses2Section;
import com.badr.infodota.service.hero.HeroService;
import com.badr.infodota.service.item.ItemService;
import com.badr.infodota.util.FileUtils;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.google.gson.Gson;
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
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }

    @Override
    protected void onStart() {
        if(!spiceManager.isStarted()){
            spiceManager.start(this);
            spiceManager.execute(new ResponseLoadRequest(getApplicationContext()),this);
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onDestroy();
    }


    @Override
    public void onRequestFailure(SpiceException spiceException) {

    }

    @Override
    public void onRequestSuccess(Object o) {

    }

    public static class ResponseLoadRequest extends TaskRequest<String>{
        private Context mContext;
        HeroService heroService= BeanContainer.getInstance().getHeroService();
        ItemService itemService=BeanContainer.getInstance().getItemService();
        public ResponseLoadRequest(Context context) {
            super(String.class);
            this.mContext=context;
        }

        @Override
        public String loadData() throws Exception {
            List<Hero> heroes=heroService.getAllHeroes(mContext);
            for(Hero hero:heroes){
                List<HeroResponses2Section> responses=loadHeroResponses(hero);
                FileUtils.saveJsonFile(Environment.getExternalStorageDirectory().getPath() + "/dota/"+hero.getDotaId() + "/responses.json",responses);
            }
            return "";
        }
        /*do not use /ru, otherwise you won't have items*/
        private List<HeroResponses2Section> loadHeroResponses(Hero hero) throws Exception{
            String heroName = hero.getLocalizedName().replace("'", "%27").replace(' ', '_');

            String url = MessageFormat.format(Constants.Heroes.DOTA2_WIKI_RESPONSES_URL, heroName);
            System.out.println("hero url: " + url);
            Document doc = Jsoup.connect(url).get();
            Elements holderDev=doc.select("div[class=mw-content-ltr]");// div - content и глубже.
            List<HeroResponses2Section> heroResponsesList=new ArrayList<>();
            if(holderDev!=null&&holderDev.size()>0){
                Elements headers=holderDev.select("span[class=mw-headline]");
                Elements responses=holderDev.select("ul");

                if(headers!=null&&headers.size()>0){
                    for(int i=0,size=headers.size();i<size;i++){
                        Element header=headers.get(i);
                        Element headerResponsesHolder=responses.get(i);
                        HeroResponses2Section section=new HeroResponses2Section();
                        section.setName(header.text());
                        //todo тут могут быть вложенные span с предметами и скилами. исследуй это
                        Elements headerResponses=headerResponsesHolder.select("li");
                        section.setResponses(new ArrayList<HeroResponse2>());
                        for(Element elResponse:headerResponses){
                            HeroResponse2 response2=new HeroResponse2();
                            response2.setUrl(elResponse.select("a[title=Play]").first().attr("href"));
                            String title=null;
                            List<TextNode> nodes=elResponse.textNodes();
                            if(nodes.size()>0){
                                title=nodes.get(nodes.size()-1).toString();
                            }
                            response2.setTitle(title);
                            response2.setHeroes(new ArrayList<String>());
                            response2.setItems(new ArrayList<String>());
                            Elements children=elResponse.children();
                            for(int j=1,elResponseChildSize=children.size();j<elResponseChildSize;j++){
                                Element child=children.get(j);
                                String otherHeroName=child.attr("title");
                                if(otherHeroName.contains("Runes")){
                                    Element img=child.select("img").first();
                                    String imgAlt=img.attr("alt");
                                    if(imgAlt.contains("Double Damage")){
                                        response2.setRune("dd");
                                    }
                                    else if(imgAlt.contains("Haste")){
                                        response2.setRune("haste");
                                    }
                                    else if(imgAlt.contains("Illusion")){
                                        response2.setRune("illus");
                                    }
                                    else if(imgAlt.contains("Invisibility")){
                                        response2.setRune("invis");
                                    }
                                    else if(imgAlt.contains("Regeneration")){
                                        response2.setRune("regen");
                                    }
                                }else {
                                    List<Hero> heroesWithThisName = heroService.getHeroesByName(mContext, otherHeroName);
                                    if (heroesWithThisName != null && heroesWithThisName.size() > 0) {
                                        response2.getHeroes().add(heroesWithThisName.get(0).getDotaId());
                                    } else {
                                        List<Item> itemsWithThisName=itemService.getItemsByName(mContext, otherHeroName);
                                        if(itemsWithThisName!=null&&itemsWithThisName.size()>0){
                                            response2.getItems().add(itemsWithThisName.get(0).getDotaId());
                                        }
                                    }
                                }
                            }
                            section.getResponses().add(response2);
                        }
                        heroResponsesList.add(section);
                    }
                }
            }

            return heroResponsesList;
        }

    }
}
