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
import com.util.infoparser.loader.ResponseLoadRequest;

/**
 * Created by ABadretdinov
 * 19.06.2015
 * 15:20
 */
public class InfoParserActivity extends Activity implements RequestListener {
    private SpiceManager mSpiceManager = new SpiceManager(UncachedSpiceService.class);
    private LoadType mCurLoadType=LoadType.cosmetic_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }

    @Override
    protected void onStart() {
        if (!mSpiceManager.isStarted()) {
            mSpiceManager.start(this);
            switch (mCurLoadType) {
                case response:
                    mSpiceManager.execute(new ResponseLoadRequest(getApplicationContext()), this);
                    break;
                case cosmetic_items:
                    mSpiceManager.execute(new CosmeticItemsLoadRequest(getApplicationContext()), this);
            }
        }
        super.onStart();
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
        Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, LoaderActivity.class));
        finish();
    }

    enum LoadType {
        response,
        cosmetic_items
    }
}
