package com.badr.infodota.fragment.hero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.ItemInfoActivity;
import com.badr.infodota.api.guide.GuideItems;
import com.badr.infodota.api.guide.valve.Guide;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.api.items.Item;
import com.badr.infodota.service.item.ItemService;
import com.badr.infodota.util.FileUtils;
import com.badr.infodota.view.FlowLayout;
import com.google.gson.Gson;

/**
 * User: Histler
 * Date: 19.01.14
 */
public class HeroDefaultItemBuild extends Fragment {
    private Guide guide;
    private Hero hero;

    public static HeroDefaultItemBuild newInstance(Hero hero) {
        HeroDefaultItemBuild fragment = new HeroDefaultItemBuild();
        fragment.hero = hero;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hero_default_itembuild, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String guideEntity = FileUtils.getTextFromAsset(getActivity(), "heroes/" + hero.getDotaId() + "/default.json");
        guide = new Gson().fromJson(guideEntity, Guide.class);
        initGuide();
    }

    private void initGuide() {
        GuideItems guideItems = guide.getItems();//todo nullPointer.
        View fragmentView = getView();
        ItemService itemService = BeanContainer.getInstance().getItemService();
        Activity activity = getActivity();
        FlowLayout startingItems = (FlowLayout) fragmentView.findViewById(R.id.starting_items);
        startingItems.removeAllViews();
        for (String itemName : guideItems.getStartingItems()) {
            Item item = itemService.getItemByDotaId(activity, itemName);
            if (item == null) {
                Log.d(HeroDefaultItemBuild.class.getName(), "error loading item: " + itemName);
            } else {
                LinearLayout row = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_recept_row, null);
                ((ImageView) row.findViewById(R.id.img)).setImageDrawable(FileUtils.getDrawableFromAsset(getActivity(), "items/" + item.getDotaId() + ".png"));
                ((TextView) row.findViewById(R.id.name)).setText(item.getDname());
                ((TextView) row.findViewById(R.id.cost)).setText(String.valueOf(item.getCost()));
                final long id = item.getId();
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ItemInfoActivity.class);
                        intent.putExtra("id", id);
                        startActivityForResult(intent, 1);
                    }
                });
                startingItems.addView(row);
            }
        }

        FlowLayout earlyGameItems = (FlowLayout) fragmentView.findViewById(R.id.early_game);
        earlyGameItems.removeAllViews();
        for (String itemName : guideItems.getEarlyGame()) {
            Item item = itemService.getItemByDotaId(activity, itemName);
            if (item == null) {
                Log.d(HeroDefaultItemBuild.class.getName(), "error loading item: " + itemName);
            } else {
                LinearLayout row = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_recept_row, null);
                ((ImageView) row.findViewById(R.id.img)).setImageDrawable(FileUtils.getDrawableFromAsset(getActivity(), "items/" + item.getDotaId() + ".png"));
                ((TextView) row.findViewById(R.id.name)).setText(item.getDname());
                ((TextView) row.findViewById(R.id.cost)).setText(String.valueOf(item.getCost()));
                final long id = item.getId();
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ItemInfoActivity.class);
                        intent.putExtra("id", id);
                        startActivityForResult(intent, 1);
                    }
                });
                earlyGameItems.addView(row);
            }
        }

        FlowLayout coreItems = (FlowLayout) fragmentView.findViewById(R.id.core_items);
        coreItems.removeAllViews();
        for (String itemName : guideItems.getCoreItems()) {
            Item item = itemService.getItemByDotaId(activity, itemName);
            if (item == null) {
                Log.d(HeroDefaultItemBuild.class.getName(), "error loading item: " + itemName);
            } else {
                LinearLayout row = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_recept_row, null);
                ((ImageView) row.findViewById(R.id.img)).setImageDrawable(FileUtils.getDrawableFromAsset(getActivity(), "items/" + item.getDotaId() + ".png"));
                ((TextView) row.findViewById(R.id.name)).setText(item.getDname());
                ((TextView) row.findViewById(R.id.cost)).setText(String.valueOf(item.getCost()));
                final long id = item.getId();
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ItemInfoActivity.class);
                        intent.putExtra("id", id);
                        startActivityForResult(intent, 1);
                    }
                });
                coreItems.addView(row);
            }
        }
        FlowLayout luxuryItems = (FlowLayout) fragmentView.findViewById(R.id.luxury_items);
        luxuryItems.removeAllViews();
        for (String itemName : guideItems.getLuxury()) {
            Item item = itemService.getItemByDotaId(activity, itemName);
            if (item == null) {
                Log.d(HeroDefaultItemBuild.class.getName(), "error loading item: " + itemName);
            } else {
                LinearLayout row = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_recept_row, null);
                ((ImageView) row.findViewById(R.id.img)).setImageDrawable(FileUtils.getDrawableFromAsset(getActivity(), "items/" + item.getDotaId() + ".png"));
                ((TextView) row.findViewById(R.id.name)).setText(item.getDname());
                ((TextView) row.findViewById(R.id.cost)).setText(String.valueOf(item.getCost()));
                final long id = item.getId();
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ItemInfoActivity.class);
                        intent.putExtra("id", id);
                        startActivityForResult(intent, 1);
                    }
                });
                luxuryItems.addView(row);
            }
        }
    }
}
