package com.badr.infodota.item.adapter.viewholder;

import android.view.View;

import com.badr.infodota.base.adapter.OnItemClickListener;
import com.badr.infodota.base.adapter.holder.BaseViewHolder;
import com.badr.infodota.item.view.ItemRowView;

/**
 * Created by ABadretdinov
 * 17.03.2016
 * 15:52
 */
public class ItemViewHolder extends BaseViewHolder {
    public ItemRowView itemRowView;

    public ItemViewHolder(View itemView) {
        super(itemView);
    }

    public ItemViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView, listener);
    }

    @Override
    protected void initView(View itemView) {
        itemRowView = (ItemRowView) itemView;
    }
}
