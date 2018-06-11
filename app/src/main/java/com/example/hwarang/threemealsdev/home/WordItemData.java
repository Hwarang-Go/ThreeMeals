package com.example.hwarang.threemealsdev.home;

import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;

public class WordItemData{
    public String txdate;
    public double car;
    public double prt;
    public double fat;
    public String txfood;

    // 화면에 표시될 문자열 초기화
    public WordItemData(String txdate,double car, double prt, double fat,String txfood) {
        this.txdate = txdate;
        this.car = car;
        this.prt = prt;
        this.fat = fat;
        this.txfood = txfood;
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
