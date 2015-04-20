package com.badr.infodota.fragment.trackdota.game;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.badr.infodota.R;
import com.badr.infodota.api.trackdota.core.CoreResult;
import com.badr.infodota.api.trackdota.live.LiveGame;
import com.badr.infodota.util.Refresher;
import com.badr.infodota.util.Updatable;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ABadretdinov
 * 18.04.2015
 * 11:31
 */
public class Graphs extends Fragment implements Updatable<Pair<CoreResult,LiveGame>> {
    private Refresher refresher;
    private CoreResult coreResult;
    private LiveGame liveGame;
    private Spinner mChartSpinner;
    private LineChart mChart;
    private LineData mLineData;
    private LineDataSet mLineDataSet;
    private List<Entry> mLineDataSetEntries;
    private ArrayList<String> mTicks;
    int mSelectedStat;

    public static Graphs newInstance(Refresher refresher,CoreResult coreResult,LiveGame liveGame){
        Graphs fragment=new Graphs();
        fragment.refresher=refresher;
        fragment.coreResult=coreResult;
        fragment.liveGame=liveGame;
        return fragment;
    }
    @Override
    public void onUpdate(Pair<CoreResult, LiveGame> entity) {
        this.coreResult=entity.first;
        this.liveGame=entity.second;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View root=getView();
        if(root!=null) {
            mChart = (LineChart) root.findViewById(R.id.chart);
            mChartSpinner= (Spinner) root.findViewById(R.id.chart_type);
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_spinner_item,
                    getResources().getStringArray(R.array.charts_array));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mChartSpinner.setAdapter(adapter);
            mChartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(mSelectedStat!=position){
                        mSelectedStat=position;
                        initChart();
                        if(coreResult.getStatus()>=2){
                            runOnTick(coreResult.getStatus());
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
    public void initChart(){
        mLineDataSetEntries=new ArrayList<>();
        mLineDataSet=new LineDataSet(mLineDataSetEntries,"");
        mLineDataSet.setLineWidth(2f);
        mLineDataSet.setCircleSize(0f);
        mLineDataSet.addEntry(new Entry(0f,0));
        mTicks=new ArrayList<>();
        mTicks.add("0");
        List list=new ArrayList();
        list.add(mLineDataSet);
        mLineData=new LineData(mTicks,list);
        LimitLine limitLine=new LimitLine(0f);
        limitLine.setLineColor(Color.WHITE);
        limitLine.setLineWidth(2f);
        mLineData.addLimitLine(limitLine);
        mChart.setData(mLineData);
        mChart.setSta
        /*chartsViewTask*/

    }
}
