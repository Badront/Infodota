package com.badr.infodota.task;

import android.content.Context;
import android.content.res.AssetManager;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.service.LocalUpdateService;
import com.badr.infodota.util.FileUtils;
import com.badr.infodota.util.retrofit.TaskRequest;

import java.io.File;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 16:59
 */
public class UpdateLoadRequest extends TaskRequest<String> {

    private Context mContext;

    public UpdateLoadRequest(Context context) {
        super(String.class);
        this.mContext = context;
    }

    @Override
    public String loadData() throws Exception {
        LocalUpdateService localUpdateService = BeanContainer.getInstance().getLocalUpdateService();
        AssetManager assetManager = mContext.getAssets();
        String[] files = assetManager.list("updates");
        for (String fileName : files) {
            int fileVersion = Integer.valueOf(fileName.split("\\.")[0]);
            String sql = FileUtils.getTextFromAsset(mContext, "updates" + File.separator + fileName);
            localUpdateService.update(mContext, sql, fileVersion);
        }
        return "";
    }
}
