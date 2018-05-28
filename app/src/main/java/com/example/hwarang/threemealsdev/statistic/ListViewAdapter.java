package com.example.hwarang.threemealsdev.statistic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hwarang.threemealsdev.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<ListViewItem> data = new ArrayList<ListViewItem>() ;
    public ListViewAdapter(){
    }
    @Override
    public int getCount(){return data.size();}
    @Override
    public Object getItem(int position){return data.get(position);}
    @Override
    public long getItemId(int position){return position;}
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.statistic_item, parent, false);
        }


        TextView nutData=(TextView)convertView.findViewById(R.id.nutData);
        TextView nutName=(TextView)convertView.findViewById(R.id.nutName);
        ImageButton IB = (ImageButton)convertView.findViewById((R.id.imageButton));

        ListViewItem listviewitem=data.get(position);


        nutData.setText(listviewitem.getNutData()+"%");

        if(listviewitem.getNutData() <=25)
            nutData.setBackgroundColor(Color.rgb(179,45,0));
        else if(listviewitem.getNutData() <=50)
            nutData.setBackgroundColor(Color.rgb(248,182,81));
        else if(listviewitem.getNutData() <=70)
            nutData.setBackgroundColor(Color.rgb(3,210,168));
        else if(listviewitem.getNutData() <=100)
            nutData.setBackgroundColor(Color.rgb(3,182,140));
        else
            nutData.setBackgroundColor(Color.rgb(0,86,70));


        nutName.setText(listviewitem.getNutName());
        IB.setImageDrawable(listviewitem.getIcon());


        return convertView;
    }
    public void addItem(int nutData, String nutName, Drawable icon) {
        ListViewItem item = new ListViewItem(nutData,nutName,icon);
        data.add(item);
    }
}

