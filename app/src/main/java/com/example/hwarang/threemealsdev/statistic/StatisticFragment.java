package com.example.hwarang.threemealsdev.statistic;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.hwarang.threemealsdev.R;
import com.example.hwarang.threemealsdev.chatbot.DietModel;
import com.example.hwarang.threemealsdev.chatbot.infoModel;
import com.example.hwarang.threemealsdev.main.UserData;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticFragment extends ListFragment {
    RadarChart radarChart;
    //ListView listView;
    ListViewAdapter adapter;
    public FirebaseDatabase mDatabase;
    public DatabaseReference mReference;
    //private ChildEventListener mChild;
    public SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
    public long mNow;
    public Date mDate;
    public int nYear;
    public int nMonth;
    public int nDay;
    public infoModel userDiet = new infoModel();
    public UserData recData = new UserData();
    public infoModel resultDiet = new infoModel();
    public ArrayList<infoModel> foodData = new ArrayList<infoModel>();

    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }


    public StatisticFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance(){
        StatisticFragment statisticFragment = new StatisticFragment();
        return statisticFragment;
    }

    private void refresh(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public void makeResult(){
        if(userDiet.carbo == 0)
            resultDiet.carbo = 0;
        else
            resultDiet.carbo = Math.round(userDiet.carbo / recData.userCarbo * 100);

        if(userDiet.protein == 0)
            resultDiet.protein = 0;
        else
            resultDiet.protein = Math.round(userDiet.protein / recData.userProtein * 100);

        if(userDiet.fat == 0)
            resultDiet.fat = 0;
        else
            resultDiet.fat = Math.round(userDiet.fat / recData.userFat * 100);

        if(userDiet.kcal == 0)
            resultDiet.kcal = 0;
        else
            resultDiet.kcal = Math.round(userDiet.kcal / recData.userCalorie * 100);

        if(userDiet.iron == 0)
            resultDiet.iron = 0;
        else
            resultDiet.iron = Math.round(userDiet.iron / recData.userIron * 100);

        if(userDiet.natrium == 0)
            resultDiet.natrium = 0;
        else
            resultDiet.natrium = Math.round(userDiet.natrium / recData.userNatrium * 100);

        if(userDiet.calcium == 0)
            resultDiet.calcium = 0;
        else
            resultDiet.calcium = Math.round(userDiet.calcium / recData.userCalcium * 100);

        if(userDiet.vitaminA == 0)
            resultDiet.vitaminA = 0;
        else
            resultDiet.vitaminA = Math.round(userDiet.vitaminA / recData.userVitaminA * 100);

        if(userDiet.vitaminB == 0)
            resultDiet.vitaminB = 0;
        else
            resultDiet.vitaminB = Math.round(userDiet.vitaminB / recData.userVitaminB * 100);

        if(userDiet.vitaminC == 0)
            resultDiet.vitaminC = 0;
        else
            resultDiet.vitaminC = Math.round(userDiet.vitaminC / recData.userVitaminC * 100);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        ButterKnife.bind(this, view);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//현재 유저
        String userid = user.getUid();//유저 아이디 획득
        Calendar calendar = new GregorianCalendar(Locale.KOREA);//날짜 획득
        nMonth = calendar.get(Calendar.MONTH) + 1;
        nDay = calendar.get(Calendar.DAY_OF_MONTH);

        mDatabase = FirebaseDatabase.getInstance();

        FirebaseDatabase.getInstance().getReference().child("users").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recData = dataSnapshot.getValue(UserData.class);
                recData.userCalorie *= nDay;
                recData.userCarbo *= nDay;
                recData.userProtein *= nDay;
                recData.userFat *= nDay;
                recData.userCalcium *= nDay;
                recData.userIron *= nDay;
                recData.userNatrium *= nDay;
                recData.userVitaminA *= nDay;
                recData.userVitaminB *= nDay;
                recData.userVitaminC *= nDay;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        for(int i = 1; i <= nDay; i++){
            String timestamp = getTime().substring(0,8);
            String temp;
            if(i<10)
                temp = "0"+i;
            else
                temp = ""+i;
            timestamp += temp;


            FirebaseDatabase.getInstance().getReference().child(userid).child("Diet").child(timestamp).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    foodData.clear();
                    for( DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                        DietModel diet = snapshot.getValue(DietModel.class);
                        userDiet.carbo += diet.carbo;
                        userDiet.protein += diet.protein;
                        userDiet.fat += diet.fat;
                        userDiet.calcium+= diet.calcium;
                        userDiet.iron += diet.iron;
                        userDiet.natrium += diet.natrium;
                        userDiet.kcal += diet.kcal;
                        userDiet.vitaminA += diet.vitaminA;
                        userDiet.vitaminB += diet.vitaminB;
                        userDiet.vitaminC += diet.vitaminC;

                        if(diet.query.equals("welcome"))
                            continue;
                        infoModel im = new infoModel();
                        im.carbo = diet.carbo;
                        im.protein = diet.protein;
                        im.fat = diet.fat;
                        im.calcium = diet.calcium;
                        im.iron = diet.iron;
                        im.natrium = diet.natrium;
                        im.kcal = diet.kcal;
                        im.vitaminA = diet.vitaminA;
                        im.vitaminB = diet.vitaminB;
                        Log.d("jh","1번 지점 푸드네임 "+im.carbo+"\n");
                        im.vitaminC = diet.vitaminC;
                        im.foodname = diet.food;
                        Log.d("jh","1번 지점 푸드네임 "+im.foodname+"\n");

                        adapter.setInfoModels(im);
                        Log.d("jh","1번 지점 푸드네임 "+im.foodname+"\n");
                    }

                    makeResult();

                    adapter.setData((int)resultDiet.kcal,0);
                    adapter.setData((int)resultDiet.carbo,1);
                    adapter.setData((int)resultDiet.protein,2);
                    adapter.setData((int)resultDiet.fat,3);
                    adapter.setData((int)resultDiet.calcium,4);
                    adapter.setData((int)resultDiet.vitaminA,5);
                    adapter.setData((int)resultDiet.vitaminB,6);
                    adapter.setData((int)resultDiet.vitaminC,7);
                    adapter.setData((int)resultDiet.iron,8);
                    adapter.setData((int)resultDiet.natrium,9);
                    adapter.notifyDataSetChanged();
                    setData();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {


                }

            });
        }

        radarChart = (RadarChart)view.findViewById(R.id.chart);
        //radarChart.setBackgroundColor(Color.rgb(255, 255, 255));
        radarChart.setBackgroundResource(R.drawable.edge_draw);
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
        xAxis.setTextSize(14f);
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
        yAxis.setAxisMaximum(24f);
        yAxis.setDrawLabels(false);

        Legend lgd = radarChart.getLegend();
        lgd.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        lgd.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        lgd.setDrawInside(true);
        lgd.setXEntrySpace(12f);
        lgd.setYEntrySpace(5f);
        lgd.setXOffset(-18f);
        lgd.setTextColor(Color.rgb(50,100,100));
        lgd.setTextSize(12f);
        radarChart.setRotationEnabled(false);



        adapter = new ListViewAdapter() ;
        setListAdapter(adapter) ;
        adapter.addItem((int)resultDiet.kcal,"칼로리", ContextCompat.getDrawable(getActivity(), R.drawable.ic_morebutton));
        adapter.addItem((int)resultDiet.carbo,"탄수화물", ContextCompat.getDrawable(getActivity(), R.drawable.ic_morebutton));
        adapter.addItem((int)resultDiet.protein,"단백질", ContextCompat.getDrawable(getActivity(), R.drawable.ic_morebutton));
        adapter.addItem((int)resultDiet.fat,"지방", ContextCompat.getDrawable(getActivity(), R.drawable.ic_morebutton));
        adapter.addItem((int)resultDiet.calcium,"칼슘", ContextCompat.getDrawable(getActivity(), R.drawable.ic_morebutton));
        adapter.addItem((int)resultDiet.vitaminA,"비타민A", ContextCompat.getDrawable(getActivity(), R.drawable.ic_morebutton));
        adapter.addItem((int)resultDiet.vitaminB,"비타민B", ContextCompat.getDrawable(getActivity(), R.drawable.ic_morebutton));
        adapter.addItem((int)resultDiet.vitaminC,"비타민C", ContextCompat.getDrawable(getActivity(), R.drawable.ic_morebutton));
        adapter.addItem((int)resultDiet.iron,"철분", ContextCompat.getDrawable(getActivity(), R.drawable.ic_morebutton));
        adapter.addItem((int)resultDiet.natrium,"나트륨", ContextCompat.getDrawable(getActivity(), R.drawable.ic_morebutton));


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

        /*for(int i = 0; i< cnt; i++){
            float val1 = (float) (Math.random() * mult) + min;
            entries1.add(new RadarEntry(val1));

            float val2 = (float) (Math.random() * mult) + min;
            entries2.add(new RadarEntry(val2));
            //entries1.add(new RadarEntry(result.get(i)*20));
        }*/
        entries1.add(new RadarEntry((float)resultDiet.protein));//단백질
        Log.d("jh", "차트 탄수화물 "+(float)resultDiet.protein);
        entries1.add(new RadarEntry((float)resultDiet.carbo));//탄수화물
        entries1.add(new RadarEntry((float)resultDiet.fat));//지방
        entries1.add(new RadarEntry((float)resultDiet.vitaminA));//비A
        entries1.add(new RadarEntry((float)resultDiet.vitaminB));//비B2
        entries1.add(new RadarEntry((float)resultDiet.iron));//철분

        entries2.add(new RadarEntry(100f));
        entries2.add(new RadarEntry(100f));
        entries2.add(new RadarEntry(100f));
        entries2.add(new RadarEntry(100f));
        entries2.add(new RadarEntry(100f));
        entries2.add(new RadarEntry(100f));



        RadarDataSet set1 = new RadarDataSet(entries1,"섭취량");
        set1.setColor(Color.rgb(50, 151, 151));
        set1.setFillColor(Color.rgb(120, 183, 183));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        RadarDataSet set2 = new RadarDataSet(entries2, "권장량");
        set2.setColor(Color.rgb(248,182,82));
        set2.setFillColor(Color.rgb(255,255,255));
        set2.setDrawFilled(false);
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
