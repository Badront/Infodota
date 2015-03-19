package com.badr.infodota.activity;

import android.content.Context;
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
import com.badr.infodota.util.retrofit.LocalSpiceService;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.badr.infodota.view.SlidingTabLayout;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.text.MessageFormat;

/**
 * User: ABadretdinov
 * Date: 14.05.14
 * Time: 20:17
 */
@Deprecated
public class TI4Activity extends BaseActivity implements RequestListener<Long> {
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onStop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.ti4_holder);
        setSupportProgressBarIndeterminateVisibility(false);
        getSupportActionBar().setTitle("The International 2014");
        spiceManager.execute(new PrizePoolLoadRequest(getApplicationContext()),this);
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

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(this, spiceException.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestSuccess(Long result) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(MessageFormat.format(getString(R.string.current_prizepool), String.valueOf(result)));
    }
    public static class PrizePoolLoadRequest extends TaskRequest<Long>{
        private Context context;
        public PrizePoolLoadRequest(Context context) {
            super(Long.class);
            this.context=context;

        }

        @Override
        public Long loadData() throws Exception {
            BeanContainer container = BeanContainer.getInstance();
            TI4Service service = container.getTi4Service();
            return service.getPrizePool(context);
        }
    }
}
