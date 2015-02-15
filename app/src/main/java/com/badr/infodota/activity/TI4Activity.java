package com.badr.infodota.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Pair;
import android.view.Window;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.adapter.pager.TI4PagerAdapter;
import com.badr.infodota.service.ti4.TI4Service;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;
import com.badr.infodota.view.SlidingTabLayout;

import java.text.MessageFormat;

/**
 * User: ABadretdinov
 * Date: 14.05.14
 * Time: 20:17
 */
public class TI4Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.ti4_holder);
        setSupportProgressBarIndeterminateVisibility(false);
        getSupportActionBar().setTitle("The International 2014");
        new LoaderProgressTask<Pair<Long, String>>(new ProgressTask<Pair<Long, String>>() {
            @Override
            public Pair<Long, String> doTask(OnPublishProgressListener listener) throws Exception {
                BeanContainer container = BeanContainer.getInstance();
                TI4Service service = container.getTi4Service();
                return service.getPrizePool(TI4Activity.this);
            }

            @Override
            public void doAfterTask(Pair<Long, String> result) {
                if (result.first != null) {
                    ActionBar actionBar = getSupportActionBar();
                    actionBar.setTitle(MessageFormat.format(getString(R.string.current_prizepool), String.valueOf(result.first)));
                } else {
                    handleError(result.second);
                }
            }

            @Override
            public void handleError(String error) {
                Toast.makeText(TI4Activity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public String getName() {
                return "TI4PrizePool";
            }
        }, null).execute();
        initPager();
    }

    private void initPager() {
        FragmentPagerAdapter adapter = new TI4PagerAdapter(getSupportFragmentManager(), this);
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(4);
        SlidingTabLayout indicator = (SlidingTabLayout) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }
}
