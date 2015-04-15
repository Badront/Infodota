package com.badr.infodota.fragment.trackdota.game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.HeroInfoActivity;
import com.badr.infodota.api.trackdota.core.CoreResult;
import com.badr.infodota.api.trackdota.live.LiveGame;
import com.badr.infodota.api.trackdota.live.Player;
import com.badr.infodota.api.trackdota.live.Team;
import com.badr.infodota.service.hero.HeroService;
import com.badr.infodota.util.Refresher;
import com.badr.infodota.util.Updatable;
import com.badr.infodota.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by ABadretdinov
 * 15.04.2015
 * 12:37
 */
public class MapAndHeroes extends Fragment implements Updatable<Pair<CoreResult,LiveGame>> {
    private Refresher refresher;
    private CoreResult coreResult;
    private LiveGame liveGame;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private HeroService heroService= BeanContainer.getInstance().getHeroService();
    final private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if(refresher!=null) {
                mScrollContainer.setRefreshing(true);
                refresher.onRefresh();
            }
        }
    };
    public static MapAndHeroes newInstance(Refresher refresher,CoreResult coreResult,LiveGame liveGame){
        MapAndHeroes fragment=new MapAndHeroes();
        fragment.refresher=refresher;
        fragment.coreResult=coreResult;
        fragment.liveGame=liveGame;
        return fragment;
    }
    private SwipeRefreshLayout mScrollContainer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.trackdota_game_map_n_heroes,container,false);

        mScrollContainer = (SwipeRefreshLayout) view.findViewById(R.id.listContainer);
        mScrollContainer.setOnRefreshListener(mOnRefreshListener);
        mScrollContainer.setColorSchemeResources(R.color.primary);
        return view;
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
        mScrollContainer.setRefreshing(false);
        this.coreResult=entity.first;
        this.liveGame=entity.second;
        initView();
    }
    private int mMapWidth;
    private int mMapHeight;
    private void initView(){
        View root=getView();
        Activity activity=getActivity();
        if(liveGame!=null&&root!=null&&activity!=null){
            RelativeLayout mapObjectsHolder= (RelativeLayout) root.findViewById(R.id.map_objects_holder);
            mapObjectsHolder.removeAllViews();
            View map=root.findViewById(R.id.dota_map);
            mMapHeight=map.getHeight();
            mMapWidth=map.getWidth();
            if(liveGame.getRadiant()!=null) {
                initTeam(mapObjectsHolder,liveGame.getRadiant(),0);
            }
            if(liveGame.getDire()!=null){
                initTeam(mapObjectsHolder,liveGame.getDire(),1);
            }

        }
    }
    /*align - Radiant=0, Dire=1*/
    @SuppressWarnings("deprecation")
    private void initTeam(RelativeLayout holder, Team team, int align){
        Activity activity=getActivity();
        LayoutInflater inflater=activity.getLayoutInflater();
        if(team.getPlayers()!=null){
            int radiantColor=activity.getResources().getColor(R.color.radiant_transparent);
            int radiantDeadColor=activity.getResources().getColor(R.color.radiant_transparent_dead);
            int direColor=activity.getResources().getColor(R.color.dire_transparent);
            int direDeadColor=activity.getResources().getColor(R.color.dire_transparent_dead);
            for(Player player:team.getPlayers()){
                View row=inflater.inflate(R.layout.trackdota_map_minihero,holder,false);
                imageLoader.displayImage("assets://heroes/" + player.getHero().getDotaId() + "/mini.png",
                        (ImageView) row.findViewById(R.id.image),
                        options);
                final long heroId=player.getHeroId();
                //todo переделать
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), HeroInfoActivity.class);
                        intent.putExtra("id", heroId);
                        startActivity(intent);
                    }
                });
                RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int left=(int)(0.95f*player.getPositionX()/100*mMapWidth);
                int top=(int)(0.95f*player.getPositionY()/100*mMapHeight);
                lp.setMargins(left,top,0,0);
                row.setLayoutParams(lp);
                int circleSize= Utils.dpSize(activity,40);
                Bitmap bitmap=Bitmap.createBitmap(circleSize,circleSize, Bitmap.Config.ARGB_8888);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                Canvas canvas=new Canvas(bitmap);
                switch (align){
                    case 0:
                        if(player.getRespawnTimer()>0){
                            paint.setColor(radiantDeadColor);
                        }
                        else {
                            paint.setColor(radiantColor);
                        }
                        break;
                    case 1:
                        if(player.getRespawnTimer()>0){
                            paint.setColor(direDeadColor);
                        }
                        else {
                            paint.setColor(direColor);
                        }
                        break;
                }
                canvas.drawCircle(circleSize/2,circleSize/2,circleSize/2,paint);
                BitmapDrawable drawable=new BitmapDrawable(activity.getResources(),bitmap);
                row.setBackgroundDrawable(drawable);
                holder.addView(row);
            }
        }
    }
}
