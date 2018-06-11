package com.example.hwarang.threemealsdev.statistic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.hwarang.threemealsdev.R;
import com.example.hwarang.threemealsdev.chatbot.infoModel;
import com.example.hwarang.threemealsdev.main.MainActivity;

import java.util.ArrayList;
import java.util.Comparator;

public class ListViewAdapter extends BaseAdapter {

    public ArrayList<MoreInfoBox> moreData = new ArrayList<MoreInfoBox>();
    public ArrayList<infoModel> infoModels = new ArrayList<infoModel>();
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
    public View getView(final int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.statistic_item, parent, false);
        }


        TextView nutData=(TextView)convertView.findViewById(R.id.nutData);
        TextView nutName=(TextView)convertView.findViewById(R.id.nutName);
        ImageButton IB = (ImageButton)convertView.findViewById((R.id.imageButton));

        IB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.imageButton:
                        setIntentData(position);
                        Intent intent = new Intent(context,MoreButtonActivity.class);
                        intent.putExtra("title",data.get(position).getNutName());
                        intent.putExtra("data",moreData);
                        context.startActivity(intent);
                        //context.startActivity(new Intent(context, MoreButtonActivity.class));
                        break;

                    default:
                        break;
                }

            }
        });

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

    public void setData(int nutData, int position) {
        ListViewItem item = this.data.get(position);
        item.setNutData(nutData);
        this.data.set(position,item);
    }
    public void setInfoModels(ArrayList<infoModel> iM){
        this.infoModels = iM;
    }



    public void setIntentData(int position){
        moreData.clear();
        for(int i = 0; i<infoModels.size();i++){
            if(position==0){
                if(infoModels.get(i).kcal>0){
                    MoreInfoBox temp = new MoreInfoBox();
                    temp.foodName = infoModels.get(i).foodname;
                    temp.foodData = infoModels.get(i).kcal;
                    moreData.add(temp);
                }
            }
            else if(position ==1){
                if(infoModels.get(i).carbo>0){
                    MoreInfoBox temp = new MoreInfoBox();
                    temp.foodName = infoModels.get(i).foodname;
                    temp.foodData = infoModels.get(i).carbo;
                    moreData.add(temp);
                }

            }
            else if(position ==2){
                if(infoModels.get(i).protein>0){
                    MoreInfoBox temp = new MoreInfoBox();
                    temp.foodName = infoModels.get(i).foodname;
                    temp.foodData = infoModels.get(i).protein;
                    moreData.add(temp);
                }
            }
            else if(position ==3){
                if(infoModels.get(i).fat>0){
                    MoreInfoBox temp = new MoreInfoBox();
                    temp.foodName = infoModels.get(i).foodname;
                    temp.foodData = infoModels.get(i).fat;
                    moreData.add(temp);
                }
            }
            else if(position ==4){
                if(infoModels.get(i).calcium>0){
                    MoreInfoBox temp = new MoreInfoBox();
                    temp.foodName = infoModels.get(i).foodname;
                    temp.foodData = infoModels.get(i).calcium;
                    moreData.add(temp);
                }
            }
            else if(position ==5){
                if(infoModels.get(i).vitaminA>0){
                    MoreInfoBox temp = new MoreInfoBox();
                    temp.foodName = infoModels.get(i).foodname;
                    temp.foodData = infoModels.get(i).vitaminA;
                    moreData.add(temp);
                }
            }
            else if(position ==6){
                if(infoModels.get(i).vitaminB>0){
                    MoreInfoBox temp = new MoreInfoBox();
                    temp.foodName = infoModels.get(i).foodname;
                    temp.foodData = infoModels.get(i).vitaminB;
                    moreData.add(temp);
                }
            }
            else if(position ==7){
                if(infoModels.get(i).vitaminC>0){
                    MoreInfoBox temp = new MoreInfoBox();
                    temp.foodName = infoModels.get(i).foodname;
                    temp.foodData = infoModels.get(i).vitaminC;
                    moreData.add(temp);
                }
            }
            else if(position ==8){
                if(infoModels.get(i).iron>0){
                    MoreInfoBox temp = new MoreInfoBox();
                    temp.foodName = infoModels.get(i).foodname;
                    temp.foodData = infoModels.get(i).iron;
                    moreData.add(temp);
                }
            }
            else if(position ==9){
                if(infoModels.get(i).natrium>0){
                    MoreInfoBox temp = new MoreInfoBox();
                    temp.foodName = infoModels.get(i).foodname;
                    temp.foodData = infoModels.get(i).natrium;
                    moreData.add(temp);
                }
            }


        }

    }
}

