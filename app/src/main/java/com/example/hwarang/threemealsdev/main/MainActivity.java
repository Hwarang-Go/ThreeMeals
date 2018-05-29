package com.example.hwarang.threemealsdev.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hwarang.threemealsdev.R;
import com.example.hwarang.threemealsdev.chatbot.ChatbotFragment;
import com.example.hwarang.threemealsdev.home.HomeFragment;
import com.example.hwarang.threemealsdev.login.LoginActivity;
import com.example.hwarang.threemealsdev.statistic.StatisticFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.github.mikephil.charting.charts.RadarChart;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sp; // 최초 실행 구분을 위함

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    public UserData userData;

    private DrawerLayout mDrawerLayout; // profile menu
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    // 뒤로 2번 눌러서 종료
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @BindView(R.id.main_bottomnavigation)
    BottomNavigationView bottomNavigationView;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(); // UserData 올릴 database


    public RadarChart radarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // toolbar 설정 start
        toolbar = (Toolbar)findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar); // toolbar와 actionbar를 같게 만듬
        toolbar.setBackgroundResource(R.color.mainColor);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // toolbar 설정 end


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        userData = new UserData();

        // 로그인 된 사용자의 정보 불러오기
        if(mFirebaseUser == null){
            // 로그인 되어 있지 않으면 로그인 액티비티로 보낸다.
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            userData.userName = mFirebaseUser.getDisplayName();
            userData.email = mFirebaseUser.getEmail();
            if(mFirebaseUser.getPhotoUrl() != null){
                userData.userPhoto = mFirebaseUser.getPhotoUrl().toString();
            }
            //TODO 로그인된 사용자의 정보를 객체에 넣어줄 부분
        }


        // DrawerLayout 부분 start
        mDrawerLayout = (DrawerLayout)findViewById(R.id.layout_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // DrawerLayout 부분 end


        // Navigation View start
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_profile);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                int id = item.getItemId();
                if(id == R.id.menu_nav_signout){
                    // signout 버튼 눌러서 로그아웃
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }
                //TODO update 버튼 누르면 user profile update 하는 액티비티 호출

                // 다하고 드로워 닫아주기
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        // Navigation View end


        // bottom Navigation view 부분 start

        //TODO 슬라이드로 넘기는 부분 구현해야함

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

        // bottom Navigation view 부분 start

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
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirst", true);
            editor.apply();

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

    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if(0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime){
            ActivityCompat.finishAffinity(this);
        }else{
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 누르시면 종료합니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
