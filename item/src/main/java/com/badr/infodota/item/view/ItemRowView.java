package com.badr.infodota.item.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badr.infodota.item.R;
import com.badr.infodota.item.entity.Item;
import com.badr.infodota.item.util.ItemUtils;
import com.bumptech.glide.Glide;

/**
 * Created by ABadretdinov
 * 17.03.2016
 * 13:02
 */
public class ItemRowView extends LinearLayout {
    private Item mItem;

    private ImageView imageView;
    private TextView nameView;
    private TextView costView;

    public ItemRowView(Context context, Item item) {
        this(context);
        setItem(item, false);
    }

    public ItemRowView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ItemRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ItemRowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        inflate(context, R.layout.item_row_view, this);
        imageView = (ImageView) findViewById(R.id.image);
        nameView = (TextView) findViewById(R.id.name);
        costView = (TextView) findViewById(R.id.cost);
    }

    public void setItem(Item item, boolean showCost) {
        this.mItem = item;
        if (mItem != null) {
            Glide.with(getContext()).load(ItemUtils.getItemImage(mItem.getDotaId())).into(imageView);
            nameView.setText(mItem.getDotaName());
            if (showCost) {
                costView.setText(mItem.getCost());
                costView.setVisibility(VISIBLE);
            } else {
                costView.setVisibility(GONE);
            }
        }
    }

    public Item getItem() {
        return mItem;
    }
}
