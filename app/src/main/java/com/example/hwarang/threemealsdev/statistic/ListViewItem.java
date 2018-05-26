package com.example.hwarang.threemealsdev.statistic;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    //private int icon;
    private int nutData;
    private String nutName;
    private Drawable icon;

    public int getNutData(){return nutData;}
    public String getNutName(){return nutName;}
    public Drawable getIcon(){return icon;}
    public ListViewItem(int data, String name,Drawable icon){
        this.nutData=data;
        this.nutName=name;
        this.icon = icon;
    }
}
