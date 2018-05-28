package com.example.hwarang.threemealsdev.main;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hwarang.threemealsdev.R;
import com.example.hwarang.threemealsdev.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class FirstPopupActivity extends Activity {

    private EditText edtAge;
    private EditText edtTall;
    private EditText edtWeight;
    /*private AppCompatEditText a;
    private TextInputLayout ageLayout;
    private TextInputLayout tallLayout;
    private TextInputLayout weightLayout;*/

    // firebase 관련
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    //@BindView(R.id.txt_question_gender)
    Spinner spinnerGender;
    //@BindView(R.id.txt_question_physical)
    Spinner spinnerPhysical;

    // user 정보
    private String mName;
    private String mEmail;
    private String uid; // Firebase Realtime Database 에 등록된 키 값
    private Long userAge;      // 사용자 나이
    private Double userHeight;   // 사용자 키
    private Double userWeight;   // 사용자 몸무게
    private Double stdWeight;    // 사용자 표준 몸무게
    private Double userCalorie;  // 사용자 권장 섭취 칼로리
    public double userKcal; // 사용자 추천 칼로리
    public double userCarbo;    // 사용자 추천 탄수화물
    public double userProtein;  // 사용자 추천 단백질
    public double userFat;  // 사용자 추천 지방
    public double userCalcium;  // 사용자 추천 칼슘
    public double userIron; // 사용자 추천 철
    public double userNatrium;  // 사용자 추천 나트륨
    public double userVitaminA; // 사용자 추천 비타민A
    public double userVitaminB; // 사용자 추천 비타민B
    public double userVitaminC; // 사용자 추천 비타민C
    private Boolean userGender;
    private Double userPhysical;
    private ArrayAdapter sAdapter;
    private ArrayAdapter pAdapter;
    private int sSelect;
    private int pSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        //타이틀바 없애기
        Log.d("is in popup?", "im in popup");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d("is in popup?", "im in popup2");
        setContentView(R.layout.activity_first_popup);
        Log.d("is in popup?", "im in popup3");

        // firebase database 및 auth, user profile 가져올 것들
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser ==  null){
            // 현재 로그인된 세션이 없는경우
            Intent i = new Intent(this, LoginActivity.class);// 로그인으로 보내버림
            startActivity(i);
            finish();
        }else{
            mName = mFirebaseUser.getDisplayName();
            mEmail = mFirebaseUser.getEmail();
            uid = mFirebaseUser.getUid();
        }

        // 스피너 연결
        spinnerGender = (Spinner)findViewById(R.id.txt_question_gender);
        spinnerPhysical = (Spinner)findViewById(R.id.txt_question_physical);


        sAdapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_dropdown_item);

        spinnerGender.setAdapter(sAdapter);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sSelect = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        pAdapter = ArrayAdapter.createFromResource(this, R.array.physical, android.R.layout.simple_spinner_dropdown_item);
        spinnerPhysical.setAdapter(pAdapter);
        spinnerPhysical.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pSelect = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //UI 객체생성
        edtAge = (EditText)findViewById(R.id.edit_text_age);
        edtTall = (EditText)findViewById(R.id.edit_text_tall);
        edtWeight = (EditText)findViewById(R.id.edit_text_weight);

        //TODO TextInputLayout 사용할 때 쓸 듯, 버터나이프로 바인딩 하면 없앨 것들
        /*ageLayout = (TextInputLayout)findViewById(R.id.text_input_layout1);
        tallLayout = (TextInputLayout)findViewById(R.id.text_input_layout2);
        weightLayout = (TextInputLayout)findViewById(R.id.text_input_layout3);
*/

        //데이터 가져오기
        //Intent intent = getIntent();
        //String data = intent.getStringExtra("data");
        //txtText.setText(data);

    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        //intent.putExtra("result", "Close Popup");
        //setResult(RESULT_OK, intent);

        //TODO 현재 빈칸으로 제출 시 에러나는 경우 발견함, 추후에 머터리얼 디자인으로 구성하면서 수정해야함.

        userAge = Long.parseLong(edtAge.getText().toString());
        userHeight = Double.parseDouble(edtTall.getText().toString());
        userWeight = Double.parseDouble(edtWeight.getText().toString());
        stdWeight = (userHeight/100)*(userHeight/100)*22;
        if(sSelect == 0){
            // man
            userGender = false;
            if(pSelect == 0){
                userPhysical = 1.0;
            }else if(pSelect == 1){
                userPhysical = 1.11;
            }else if(pSelect == 2){
                userPhysical = 1.25;
            }else{
                userPhysical = 1.48;
            }
        }else{
            // woman
            userGender = true;
            if(pSelect == 0){
                userPhysical = 1.0;
            }else if(pSelect == 1){
                userPhysical = 1.12;
            }else if(pSelect == 2){
                userPhysical = 1.27;
            }else{
                userPhysical = 1.45;
            }
        }

        if(userGender){
            // man
            userCalorie = 662-(9.53*userAge)+(userPhysical*(15.91*stdWeight+539.6*userHeight/100)); // Kcal
            if(userAge <= 2){
                userCarbo = 60; // 그램
                userProtein = 15;   // 그램
                userFat = 25;   // 그램
                userVitaminA = 300; // 마이크로그램
                userVitaminB = 0.6; // 밀리그램
                userVitaminC = 40;  // 밀리그램
                userCalcium = 500;  // 밀리그램
                userNatrium = 0.7;  // 밀리그램
                userIron = 6;   // 밀리그램
            }else if(userAge <= 5){
                userCarbo = 60;
                userProtein = 20;
                userFat = 25;
                userVitaminA = 300;
                userVitaminB = 0.7;
                userVitaminC = 40;
                userCalcium = 600;
                userNatrium = 0.9;
                userIron = 7;
            }else if(userAge <= 8){
                userCarbo = 90;
                userProtein = 20;
                userFat = 25;
                userVitaminA = 400;
                userVitaminB = 0.9;
                userVitaminC = 60;
                userCalcium = 700;
                userNatrium = 1.2;
                userIron = 8;
            }else if(userAge <= 11){
                userCarbo = 90;
                userProtein = 35;
                userFat = 25;
                userVitaminA = 550;
                userVitaminB = 1.1;
                userVitaminC = 70;
                userCalcium = 800;
                userNatrium = 1.3;
                userIron = 11;
            }else if(userAge <= 14){
                userCarbo = userCalorie*0.6/4;
                userProtein = 50;
                userFat = userCalorie*0.25/9;
                userVitaminA = 700;
                userVitaminB = 1.5;
                userVitaminC = 100;
                userCalcium = 1000;
                userNatrium = 1.5;
                userIron = 14;
            }else if(userAge <= 18){
                userCarbo = userCalorie*0.6/4;
                userProtein = 55;
                userFat = userCalorie*0.25/9;
                userVitaminA = 850;
                userVitaminB = 1.7;
                userVitaminC = 110;
                userCalcium = 900;
                userNatrium = 1.5;
                userIron = 15;
            }else if(userAge <= 29){
                userCarbo = userCalorie*0.6/4;
                userProtein = 55;
                userFat = userCalorie*0.25/9;
                userVitaminA = 750;
                userVitaminB = 1.5;
                userVitaminC = 100;
                userCalcium = 750;
                userNatrium = 1.5;
                userIron = 10;
            }else if(userAge <= 49){
                userCarbo = userCalorie*0.6/4;
                userProtein = 55;
                userFat = userCalorie*0.25/9;
                userVitaminA = 750;
                userVitaminB = 1.5;
                userVitaminC = 100;
                userCalcium = 750;
                userNatrium = 1.5;
                userIron = 10;
            }else if(userAge <= 64){
                userCarbo = userCalorie*0.6/4;
                userProtein = 50;
                userFat = userCalorie*0.25/9;
                userVitaminA = 700;
                userVitaminB = 1.5;
                userVitaminC = 100;
                userCalcium = 700;
                userNatrium = 1.4;
                userIron = 9;
            }else if(userAge <= 74){
                userCarbo = userCalorie*0.6/4;
                userProtein = 50;
                userFat = userCalorie*0.25/9;
                userVitaminA = 700;
                userVitaminB = 1.5;
                userVitaminC = 100;
                userCalcium = 700;
                userNatrium = 1.2;
                userIron = 9;
            }else{ // 75이상
                userCarbo = userCalorie*0.6/4;
                userProtein = 50;
                userFat = userCalorie*0.25/9;
                userVitaminA = 700;
                userVitaminB = 1.5;
                userVitaminC = 100;
                userCalcium = 700;
                userNatrium = 1.1;
                userIron = 9;
            }
        }else{
            // woman
            userCalorie = 354-(6.91*userAge)+(userPhysical*(9.36*stdWeight+726*userHeight/100));
            if(userAge <= 2){
                userCarbo = 60; // 그램
                userProtein = 15;   // 그램
                userFat = 25;   // 그램
                userVitaminA = 300; // 마이크로그램
                userVitaminB = 0.6; // 밀리그램
                userVitaminC = 40;  // 밀리그램
                userCalcium = 500;  // 밀리그램
                userNatrium = 0.7;  // 밀리그램
                userIron = 6;   // 밀리그램
            }else if(userAge <= 5){
                userCarbo = 60;
                userProtein = 20;
                userFat = 25;
                userVitaminA = 300;
                userVitaminB = 0.7;
                userVitaminC = 40;
                userCalcium = 600;
                userNatrium = 0.9;
                userIron = 7;
            }else if(userAge <= 8){
                userCarbo = 90;
                userProtein = 25;
                userFat = 25;
                userVitaminA = 400;
                userVitaminB = 0.7;
                userVitaminC = 60;
                userCalcium = 700;
                userNatrium = 1.2;
                userIron = 8;
            }else if(userAge <= 11){
                userCarbo = 90;
                userProtein = 35;
                userFat = 25;
                userVitaminA = 500;
                userVitaminB = 0.9;
                userVitaminC = 80;
                userCalcium = 800;
                userNatrium = 1.3;
                userIron = 10;
            }else if(userAge <= 14){
                userCarbo = userCalorie*0.6/4;
                userProtein = 45;
                userFat = userCalorie*0.25/9;
                userVitaminA = 650;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 900;
                userNatrium = 1.5;
                userIron = 13;
            }else if(userAge <= 18){
                userCarbo = userCalorie*0.6/4;
                userProtein = 45;
                userFat = userCalorie*0.25/9;
                userVitaminA = 600;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 900;
                userNatrium = 1.5;
                userIron = 17;
            }else if(userAge <= 29){
                userCarbo = userCalorie*0.6/4;
                userProtein = 50;
                userFat = userCalorie*0.25/9;
                userVitaminA = 650;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 750;
                userNatrium = 1.5;
                userIron = 14;
            }else if(userAge <= 49){
                userCarbo = userCalorie*0.6/4;
                userProtein = 45;
                userFat = userCalorie*0.25/9;
                userVitaminA = 650;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 750;
                userNatrium = 1.5;
                userIron = 14;
            }else if(userAge <= 64){
                userCarbo = userCalorie*0.6/4;
                userProtein = 45;
                userFat = userCalorie*0.25/9;
                userVitaminA = 1600;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 700;
                userNatrium = 1.4;
                userIron = 8;
            }else if(userAge <= 74){
                userCarbo = userCalorie*0.6/4;
                userProtein = 45;
                userFat = userCalorie*0.25/9;
                userVitaminA = 600;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 700;
                userNatrium = 1.2;
                userIron = 8;
            }else{ // 75이상
                userCarbo = userCalorie*0.6/4;
                userProtein = 45;
                userFat = userCalorie*0.25/9;
                userVitaminA = 600;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 700;
                userNatrium = 1.1;
                userIron = 8;
            }
        }




        writeNewUser(uid ,mName, mEmail, userAge, userHeight, userWeight, stdWeight, userCalorie,
                userGender, userPhysical, userCarbo, userProtein, userFat, userCalcium,
                userIron, userNatrium, userVitaminA, userVitaminB, userVitaminC);
        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

    //TODO ', Double[] userNutrient' 나중에 파라미터에 추가
    private void writeNewUser(String userId, String userName, String email, Long userAge, Double userHeight,
                              Double userWeight, Double stdWeight, Double userCalorie, Boolean userGender,
                              Double userPhysical, double userCarbo, double userProtein,
                              double userFat, double userCalcium, double userIron, double userNatrium,
                              double userVitaminA, double userVitaminB, double userVitaminC){
        UserData user = new UserData(userName, email, userAge, userHeight, userWeight, stdWeight,
                userCalorie, userGender, userPhysical, userCarbo, userProtein, userFat, userCalcium,
                userIron, userNatrium, userVitaminA, userVitaminB, userVitaminC);

        mDatabase.child("users").child(userId).setValue(user);

        // 전체 업데이트 말고 일부 하위항목만 업데이트 하는 경우
        // mDatabase.child("users").child(userId).child("username").setValue(name);
    }

    /*@OnItemSelected(R.id.txt_question_gender)
    void onItemSelected(int position){
        sSelect = position;
    }*/
}

