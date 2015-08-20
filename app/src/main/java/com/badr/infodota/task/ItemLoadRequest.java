package com.badr.infodota.task;

import android.content.Context;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.items.Item;
import com.badr.infodota.service.item.ItemService;
import com.badr.infodota.util.retrofit.TaskRequest;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 15:32
 */
public class ItemLoadRequest extends TaskRequest<Item.List> {
    private String mFilter;
    private Context mContext;

    public ItemLoadRequest(Context context, String filter) {
        super(Item.List.class);
        this.mContext = context;
        this.mFilter = filter;
    }

    @Override
    public Item.List loadData() throws Exception {
        ItemService itemService = BeanContainer.getInstance().getItemService();
        return itemService.getItems(mContext, mFilter);
    }
}
