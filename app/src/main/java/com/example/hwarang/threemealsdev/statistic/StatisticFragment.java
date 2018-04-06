package com.example.hwarang.threemealsdev.statistic;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwarang.threemealsdev.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticFragment extends Fragment {


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

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("단백질");
        labels.add("탄수화물");
        labels.add("지방");
        labels.add("비타민A");
        labels.add("비타민B");
        labels.add("비타민C");



        return view;
    }



}
