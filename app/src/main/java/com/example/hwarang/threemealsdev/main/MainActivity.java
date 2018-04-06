package com.example.hwarang.threemealsdev.main;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.hwarang.threemealsdev.R;
import com.example.hwarang.threemealsdev.chatbot.ChatbotFragment;
import com.example.hwarang.threemealsdev.home.HomeFragment;
import com.example.hwarang.threemealsdev.statistic.StatisticFragment;
import com.github.mikephil.charting.charts.RadarChart;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_bottomnavigation)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        RadarChart chart = (RadarChart) findViewById(R.id.chart);
        chart.setBackgroundColor(Color.rgb(60, 65, 82));
        chart.getDescription().setEnabled(false);
        chart.setWebLineWidth(1f);
        chart.setWebColor(Color.LTGRAY);
        chart.setWebLineWidthInner(1f);
        chart.setWebColorInner(Color.LTGRAY);
        chart.setWebAlpha(100);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment seletedFragment = null;
                        switch(item.getItemId()){
                            case R.id.main_bottomnavigation_home:
                                seletedFragment = HomeFragment.getInstance();
                                break;
                            case R.id.main_bottomnavigation_chatbot:
                                seletedFragment = ChatbotFragment.getInstance();
                                break;
                            case R.id.main_bottomnavigation_statistic:
                                seletedFragment = StatisticFragment.getInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_fragment_container, seletedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, HomeFragment.getInstance());
        transaction.commit();
    }
}
