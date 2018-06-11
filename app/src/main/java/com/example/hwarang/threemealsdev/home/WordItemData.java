package com.example.hwarang.threemealsdev.home;

import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;

public class WordItemData{
    public String txdate;
    public double car;
    public double prt;
    public double fat;
    public ArrayList<String> txfood=new ArrayList<String>();
    public String typeOfMills;

    // 화면에 표시될 문자열 초기화

    public WordItemData(){}

    public WordItemData(String txdate,double car, double prt, double fat,ArrayList<String> txfood) {
        this.txdate = txdate;
        this.car = car;
        this.prt = prt;
        this.fat = fat;
        this.txfood = txfood;
    }
    public void plusData(double car, double prt, double fat, String txt){
        this.car += car;
        this.prt += prt;
        this.fat += fat;
        this.txfood.add(txt);

    }
    public void resetData(){
        this.car =0;
        this.prt = 0;
        this.fat = 0;
        this.typeOfMills = null;
        this.txdate = null;
        this.txfood.clear();

    }

    // 입력받은 숫자의 리스트생성
    /*
    public static ArrayList<WordItemData> createContactsList(int numContacts) {
        ArrayList<WordItemData> contacts = new ArrayList<WordItemData>();

        for (int i = 1; i <= numContacts; i++) {
            contacts.add(new WordItemData("18년 7월 31일",));
        }

        return contacts;
    }*/


}
