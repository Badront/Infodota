package com.badr.infodota.fragment.trackdota.game;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badr.infodota.R;
import com.badr.infodota.api.trackdota.core.CoreResult;
import com.badr.infodota.api.trackdota.game.League;
import com.badr.infodota.api.trackdota.game.Team;
import com.badr.infodota.api.trackdota.live.LiveGame;
import com.badr.infodota.util.Refresher;
import com.badr.infodota.util.Updatable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ABadretdinov
 * 14.04.2015
 * 16:44
 */
public class CommonInfo extends Fragment implements Updatable<Pair<CoreResult,LiveGame>> {
    private Refresher refresher;
    private CoreResult coreResult;
    private LiveGame liveGame;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    public static CommonInfo newInstance(Refresher refresher,CoreResult coreResult, LiveGame liveGame){
        CommonInfo fragment=new CommonInfo();
        fragment.refresher=refresher;
        fragment.coreResult=coreResult;
        fragment.liveGame=liveGame;
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.trackdota_game_common,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_item)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
        initView();
    }

    @Override
    public void onUpdate(Pair<CoreResult,LiveGame> entity) {
        this.coreResult=entity.first;
        this.liveGame=entity.second;
        initView();
    }

    private void initView() {
        View root=getView();
        Activity activity=getActivity();
        if(coreResult!=null&&root!=null&&activity!=null){
            League league=coreResult.getLeague();
            if(league!=null) {
                if (league.isHasImage()) {
                    imageLoader.displayImage("http://www.trackdota.com/data/images/leagues/" + league.getId() + ".jpg", (ImageView) root.findViewById(R.id.league_logo), options);
                }
                ((TextView) root.findViewById(R.id.league_name)).setText(league.getName());
            }
            TextView gameRdWins=(TextView)root.findViewById(R.id.game_rd_wins);
            gameRdWins.setText(coreResult.getRadiantWins()+" - "+coreResult.getDireWins());
            StringBuilder gameState=new StringBuilder("Game ");
            gameState.append(coreResult.getDireWins()+coreResult.getRadiantWins()+1);
            gameState.append(" / BO");
            switch (coreResult.getSeriesType()){
                case 0:
                    gameState.append(1);
                    gameRdWins.setText("-");
                    break;
                case 1:
                    gameState.append(3);
                    break;
                default:
                    gameState.append("{").append(coreResult.getSeriesType()).append("}");
            }
            ((TextView)root.findViewById(R.id.game_state)).setText(gameState.toString());
            ((TextView)root.findViewById(R.id.viewers)).setText(coreResult.getSpectators()+" viewers");
            SimpleDateFormat dateTimeFormat=new SimpleDateFormat("HH:mm  dd.MM.yyyy");
            ((TextView) root.findViewById(R.id.game_start_time)).setText(dateTimeFormat.format(new Date(coreResult.getStartTime()*1000L)));

            Team radiant=coreResult.getRadiant();
            if(radiant!=null){
                ((TextView)root.findViewById(R.id.radiant_tag)).setText(!TextUtils.isEmpty(radiant.getTag())?radiant.getTag():"Radiant");
                ((TextView)root.findViewById(R.id.radiant_name)).setText(!TextUtils.isEmpty(radiant.getName())?radiant.getName():"Radiant");
                if(radiant.isHasLogo()){
                    imageLoader.displayImage("http://www.trackdota.com/data/images/teams/"+radiant.getId()+".png", (ImageView) root.findViewById(R.id.radiant_logo), options);
                }
            }
            Team dire=coreResult.getDire();
            if(dire!=null){
                ((TextView)root.findViewById(R.id.dire_tag)).setText(!TextUtils.isEmpty(dire.getTag())?dire.getTag():"Dire");
                ((TextView)root.findViewById(R.id.dire_name)).setText(!TextUtils.isEmpty(dire.getName())?dire.getName():"Dire");
                if(dire.isHasLogo()){
                    imageLoader.displayImage("http://www.trackdota.com/data/images/teams/"+dire.getId()+".png", (ImageView) root.findViewById(R.id.dire_logo), options);
                }
            }
            long minutes=coreResult.getDuration()/60;
            long seconds=coreResult.getDuration()-minutes*60;
            ((TextView)root.findViewById(R.id.game_duration)).setText(minutes+":"+(seconds<10?"0":"")+seconds);
            //todo net worth advantage team tag, gold advantage, roshan status, kills - from LiveGame
            //todo pickBan
        }
    }
}
