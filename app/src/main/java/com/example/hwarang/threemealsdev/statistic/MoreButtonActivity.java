package com.example.hwarang.threemealsdev.statistic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.hwarang.threemealsdev.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class MoreButtonActivity extends Activity{
    private TextView txtText;
    private ListView listView;
    ArrayList<MoreInfoBox> mb;
    public ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_more_btn);

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.more_title);
        listView = (ListView)findViewById(R.id.more_list);

        //데이터 가져오기
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        mb = (ArrayList<MoreInfoBox>)intent.getSerializableExtra("data");
        Log.d("jh","mb어레이 데이타 이름 "+mb.get(0).foodName);
        HashMap<String,String> item;
        for(int i = 0; i < mb.size();i++){
            item = new HashMap<String,String>();
            item.put("foodName",mb.get(i).foodName);
            switch (title){

                case "칼로리":
                    item.put("foodData",mb.get(i).foodData + " Kcal");
                    break;

                case "탄수화물":
                case "단백질" :
                case "지방" :
                    item.put("foodData",mb.get(i).foodData + " g");
                    break;

                case "비타민B" :
                case "비타민C" :
                case "칼슘" :
                case "나트륨" :
                case "철분" :
                    item.put("foodData",mb.get(i).foodData + " mg");
                    break;
                case "비타민A" :
                    item.put("foodData",mb.get(i).foodData + " \u00B5g");

            }

            list.add(item);

        }
        txtText.setText(title);

        SimpleAdapter adapter = new SimpleAdapter(this,list,android.R.layout.simple_list_item_2,
                new String[]{"foodName","foodData"},
                new int[]{android.R.id.text1,android.R.id.text2}){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setTextColor(Color.rgb(0,0,0));
                text1.setTextSize(17f);

                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setTextColor(Color.rgb(0,0,0));
                return view;
            };
        };

        listView.setAdapter(adapter);
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        /*
        Intent intent = new Intent();
        mb.clear();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);
        */
        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
