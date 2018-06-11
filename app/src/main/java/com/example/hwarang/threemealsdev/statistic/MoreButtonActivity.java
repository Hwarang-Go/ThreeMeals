package com.example.hwarang.threemealsdev.statistic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.hwarang.threemealsdev.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MoreButtonActivity extends Activity{
    private TextView txtText;
    private TextView txtText2;
    private ListView listView;
    public ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_more_btn);

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.more_title);
        txtText2 = (TextView)findViewById(R.id.txtText);
        listView = (ListView)findViewById(R.id.more_list);

        //데이터 가져오기
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        ArrayList<MoreInfoBox> mb = (ArrayList<MoreInfoBox>)intent.getSerializableExtra("data");
        HashMap<String,String> item;
        for(int i = 0; i < mb.size();i++){
            item = new HashMap<String,String>();
            item.put("foodName",mb.get(i).foodName);
            item.put("foodData",""+mb.get(i).foodData);
            list.add(item);

        }
        txtText.setText(title);

        SimpleAdapter adapter = new SimpleAdapter(this,list,android.R.layout.simple_list_item_2,
                new String[]{"foodName","foodData"},
                new int[]{android.R.id.text1,android.R.id.text2});
        listView.setAdapter(adapter);
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        /*
        Intent intent = new Intent();
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
