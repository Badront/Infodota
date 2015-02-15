package com.badr.infodota.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.adapter.ItemsAdapter;
import com.badr.infodota.adapter.OnItemClickListener;
import com.badr.infodota.api.items.Item;
import com.badr.infodota.service.item.ItemService;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;
import com.badr.infodota.util.ResourceUtils;

import java.util.List;

/**
 * User: Histler
 * Date: 17.02.14
 */
public class ItemSelectActivity extends BaseActivity implements SearchView.OnQueryTextListener, OnItemClickListener {
    private RecyclerView gridView;
    private ItemsAdapter mAdapter;
    private String search = null;
    private String selectedFilter = null;
    private Filter filter;

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.filter) {
            PopupMenu popup = new PopupMenu(this, findViewById(item.getItemId()));
            final Menu menu = popup.getMenu();
            String[] itemTypes = getResources().getStringArray(R.array.item_types);
            for (int i = 0; i < itemTypes.length; i++) {
                menu.add(2, i, 0, itemTypes[i]);
            }
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == 0) {
                        item.setTitle(R.string.filter);
                        selectedFilter = null;
                    } else {
                        item.setTitle(menuItem.getTitle());
                        selectedFilter = ResourceUtils.getItemType(menuItem.getItemId());
                    }
                    loadItems();

                    return true;
                }
            });
            popup.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.item_select, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(android.R.string.search_go));
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_item_list);
        gridView = (RecyclerView) findViewById(R.id.gridView);
        gridView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        //layoutManager.setReverseLayout(true);
        gridView.setLayoutManager(layoutManager);
        setColumnSize();
        loadItems();
    }

    private void setColumnSize() {
        if (getResources().getBoolean(R.bool.is_tablet)) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (gridView != null) {
                    ((GridLayoutManager) gridView.getLayoutManager()).setSpanCount(8);
                }
            } else {
                if (gridView != null) {
                    ((GridLayoutManager) gridView.getLayoutManager()).setSpanCount(6);
                }
            }
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (gridView != null) {
                    ((GridLayoutManager) gridView.getLayoutManager()).setSpanCount(6);
                }
            } else {
                if (gridView != null) {
                    ((GridLayoutManager) gridView.getLayoutManager()).setSpanCount(4);
                }
            }
        }
    }

    private void loadItems() {
        new LoaderProgressTask<List<Item>>(new ProgressTask<List<Item>>() {
            @Override
            public List<Item> doTask(OnPublishProgressListener listener) throws Exception {
                ItemService itemService = BeanContainer.getInstance().getItemService();
                return itemService.getItems(ItemSelectActivity.this, selectedFilter);
            }

            @Override
            public void doAfterTask(List<Item> result) {
                mAdapter = new ItemsAdapter(result);
                filter = mAdapter.getFilter();
                filter.filter(search);
                gridView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(ItemSelectActivity.this);
            }

            @Override
            public void handleError(String error) {

            }

            @Override
            public String getName() {
                return null;
            }
        }, null).execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setColumnSize();
    }

    @Override
    public boolean onQueryTextSubmit(String text) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String text) {
        search = text;
        if (filter != null) {
            filter.filter(search);
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent resultIntent = new Intent();
        Item item = mAdapter.getItem(position);
        if (item != null) {
            resultIntent.putExtra("id", item.getId());
            setResult(RESULT_OK, resultIntent);
            finish();
        }

    }
}
