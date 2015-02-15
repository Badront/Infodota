package com.badr.infodota.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.badr.infodota.R;
import com.badr.infodota.api.cosmetics.store.CosmeticItem;
import com.badr.infodota.util.ResourceUtils;
import com.badr.infodota.view.FlowLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * User: ABadretdinov
 * Date: 01.04.14
 * Time: 14:21
 */
public class CosmeticItemInfoActivity extends BaseActivity {
    protected ImageLoader imageLoader;
    DisplayImageOptions options;
    private CosmeticItem item;
    private CosmeticItem set;
    private ArrayList<CosmeticItem> setItems;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cosmetic_item_info);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.emptyitembg)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("set")) {
            set = (CosmeticItem) bundle.getSerializable("set");
        }
        if (bundle.containsKey("setItems")) {
            setItems = (ArrayList<CosmeticItem>) bundle.getSerializable("setItems");
        }
        if (bundle.containsKey("item")) {
            item = (CosmeticItem) bundle.getSerializable("item");
            initItem();
        }
    }

    private void initItem() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(item.getName());
        actionBar.setDisplayHomeAsUpEnabled(true);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(item.getName());
        title.setTextColor(getResources().getColor(ResourceUtils.COSMETIC_ITEM_QUALITY_IDS[item.getItem_quality()]));

        imageLoader.displayImage(item.getImage_url_large(), (ImageView) findViewById(R.id.image), options);

        TextView type = (TextView) findViewById(R.id.type);
        type.setText(item.getItem_type_name());

        TextView description = (TextView) findViewById(R.id.description);
        if (!TextUtils.isEmpty(item.getItem_description())) {
            description.setVisibility(View.VISIBLE);
            description.setText(Html.fromHtml(item.getItem_description()));
        } else {
            description.setVisibility(View.GONE);
        }
        TextView prices = (TextView) findViewById(R.id.prices);
        if (item.getPrices() != null && item.getPrices().size() > 0) {
            prices.setVisibility(View.VISIBLE);
            Map<String, Long> pricesMap = item.getPrices();
            Set<String> keySet = pricesMap.keySet();
            StringBuilder builder = new StringBuilder("\n");
            int index = 1;
            int size = keySet.size();
            Iterator<String> iterator = keySet.iterator();
            for (int i = 0; iterator.hasNext(); i++) {
                String key = iterator.next();
                float realPrice = pricesMap.get(key).floatValue() / 100;
                builder.append(String.valueOf(realPrice));
                builder.append(symbolForKey(key));
                if (index % 4 == 0) {
                    builder.append("\n");
                    index = 0;
                } else if (i != size - 1) {
                    builder.append(" / ");
                }
                index++;
            }
            prices.setText(builder.toString());

        } else {
            prices.setVisibility(View.GONE);
        }

        if ("bundle".equals(item.getItem_class())) {
            setSetsItemsInfo();
        } else {
            setSetInfo();
        }
    }

    private void setSetsItemsInfo() {
        FlowLayout flowLayout = (FlowLayout) findViewById(R.id.flow_set);
        findViewById(R.id.set).setVisibility(View.GONE);
        findViewById(R.id.set_title).setVisibility(View.GONE);
        if (setItems != null && setItems.size() > 0) {
            flowLayout.setVisibility(View.VISIBLE);
            flowLayout.removeAllViews();
            LayoutInflater inflater = getLayoutInflater();
            for (final CosmeticItem cosmeticItem : setItems) {
                View view = inflater.inflate(R.layout.cosmetic_item_row, null, false);
                imageLoader.displayImage(cosmeticItem.getImage_url(), (ImageView) view.findViewById(R.id.img), options);
                TextView name = (TextView) view.findViewById(R.id.name);
                name.setText(cosmeticItem.getName());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recreateForItem(cosmeticItem);

                    }
                });
                flowLayout.addView(view);
            }
        } else {
            flowLayout.setVisibility(View.GONE);
        }

    }

    private void recreateForItem(CosmeticItem cosmeticItem) {
        Intent intent = new Intent(CosmeticItemInfoActivity.this, CosmeticItemInfoActivity.class);
        intent.putExtra("item", cosmeticItem);
        intent.putExtra("set", set);
        intent.putExtra("setItems", setItems);
        startActivityForResult(intent, 1);
    }

    private void setSetInfo() {
        View setLayout = findViewById(R.id.set);
        View setTitle = findViewById(R.id.set_title);
        if (set == null) {
            setLayout.setVisibility(View.GONE);
            setTitle.setVisibility(View.GONE);
        } else {
            findViewById(R.id.flow_set).setVisibility(View.GONE);
            setLayout.setVisibility(View.VISIBLE);
            setTitle.setVisibility(View.VISIBLE);
            imageLoader.displayImage(set.getImage_url(), (ImageView) setLayout.findViewById(R.id.set_img), options);
            ((TextView) setLayout.findViewById(R.id.set_name)).setText(set.getName());
            setLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreateForItem(set);
                }
            });
        }
    }

    private String symbolForKey(String key) {
        if ("USD".equals(key)) {
            return "$";
        }
        if ("GBP".equals(key)) {
            return "£";
        }
        if ("EUR".equals(key)) {
            return "€";
        }
        if ("RUB".equals(key)) {
            return "руб.";
        }
        return key;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
