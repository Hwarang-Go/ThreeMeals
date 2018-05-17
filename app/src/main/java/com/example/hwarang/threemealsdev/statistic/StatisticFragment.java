package com.example.hwarang.threemealsdev.statistic;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.hwarang.threemealsdev.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticFragment extends Fragment {
    RadarChart radarChart;


    public StatisticFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance(){
        StatisticFragment statisticFragment = new StatisticFragment();
        return statisticFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        ButterKnife.bind(this, view);

        radarChart = (RadarChart)view.findViewById(R.id.chart);
        radarChart.setBackgroundColor(Color.rgb(255, 255, 255));
        radarChart.getDescription().setEnabled(false);
        radarChart.setWebLineWidth(1f);
        radarChart.setWebColor(Color.LTGRAY);
        radarChart.setWebLineWidthInner(1f);
        radarChart.setWebColorInner(Color.LTGRAY);
        radarChart.setWebAlpha(100);

        setData();

        radarChart.animateXY(
                1400,1400,
                Easing.EasingOption.EaseInOutQuad,
                Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mActivities = new String[]{"단백질", "탄수화물", "지방", "비타민A", "비타민B2", "철분"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int)value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.rgb(50,100,100));

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setLabelCount(6,false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);

        Legend lgd = radarChart.getLegend();
        lgd.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        lgd.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        lgd.setDrawInside(true);
        lgd.setXEntrySpace(7f);
        lgd.setYEntrySpace(5f);
        lgd.setTextColor(Color.rgb(50,100,100));
        radarChart.setRotationEnabled(false);

        return view;
    }

    public void setData() {

        float mult = 80;
        float min = 20;
        int cnt = 6;
        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();
        ArrayList<RadarEntry> entries2 = new ArrayList<RadarEntry>();
        /*ArrayList<Integer> result;
        Intent get_intent = getIntent();
        result = (ArrayList<Integer>) get_intent.getSerializableExtra("result");
        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();*/

        for(int i = 0; i< cnt; i++){
            float val1 = (float) (Math.random() * mult) + min;
            entries1.add(new RadarEntry(val1));

            float val2 = (float) (Math.random() * mult) + min;
            entries2.add(new RadarEntry(val2));
            //entries1.add(new RadarEntry(result.get(i)*20));
        }

        RadarDataSet set1 = new RadarDataSet(entries1,"권장량");
        set1.setColor(Color.rgb(248,182,82));
        set1.setFillColor(Color.rgb(255,255,255));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        RadarDataSet set2 = new RadarDataSet(entries2, "섭취량");
        set2.setColor(Color.rgb(50, 151, 151));
        set2.setFillColor(Color.rgb(120, 183, 183));
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData((sets));
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        radarChart.setData(data);
        radarChart.invalidate();

    }



}
