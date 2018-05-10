package com.example.hwarang.threemealsdev.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.hwarang.threemealsdev.R;
import com.example.hwarang.threemealsdev.chatbot.ChatbotFragment;
import com.example.hwarang.threemealsdev.home.HomeFragment;
import com.example.hwarang.threemealsdev.login.LoginActivity;
import com.example.hwarang.threemealsdev.statistic.StatisticFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sp; // 최초 실행 구분을 위함

    @BindView(R.id.main_bottomnavigation)
    BottomNavigationView bottomNavigationView;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(); // UserData 올릴 database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

        sp = getSharedPreferences("isFirst", MODE_PRIVATE);
        boolean first = sp.getBoolean("isFirst", false);
        Log.d("sp set", "set sp false");
        if(!first){
            Log.d("Is first Time?","yes first");

            //TODO 나중에 주석 해제해야 최초 실행 이후에는 팝업창이 뜨지 않음.
            /*SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirst", true);
            editor.apply();*/

            //TODO 최초 실행 시 나이, 키, 몸무게 입력하라는 팝업 띄울 위치
            startActivity(new Intent(this, FirstPopupActivity.class));
        }else{
            Log.d("Is first Time?","not first");
        }

    }


    public void onClickButton(View view){
        FirebaseAuth.getInstance().signOut();
        Log.d("signout ok?", "signout");
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }
}
