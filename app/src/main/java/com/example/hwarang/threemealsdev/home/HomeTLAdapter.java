package com.example.hwarang.threemealsdev.home;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hwarang.threemealsdev.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeTLAdapter extends RecyclerView.Adapter<HomeTLAdapter.MyViewHolder>{

    private LinearLayout container;
    private Context mContext;
    public String sDate;
    ArrayList<WordItemData> data = new ArrayList<WordItemData>();
    List<Integer> colors = new ArrayList<>();
    final int[] MY_COLORS = {Color.rgb(249,167,107), Color.rgb(124,204,179), Color.rgb(218,210,103)};

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public PieChart pieChart;
        public TextView txtDate;
        public TextView txtFoodName;

        public MyViewHolder(View view) {
            super(view);
            pieChart = (PieChart) view.findViewById(R.id.pieChart);
            txtDate = (TextView)view.findViewById(R.id.txt_date);
            txtFoodName = (TextView)view.findViewById(R.id.txt_foodName);


            //텍스트뷰 태그 동적할당

            /*
            container = (LinearLayout)view.findViewById(R.id.dynamicArea);

            for(){
                //TextView 생성
                TextView view1 = new TextView(this);
                view1.setText("나는 텍스트뷰");
                view1.setTextSize(FONT_SIZE);
                view1.setTextColor(Color.BLACK);

                //layout_width, layout_height, gravity 설정
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;
                view1.setLayoutParams(lp);

                //부모 뷰에 추가
                container.addView(view1);

            }*/
        }
    }

    public HomeTLAdapter(Context mContext, ArrayList<WordItemData> data) {
        this.mContext = mContext;
        this.data = data;
        for(int c: MY_COLORS) colors.add(c);
    }

    @Override
    public HomeTLAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {




        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        if(data.isEmpty()) {
            holder.txtDate.setText("");
            holder.txtFoodName.setText("");
            yValues.add(new PieEntry(0,"탄수화물"));
            yValues.add(new PieEntry(0,"단백질"));
            yValues.add(new PieEntry(0,"지방"));

        }
        else{
            holder.txtDate.setText(data.get(position).txdate+" " + data.get(position).typeOfMills);
            //holder.txtFoodName.setText(data.get(position).txfood);

            double inputCar = data.get(position).car/(data.get(position).car+data.get(position).prt+data.get(position).fat);
            double inputprt = data.get(position).prt/(data.get(position).car+data.get(position).prt+data.get(position).fat);
            double inputfat = data.get(position).fat/(data.get(position).car+data.get(position).prt+data.get(position).fat);

            yValues.add(new PieEntry((float)inputCar,"탄수화물"));
            yValues.add(new PieEntry((float)inputprt,"단백질"));
            yValues.add(new PieEntry((float)inputfat,"지방"));

        }


        PieDataSet pieDataSet = new PieDataSet(yValues, "");

        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(15);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        holder.pieChart.setData(pieData);

        holder.pieChart.setDrawHoleEnabled(true);
        holder.pieChart.setHoleColor(Color.WHITE);
        holder.pieChart.setTransparentCircleRadius(61f);

        holder.pieChart.setUsePercentValues(true);
        holder.pieChart.getDescription().setEnabled(false);
        holder.pieChart.setRotationEnabled(false);

        //holder.pieChart.setCenterText("");
        //holder.pieChart.setCenterTextSize(12);
        //holder.pieChart.setBackgroundResource(R.drawable.edge_draw);
        holder.pieChart.setDrawEntryLabels(false);
        /*
        Description description = new Description();
        description.setText("Description number " + (position+1));
        holder.pieChart.setDescription(description);
        */
        holder.pieChart.notifyDataSetChanged();
        holder.pieChart.invalidate();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

