package com.badr.infodota.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.badr.infodota.R;
import com.badr.infodota.api.cosmetics.store.CosmeticItem;
import com.badr.infodota.util.ResourceUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 31.03.14
 * Time: 19:02
 */
public class CosmeticItemsAdapter extends BaseAdapter implements Filterable {
    protected ImageLoader imageLoader;
    DisplayImageOptions options;
    private LayoutInflater mInflater;
    private List<CosmeticItem> items;
    private List<CosmeticItem> filtered;
    private String filterVaule = null;
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<CosmeticItem> filteredItems = new ArrayList<CosmeticItem>();
            String lowerConstr = constraint.toString().toLowerCase();
            for (CosmeticItem item : items) {
                if (TextUtils.isEmpty(filterVaule) || filterVaule.equals(item.getItem_class())) {
                    if (item.getItem_name().toLowerCase().contains(lowerConstr)) {
                        filteredItems.add(item);
                    }
                }
            }
            filterResults.count = filteredItems.size();
            filterResults.values = filteredItems;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered = (ArrayList<CosmeticItem>) results.values;
            if (filtered == null) {
                filtered = new ArrayList<CosmeticItem>();
            }
            if (results.count >= 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    public CosmeticItemsAdapter(Context context, List<CosmeticItem> items) {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.emptyitembg)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items != null ? items : new ArrayList<CosmeticItem>();
        this.filtered = items;
    }

    public void setFilterValue(String filter) {
        this.filterVaule = filter;
    }

    public List<CosmeticItem> getItems() {
        return items;
    }

    @Override
    public int getCount() {
        return filtered.size();
    }

    @Override
    public CosmeticItem getItem(int position) {
        return filtered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return filtered.get(position).getDefindex();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ItemHolder holder;
        if (convertView == null) {
            vi = mInflater.inflate(R.layout.cosmetic_item_row, parent, false);
            holder = new ItemHolder();
            holder.name = (TextView) vi.findViewById(R.id.name);
            holder.image = (ImageView) vi.findViewById(R.id.img);
            vi.setTag(holder);
        } else {
            holder = (ItemHolder) vi.getTag();
        }
        CosmeticItem item = getItem(position);
        holder.name.setText(item.getName());
        Resources resources = parent.getContext().getResources();
        holder.name.setTextColor(resources.getColor(ResourceUtils.COSMETIC_ITEM_QUALITY_IDS[item.getItem_quality()]));
        imageLoader.displayImage(item.getImage_url(), holder.image, options);
        return vi;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public static class ItemHolder {
        TextView name;
        ImageView image;
    }

}
