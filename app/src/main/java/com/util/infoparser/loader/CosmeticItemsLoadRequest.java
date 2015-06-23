package com.util.infoparser.loader;

import android.content.Context;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.cosmetics.store.StoreResult;
import com.badr.infodota.remote.SteamService;
import com.badr.infodota.util.FileUtils;
import com.badr.infodota.util.VDFtoJsonParser;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.util.infoparser.remote.URLRemoteService;

/**
 * Created by ABadretdinov
 * 23.06.2015
 * 16:07
 */
public class CosmeticItemsLoadRequest extends TaskRequest<String> {
    private Context mContext;
    public CosmeticItemsLoadRequest(Context context) {
        super(String.class);
        mContext=context;
    }

    @Override
    public String loadData() throws Exception {
        BeanContainer beanContainer=BeanContainer.getInstance();
        SteamService steamService=beanContainer.getSteamService();
        StoreResult storeResult=steamService.getCosmeticItems("ru");
        if(storeResult!=null&&storeResult.getResult()!=null){
            String path=storeResult.getResult().getItems_game_url();
            URLRemoteService urlRemoteService=new URLRemoteService();
            String items=urlRemoteService.loadResult(mContext,path);
            FileUtils.saveStringFile("cosmetic_items.txt", items);

            String parsedItems=VDFtoJsonParser.parse(items);
            items=null;
            FileUtils.saveStringFile("cosmetic_items.json",parsedItems);

        }
        return null;
    }
}
