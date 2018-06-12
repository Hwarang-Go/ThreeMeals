package com.example.hwarang.threemealsdev.main;

import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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
        toolbar.setBackgroundResource(R.color.white);
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
                //Log.d("profile img check",userData.userPhoto);
            }
            sp = getSharedPreferences("isFirst", MODE_PRIVATE);
            boolean first = sp.getBoolean("isFirst", false);
            Log.d("sp set", "set sp false");
            if(!first){
                Log.d("Is first Time?","yes first");

                //나중에 주석 해제해야 최초 실행 이후에는 팝업창이 뜨지 않음.
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isFirst", true);
                editor.apply();

                //최초 실행 시 나이, 키, 몸무게 입력하라는 팝업 띄울 위치
                //startActivity(new Intent(this, FirstPopupActivity.class));
                // 다이일로그 바디
                final AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
                //메세지
                alert_confirm.setMessage("사용자 정보를 입력해야 합니다");
                // 바깥 터치 시 안 닫히게
                alert_confirm.setCancelable(false);
                // 확인 버튼 리스너
                alert_confirm.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(MainActivity.this, FirstPopupActivity.class);
                        startActivity(i);
                    }
                });
                // 다이얼로그 생성
                final AlertDialog alert = alert_confirm.create();
                // 아이콘
                alert.setIcon(R.drawable.ic_mom);
                // 다이얼로그 타이틀
                alert.setTitle("처음 오셨군요!");
                // 다이얼로그 띄우기
                alert.show();
                //
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        Intent i = new Intent(MainActivity.this, FirstPopupActivity.class);
                        startActivity(i);
                    }
                });
            }else{
                Log.d("Is first Time?","not first");
            }
        }


        // DrawerLayout 부분 start
        mDrawerLayout = (DrawerLayout)findViewById(R.id.layout_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        // DrawerLayout 부분 end

        // actionbar 설정 부분 start
        final ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle(getString(R.string.app_name));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_ourlauncher2);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // actionbar 설정 부분 end



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
                }else if(id == R.id.menu_nav_update_profile){
                    Intent i = new Intent(MainActivity.this, FirstPopupActivity.class);
                    startActivity(i);
                }

                // 다하고 드로워 닫아주기
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        View nav_profie_view = navigationView.getHeaderView(0);
        TextView nav_profile_name = (TextView)nav_profie_view.findViewById(R.id.profile_name);
        nav_profile_name.setText(userData.userName);
        Log.d("profile check", nav_profile_name.getText().toString());
        TextView nav_profile_email = (TextView) nav_profie_view.findViewById(R.id.profile_email);
        nav_profile_email.setText(userData.email);
        Log.d("profile check", nav_profile_email.getText().toString());

        ImageView nav_profile_photo = (ImageView) nav_profie_view.findViewById(R.id.profile_img);
        Picasso.get()
                .load(userData.userPhoto)
                .error(R.drawable.ic_bot)
                .into(nav_profile_photo);
        Log.d("profile img check",userData.userPhoto+"1");
        // Navigation View end


        // bottom Navigation view 부분 start


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

    }


    /*public void onClickButton(View view){
        FirebaseAuth.getInstance().signOut();
        Log.d("signout ok?", "signout");
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }*/

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
