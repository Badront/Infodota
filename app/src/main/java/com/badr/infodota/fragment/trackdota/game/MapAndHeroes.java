package com.badr.infodota.fragment.trackdota.game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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
    private Point[] towers = new Point[]{
            new Point(13, 39), new Point(13, 55), new Point(9, 71), new Point(40, 59),
            new Point(28, 68), new Point(22, 74), new Point(82, 86), new Point(46, 88),
            new Point(26, 87), new Point(15, 79), new Point(18, 82), new Point(21, 13),
            new Point(50, 13), new Point(71, 14), new Point(56, 48), new Point(65, 37),
            new Point(75, 27), new Point(88, 61), new Point(88, 47), new Point(88, 32),
            new Point(79, 19), new Point(82, 22)
    };
    private Point[] barracks = new Point[]{
            new Point(10, 73), new Point(6, 73), new Point(19, 76),
            new Point(17, 74), new Point(22, 84), new Point(22, 88),
            new Point(72, 15), new Point(72, 11), new Point(77, 24),
            new Point(75, 22), new Point(89, 26), new Point(85, 26)
    };
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
            drawBuildings(mapObjectsHolder,liveGame.getTowerState(),liveGame.getBarracksState());
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
            int radiantColor=getResources().getColor(R.color.radiant_transparent);
            int radiantDeadColor=getResources().getColor(R.color.radiant_transparent_dead);
            int direColor=getResources().getColor(R.color.dire_transparent);
            int direDeadColor=getResources().getColor(R.color.dire_transparent_dead);
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
    private void drawBuildings(RelativeLayout holder, int towersState,int barracksState){
        ImageView buildingsImage=new ImageView(getActivity());
        buildingsImage.setMinimumWidth(mMapWidth);
        buildingsImage.setMinimumHeight(mMapHeight);
        Bitmap bitmap=Bitmap.createBitmap(mMapWidth,mMapHeight, Bitmap.Config.ARGB_8888);
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        Canvas canvas=new Canvas(bitmap);
        int towerMaxRadius=Utils.dpSize(getActivity(),7);
        int towerMinRadius=Utils.dpSize(getActivity(),5);
        int radiantColor=getResources().getColor(R.color.ally_team);
        int radiantInnerColor=getResources().getColor(R.color.radiant_transparent);
        int direColor=getResources().getColor(R.color.enemy_team);
        int direInnerColor=getResources().getColor(R.color.dire_transparent);

        for(int i=0;i<towers.length;i++){
            int alive=1<<i&towersState;
            int left=(int)(towers[i].x/100f*mMapWidth);
            int top=(int)(towers[i].y/100f*mMapHeight);

            if(alive==0){
                paint.setColor(Color.WHITE);
            }
            /*если 1ая половина, то это Radiant*/
            else if(i<11){
                paint.setColor(radiantColor);
            }
            else {
                paint.setColor(direColor);
            }
            canvas.drawCircle(left,top,towerMaxRadius,paint);

            if(alive==0){
                paint.setColor(Color.BLACK);
            }
            else if(i<11){
                paint.setColor(radiantInnerColor);
            }
            else {
                paint.setColor(direInnerColor);
            }
            canvas.drawCircle(left,top,towerMinRadius,paint);
        }
        int barrackSize=Utils.dpSize(getActivity(),8);
        int barrackInnerMargin=Utils.dpSize(getActivity(),2);
        int barrackInnerSize=Utils.dpSize(getActivity(),6);
        for(int i=0;i<barracks.length;i++){
            int alive=1<<i&barracksState;
            int left=(int)(barracks[i].x/100f*mMapWidth);
            int top=(int)(barracks[i].y/100f*mMapHeight);
            if(alive==0){
                paint.setColor(Color.WHITE);
            }
            /*если 1ая половина, то это Radiant*/
            else if(i<6){
                paint.setColor(radiantColor);
            }
            else {
                paint.setColor(direColor);
            }
            canvas.drawRect(
                    left,
                    top,
                    left+barrackSize,
                    top+barrackSize,
                    paint);

            if(alive==0){
                paint.setColor(Color.BLACK);
            }
            else if(i<6){
                paint.setColor(radiantInnerColor);
            }
            else {
                paint.setColor(direInnerColor);
            }
            canvas.drawRect(
                    left+barrackInnerMargin,
                    top+barrackInnerMargin,
                    left+barrackInnerSize,
                    top+barrackInnerSize,
                    paint);
        }
        buildingsImage.setAdjustViewBounds(true);
        buildingsImage.setImageBitmap(bitmap);
        holder.addView(buildingsImage);
    }
}
