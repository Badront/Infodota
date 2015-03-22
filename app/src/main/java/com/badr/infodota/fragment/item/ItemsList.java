package com.badr.infodota.fragment.item;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.AboutActivity;
import com.badr.infodota.activity.ItemInfoActivity;
import com.badr.infodota.activity.ListHolderActivity;
import com.badr.infodota.adapter.ItemsAdapter;
import com.badr.infodota.adapter.OnItemClickListener;
import com.badr.infodota.api.items.Item;
import com.badr.infodota.fragment.SearchableFragment;
import com.badr.infodota.service.item.ItemService;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;
import com.badr.infodota.util.ResourceUtils;
import com.badr.infodota.util.UpdateUtils;
import com.badr.infodota.util.retrofit.LocalSpiceService;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;
import java.util.Locale;

/**
 * User: ABadretdinov
 * Date: 17.01.14
 * Time: 18:28
 */
public class ItemsList extends Fragment implements SearchableFragment, OnItemClickListener,RequestListener<Item.List> {
    private SpiceManager spiceManager=new SpiceManager(LocalSpiceService.class);
    private RecyclerView gridView;
    private ItemsAdapter mAdapter;
    private String search = null;
    private String selectedFilter = null;
    private Filter filter;

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ActionMenuView actionMenuView = ((ListHolderActivity) getActivity()).getActionMenuView();
        Menu actionMenu = actionMenuView.getMenu();
        actionMenu.clear();
        actionMenuView.setVisibility(View.VISIBLE);
        inflater.inflate(R.menu.main, actionMenu);
        actionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                PopupMenu popup = new PopupMenu(getActivity(), getActivity().findViewById(item.getItemId()));
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
            case R.id.about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                return true;
            case R.id.locale:
                showLanguageDialog();
                return true;
            case R.id.new_version:
                UpdateUtils.checkNewVersion(getActivity(), true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLanguageDialog() {
        final Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.change_language));
        final String[] localeCodes = new String[]{"en-us", "ru"};
        String[] locales = new String[]{"English", "Русский"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, locales);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences localPrefs = activity.getSharedPreferences("locale", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = localPrefs.edit();
                String localeSelected = localeCodes[which];
                editor.putString("current", localeSelected);
                editor.commit();
                Locale locale = new Locale(localeSelected);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                activity.getApplicationContext().getResources().updateConfiguration(config, null);
                restartActivity(activity);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void restartActivity(Activity activity) {
        Intent intent = activity.getIntent();
        activity.finish();
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        View root=getView();
        if(root!=null) {
            gridView = (RecyclerView) root.findViewById(R.id.gridView);
            gridView.setHasFixedSize(true);
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
            //layoutManager.setReverseLayout(true);
            gridView.setLayoutManager(layoutManager);
            setColumnSize();
            loadItems();
        }
    }

    private void loadItems() {
        spiceManager.execute(new ItemsLoadRequest(),this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setColumnSize();
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

    @Override
    public void onTextSearching(String text) {
        search = text;
        if (filter != null) {
            filter.filter(search);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), ItemInfoActivity.class);
        Item item = mAdapter.getItem(position);
        if (item != null) {
            intent.putExtra("id", item.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {

    }

    @Override
    public void onRequestSuccess(Item.List items) {
        mAdapter = new ItemsAdapter(items);
        filter = mAdapter.getFilter();
        filter.filter(search);
        gridView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(ItemsList.this);
    }
    public class ItemsLoadRequest extends TaskRequest<Item.List>{

        public ItemsLoadRequest() {
            super(Item.List.class);
        }

        @Override
        public Item.List loadData() throws Exception {
            Activity activity=getActivity();
            if(activity!=null){
                ItemService itemService = BeanContainer.getInstance().getItemService();
                return itemService.getItems(activity, selectedFilter);
            }
            return null;
        }
    }
}
