package com.example.hwarang.threemealsdev.main;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hwarang.threemealsdev.R;
import com.example.hwarang.threemealsdev.login.LoginActivity;
import com.example.hwarang.threemealsdev.notification.NotificationReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Optional;

import static java.util.Calendar.AM_PM;

public class FirstPopupActivity extends Activity {

    TimePicker myTimePicker;    // timepicker
    TimePickerDialog timePickerDialog; // timepicker dialog

    //public int mealTime = 1;

    private EditText edtAge, edtTall, edtWeight;

    public int checkTime=0;

    /*@Nullable @BindView(R.id.img_time_morning) ImageButton img_time_morning;
    @Nullable @BindView(R.id.img_time_lunch) ImageButton img_time_lunch;
    @Nullable @BindView(R.id.img_time_dinner) ImageButton img_time_dinner;*/
    ImageButton imageButtonMor;
    ImageButton imageButtonLun;
    ImageButton imageButtonDin;


    // firebase 관련
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    Spinner spinnerGender;
    Spinner spinnerPhysical;

    // user 정보
    private String mName, mEmail, uid; // 이름, 이메일, Firebase Realtime Database 에 등록된 키 값
    private long userAge;      // 사용자 나이
    private double userHeight, userWeight, stdWeight, userCalorie;   // 사용자 키, 몸무게, 표준몸무게, 권장섭취칼로리
    public double userKcal, userCarbo, userProtein, userFat, userCalcium, userIron, userNatrium,
            userVitaminA, userVitaminB, userVitaminC; // 사용자 추천 칼로리, 탄수화물, 단백질, 지방, 칼슘, 철, 나트륨, 비타민ABC
    private Boolean userGender;
    private Double userPhysical;    // 사용자 활동량
    private ArrayAdapter sAdapter, pAdapter;
    private int sSelect, pSelect;

    private int morHour = 0, morMinute = 0, lunHour = 0, lunMinute = 0, dinHour = 0, dinMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);


        //타이틀바 없애기
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_first_popup);

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

        imageButtonMor = (ImageButton)findViewById(R.id.img_time_morning);
        imageButtonMor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Calendar calendar1 = Calendar.getInstance();
                morHour = calendar1.get(Calendar.HOUR_OF_DAY);
                morMinute = calendar1.get(Calendar.MINUTE);
                checkTime = 1; // 아침
                timePickerDialog = new TimePickerDialog(FirstPopupActivity.this, R.style.DatePicker,
                        mTimeSetListener, morHour, morMinute, false);
                timePickerDialog.setTitle("아침 식사 시간 설정");
                timePickerDialog.show();
            }
        });
        imageButtonLun = (ImageButton)findViewById(R.id.img_time_lunch);
        imageButtonLun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar2 = Calendar.getInstance();
                lunHour = calendar2.get(Calendar.HOUR_OF_DAY);
                lunMinute = calendar2.get(Calendar.MINUTE);
                checkTime = 2; // 점심
                timePickerDialog = new TimePickerDialog(FirstPopupActivity.this, R.style.DatePicker,
                        mTimeSetListener, lunHour, lunMinute, false);
                timePickerDialog.setTitle("점심 식사 시간 설정");
                timePickerDialog.show();
            }
        });
        imageButtonDin = (ImageButton)findViewById(R.id.img_time_dinner);
        imageButtonDin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar3 = Calendar.getInstance();
                dinHour = calendar3.get(Calendar.HOUR_OF_DAY);
                dinMinute = calendar3.get(Calendar.MINUTE);
                checkTime = 3; // 점심
                timePickerDialog = new TimePickerDialog(FirstPopupActivity.this, R.style.DatePicker,
                        mTimeSetListener, dinHour, dinMinute, false);
                timePickerDialog.setTitle("저녁 식사 시간 설정");
                timePickerDialog.show();
            }
        });
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        //intent.putExtra("result", "Close Popup");
        //setResult(RESULT_OK, intent);

        //TODO 현재 빈칸으로 제출 시 에러나는 경우 발견함, 추후에 머터리얼 디자인으로 구성하면서 수정해야함.

        if(edtAge.getText().toString().length() <= 0
                || edtTall.getText().toString().length() <= 0
                || edtWeight.getText().toString().length() <= 0){
            Toast.makeText(this,"정보를 입력해주세요!",Toast.LENGTH_LONG).show();
        }else{



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
                userNatrium = 700;  // 밀리그램
                userIron = 6;   // 밀리그램
            }else if(userAge <= 5){
                userCarbo = 60;
                userProtein = 20;
                userFat = 25;
                userVitaminA = 300;
                userVitaminB = 0.7;
                userVitaminC = 40;
                userCalcium = 600;
                userNatrium = 900;
                userIron = 7;
            }else if(userAge <= 8){
                userCarbo = 90;
                userProtein = 20;
                userFat = 25;
                userVitaminA = 400;
                userVitaminB = 0.9;
                userVitaminC = 60;
                userCalcium = 700;
                userNatrium = 1200;
                userIron = 8;
            }else if(userAge <= 11){
                userCarbo = 90;
                userProtein = 35;
                userFat = 25;
                userVitaminA = 550;
                userVitaminB = 1.1;
                userVitaminC = 70;
                userCalcium = 800;
                userNatrium = 1300;
                userIron = 11;
            }else if(userAge <= 14){
                userCarbo = userCalorie*0.6/4;
                userProtein = 50;
                userFat = userCalorie*0.25/9;
                userVitaminA = 700;
                userVitaminB = 1.5;
                userVitaminC = 100;
                userCalcium = 1000;
                userNatrium = 1500;
                userIron = 14;
            }else if(userAge <= 18){
                userCarbo = userCalorie*0.6/4;
                userProtein = 55;
                userFat = userCalorie*0.25/9;
                userVitaminA = 850;
                userVitaminB = 1.7;
                userVitaminC = 110;
                userCalcium = 900;
                userNatrium = 1500;
                userIron = 15;
            }else if(userAge <= 29){
                userCarbo = userCalorie*0.6/4;
                userProtein = 55;
                userFat = userCalorie*0.25/9;
                userVitaminA = 750;
                userVitaminB = 1.5;
                userVitaminC = 100;
                userCalcium = 750;
                userNatrium = 1500;
                userIron = 10;
            }else if(userAge <= 49){
                userCarbo = userCalorie*0.6/4;
                userProtein = 55;
                userFat = userCalorie*0.25/9;
                userVitaminA = 750;
                userVitaminB = 1.5;
                userVitaminC = 100;
                userCalcium = 750;
                userNatrium = 1500;
                userIron = 10;
            }else if(userAge <= 64){
                userCarbo = userCalorie*0.6/4;
                userProtein = 50;
                userFat = userCalorie*0.25/9;
                userVitaminA = 700;
                userVitaminB = 1.5;
                userVitaminC = 100;
                userCalcium = 700;
                userNatrium = 1400;
                userIron = 9;
            }else if(userAge <= 74){
                userCarbo = userCalorie*0.6/4;
                userProtein = 50;
                userFat = userCalorie*0.25/9;
                userVitaminA = 700;
                userVitaminB = 1.5;
                userVitaminC = 100;
                userCalcium = 700;
                userNatrium = 1200;
                userIron = 9;
            }else{ // 75이상
                userCarbo = userCalorie*0.6/4;
                userProtein = 50;
                userFat = userCalorie*0.25/9;
                userVitaminA = 700;
                userVitaminB = 1.5;
                userVitaminC = 100;
                userCalcium = 700;
                userNatrium = 1100;
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
                userNatrium = 700;  // 밀리그램
                userIron = 6;   // 밀리그램
            }else if(userAge <= 5){
                userCarbo = 60;
                userProtein = 20;
                userFat = 25;
                userVitaminA = 300;
                userVitaminB = 0.7;
                userVitaminC = 40;
                userCalcium = 600;
                userNatrium = 900;
                userIron = 7;
            }else if(userAge <= 8){
                userCarbo = 90;
                userProtein = 25;
                userFat = 25;
                userVitaminA = 400;
                userVitaminB = 0.7;
                userVitaminC = 60;
                userCalcium = 700;
                userNatrium = 1200;
                userIron = 8;
            }else if(userAge <= 11){
                userCarbo = 90;
                userProtein = 35;
                userFat = 25;
                userVitaminA = 500;
                userVitaminB = 0.9;
                userVitaminC = 80;
                userCalcium = 800;
                userNatrium = 1300;
                userIron = 10;
            }else if(userAge <= 14){
                userCarbo = userCalorie*0.6/4;
                userProtein = 45;
                userFat = userCalorie*0.25/9;
                userVitaminA = 650;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 900;
                userNatrium = 1500;
                userIron = 13;
            }else if(userAge <= 18){
                userCarbo = userCalorie*0.6/4;
                userProtein = 45;
                userFat = userCalorie*0.25/9;
                userVitaminA = 600;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 900;
                userNatrium = 1500;
                userIron = 17;
            }else if(userAge <= 29){
                userCarbo = userCalorie*0.6/4;
                userProtein = 50;
                userFat = userCalorie*0.25/9;
                userVitaminA = 650;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 750;
                userNatrium = 1500;
                userIron = 14;
            }else if(userAge <= 49){
                userCarbo = userCalorie*0.6/4;
                userProtein = 45;
                userFat = userCalorie*0.25/9;
                userVitaminA = 650;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 750;
                userNatrium = 1500;
                userIron = 14;
            }else if(userAge <= 64){
                userCarbo = userCalorie*0.6/4;
                userProtein = 45;
                userFat = userCalorie*0.25/9;
                userVitaminA = 1600;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 700;
                userNatrium = 1400;
                userIron = 8;
            }else if(userAge <= 74){
                userCarbo = userCalorie*0.6/4;
                userProtein = 45;
                userFat = userCalorie*0.25/9;
                userVitaminA = 600;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 700;
                userNatrium = 1200;
                userIron = 8;
            }else{ // 75이상
                userCarbo = userCalorie*0.6/4;
                userProtein = 45;
                userFat = userCalorie*0.25/9;
                userVitaminA = 600;
                userVitaminB = 1.2;
                userVitaminC = 100;
                userCalcium = 700;
                userNatrium = 1100;
                userIron = 8;
            }
        }




        writeNewUser(uid ,mName, mEmail, userAge, userHeight, userWeight, stdWeight, userCalorie,
                userGender, userPhysical, userCarbo, userProtein, userFat, userCalcium,
                userIron, userNatrium, userVitaminA, userVitaminB, userVitaminC);
        //액티비티(팝업) 닫기
        finish();
        }
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

    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            //Time.set(Calendar.HOUR_OF_DAY, hourOfDay)
            Calendar calNow = Calendar.getInstance(); // 현재 시간을 위한 캘린더 객체
            Calendar calSet = (Calendar)calNow.clone(); // 바로 위에서 구한 객체 복제

            switch(checkTime){
                case 1:
                    calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);    // 시간 설정
                    calSet.set(Calendar.MINUTE, minute);    // 분 설정
                    calSet.set(Calendar.MILLISECOND,0);     // 밀리초 0으로 설정
                    if(calSet.compareTo(calNow) <= 0){  // 설정한 시간과 현재 시간 비교
                        // 만약 설정한 시간이 현재 시간보다 이전이면
                        calSet.add(Calendar.DATE, 1);    // 설정 시간에 하루 더함.
                    }
                    setAlarm(calSet); // 주어진 시간으로 알람 설정
                    break;
               case 2:
                   calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);    // 시간 설정
                   calSet.set(Calendar.MINUTE, minute);    // 분 설정
                   calSet.set(Calendar.MILLISECOND,0);     // 밀리초 0으로 설정
                   if(calSet.compareTo(calNow) <= 0){  // 설정한 시간과 현재 시간 비교
                       // 만약 설정한 시간이 현재 시간보다 이전이면
                       calSet.add(Calendar.DATE, 1);    // 설정 시간에 하루 더함.
                   }
                   setAlarm(calSet); // 주어진 시간으로 알람 설정
                   break;
                case 3:
                    calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);    // 시간 설정
                    calSet.set(Calendar.MINUTE, minute);    // 분 설정
                    calSet.set(Calendar.MILLISECOND,0);     // 밀리초 0으로 설정
                    if(calSet.compareTo(calNow) <= 0){  // 설정한 시간과 현재 시간 비교
                        // 만약 설정한 시간이 현재 시간보다 이전이면
                        calSet.add(Calendar.DATE, 1);    // 설정 시간에 하루 더함.
                    }
                    setAlarm(calSet); // 주어진 시간으로 알람 설정
                    break;
            }
        }
    };


    private void setAlarm(Calendar targetCal){

        // pendingintent
        Intent intent = new Intent(getBaseContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), checkTime, intent, 0);

        // 알람매니저에 알람 시간 설정
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), 24*60*60*1000, pendingIntent);
    }
}

