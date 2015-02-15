package com.badr.infodota.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.badr.infodota.R;
import com.badr.infodota.adapter.holder.ItemHolder;
import com.badr.infodota.api.items.Item;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Histler
 * Date: 18.01.14
 */
public class ItemsAdapter extends BaseRecyclerAdapter<Item, ItemHolder> implements Filterable {

    protected ImageLoader imageLoader;
    DisplayImageOptions options;
    private List<Item> filtered;
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<Item> filteredEntities = new ArrayList<Item>();
            String lowerConstr = constraint != null ? constraint.toString().toLowerCase() : "";
            filterResults.count = filteredEntities.size();
            for (Item entity : mData) {
                if (entity.getDname().toLowerCase().contains(lowerConstr)
                        || entity.getDotaId().toLowerCase().contains(lowerConstr)) {
                    filteredEntities.add(entity);
                }
            }
            filterResults.values = filteredEntities;
            return filterResults;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered = (List<Item>) results.values;
            if (filtered == null) {
                filtered = new ArrayList<Item>();
            }
            notifyDataSetChanged();
        }
    };

    public ItemsAdapter(List<Item> items) {
        super(items);
        this.filtered = mData;
        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.emptyitembg)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    @Override
    public Item getItem(int position) {
        return filtered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return filtered.get(position).getId();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ItemHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        Item item = getItem(position);
        holder.name.setText(item.getDname());

        imageLoader.displayImage("assets://items/" + item.getDotaId() + ".png", holder.image, options);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
