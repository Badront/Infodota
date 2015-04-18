package com.badr.infodota.fragment.trackdota.game;

import android.support.v4.app.Fragment;
import android.util.Pair;

import com.badr.infodota.api.trackdota.core.CoreResult;
import com.badr.infodota.api.trackdota.live.LiveGame;
import com.badr.infodota.util.Refresher;
import com.badr.infodota.util.Updatable;

/**
 * Created by ABadretdinov
 * 18.04.2015
 * 11:31
 */
public class Graphs extends Fragment implements Updatable<Pair<CoreResult,LiveGame>> {
    private Refresher refresher;
    private CoreResult coreResult;
    private LiveGame liveGame;
    public static Graphs newInstance(Refresher refresher,CoreResult coreResult,LiveGame liveGame){
        Graphs fragment=new Graphs();
        fragment.refresher=refresher;
        fragment.coreResult=coreResult;
        fragment.liveGame=liveGame;
        return fragment;
    }
    @Override
    public void onUpdate(Pair<CoreResult, LiveGame> entity) {
        this.coreResult=entity.first;
        this.liveGame=entity.second;
    }
}
