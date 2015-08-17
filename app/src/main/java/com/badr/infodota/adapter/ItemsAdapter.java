package com.badr.infodota.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.badr.infodota.R;
import com.badr.infodota.adapter.holder.ItemHolder;
import com.badr.infodota.api.items.Item;
import com.badr.infodota.util.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Histler
 * Date: 18.01.14
 */
public class ItemsAdapter extends BaseRecyclerAdapter<Item, ItemHolder> implements Filterable {

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

        Context context = holder.name.getContext();
        Glide.with(context).load(Utils.getItemImage(item.getDotaId())).into(holder.image);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
