package com.badr.infodota.item.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.badr.infodota.base.adapter.BaseRecyclerAdapter;
import com.badr.infodota.item.R;
import com.badr.infodota.item.adapter.viewholder.ItemViewHolder;
import com.badr.infodota.item.entity.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Histler
 * Date: 18.01.14
 */
public class ItemsAdapter extends BaseRecyclerAdapter<Item, ItemViewHolder> implements Filterable {

    private List<Item> mFiltered;
    private Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<Item> filteredEntities = new ArrayList<>();
            String lowerConstr = constraint != null ? constraint.toString().toLowerCase() : "";
            filterResults.count = filteredEntities.size();
            for (Item entity : mData) {
                if (entity.getDotaName().toLowerCase().contains(lowerConstr)
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
            mFiltered = (List<Item>) results.values;
            if (mFiltered == null) {
                mFiltered = new ArrayList<>();
            }
            notifyDataSetChanged();
        }
    };

    public ItemsAdapter(List<Item> items) {
        super(items);
        this.mFiltered = mData;
    }

    @Override
    public int getItemCount() {
        return mFiltered.size();
    }

    @Override
    public Item getItem(int position) {
        return mFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mFiltered.get(position).getId();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ItemViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = getItem(position);
        holder.itemRowView.setItem(item, false);
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}
