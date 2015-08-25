package com.util.infoparser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.badr.infodota.R;
import com.badr.infodota.base.activity.LoaderActivity;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.util.infoparser.loader.CosmeticItemsLoadRequest;
import com.util.infoparser.loader.CosmeticItemsStringsLoadRequest;
import com.util.infoparser.loader.ResponseLoadRequest;

/**
 * Created by ABadretdinov
 * 19.06.2015
 * 15:20
 */
public class InfoParserActivity extends Activity implements RequestListener {
    private SpiceManager mSpiceManager = new SpiceManager(UncachedSpiceService.class);
    private LoadType mCurLoadType = LoadType.cosmetic_items_english;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }

    @Override
    protected void onStart() {
        if (!mSpiceManager.isStarted()) {
            mSpiceManager.start(this);
            runTask();
        }
        super.onStart();
    }

    private void runTask() {
        switch (mCurLoadType) {
            case response:
                mSpiceManager.execute(new ResponseLoadRequest(getApplicationContext()), this);
                break;
            case cosmetic_items:
                mSpiceManager.execute(new CosmeticItemsLoadRequest(getApplicationContext()), this);
                break;
            case cosmetic_items_english:
                mSpiceManager.execute(new CosmeticItemsStringsLoadRequest(getApplicationContext(), "english"), this);
                break;
            case cosmetic_items_russian:
                mSpiceManager.execute(new CosmeticItemsStringsLoadRequest(getApplicationContext(), "russian"), this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {

    }

    @Override
    public void onRequestSuccess(Object o) {
        if (mCurLoadType == LoadType.cosmetic_items_english) {
            mCurLoadType = LoadType.cosmetic_items_russian;
            runTask();
        } else {
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoaderActivity.class));
            finish();
        }
    }

    enum LoadType {
        response,
        cosmetic_items,
        cosmetic_items_english,
        cosmetic_items_russian
    }
}
