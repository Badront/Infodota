package com.badr.infodota.task;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.api.responses.HeroResponse;
import com.badr.infodota.api.responses.HeroResponsesSection;
import com.badr.infodota.util.FileUtils;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.google.gson.Gson;

import java.io.File;
import java.util.Random;

/**
 * Created by ABadretdinov
 * 19.08.2015
 * 18:01
 */
public class MediaPlayerForRandomHeroResponseRequest extends TaskRequest<HeroResponse> {
    private String mHeroDotaId;
    private Context mContext;
    private String mSectionName;

    public MediaPlayerForRandomHeroResponseRequest(Context context, Hero hero, String sectionName) {
        super(HeroResponse.class);
        this.mHeroDotaId = hero.getDotaId();
        this.mContext = context;
        this.mSectionName = sectionName;
    }

    @Override
    public HeroResponse loadData() throws Exception {
        String responsesEntity = FileUtils.getTextFromAsset(
                mContext,
                "heroes" + File.separator + mHeroDotaId + File.separator + "responses.json");
        HeroResponsesSection.List sections = new Gson().fromJson(responsesEntity, HeroResponsesSection.List.class);
        if (!TextUtils.isEmpty(mSectionName)) {
            for (HeroResponsesSection section : sections) {
                if (mSectionName.equals(section.getName())) {
                    int size = section.getResponses().size();
                    Random rand = new Random(size);
                    HeroResponse response = section.getResponses().get(Math.min(size - 1, rand.nextInt()));
                    File musicFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "Music" + File.separator + "dota2" + File.separator + mHeroDotaId + File.separator);
                    if (musicFolder.exists()) {
                        String[] urlParts = response.getUrl().split(File.separator);
                        String fileName = musicFolder + File.separator + urlParts[urlParts.length - 1];
                        if (new File(fileName).exists()) {
                            response.setLocalUrl(fileName);
                        }
                    }
                    return response;
                }
            }
        }
        return null;
    }
}
