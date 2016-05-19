package com.badr.infodota.item.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.badr.infodota.base.util.Navigate;
import com.badr.infodota.item.fragment.ItemInfoFragment;

/**
 * Created by ABadretdinov
 * 17.03.2016
 * 15:17
 */
public class OnItemClickListener implements View.OnClickListener {
    private long mItemId;
    private Integer mRequestCode;

    public OnItemClickListener(long itemId) {
        this.mItemId = itemId;
    }

    public OnItemClickListener(long itemId, Integer requestCode) {
        this(itemId);
        mRequestCode = requestCode;
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        Bundle bundle = new Bundle();
        bundle.putLong(Navigate.PARAM_ID, mItemId);
        if (mRequestCode != null) {
            Navigate.toForEntityResult((Activity) context, ItemInfoFragment.class, bundle, mRequestCode);
        } else {
            Navigate.to(context, ItemInfoFragment.class, bundle, true);
        }
    }
}
