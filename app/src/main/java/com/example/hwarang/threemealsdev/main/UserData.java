package com.example.hwarang.threemealsdev.main;

public class UserData {

    public String firebaseKey; // Firebase Realtime Database 에 등록된 키 값
    public String userName;     // 사용자 이름
    public String email;        // user email
    public String userPhoto;    // user profile picture
    public long userAge;      // 사용자 나이
    public double userHeight;   // 사용자 키
    public double userWeight;   // 사용자 몸무게
    public double stdWeight;    // 사용자 표준 몸무게
    public double userCalorie;  // 사용자 권장 섭취 칼로리
    public boolean userGender;  // 사용자 성별
    public double userPhysical;   // 사용자 활동량
    //public double userKcal; // 사용자 추천 칼로리
    public double userCarbo;    // 사용자 추천 탄수화물
    public double userProtein;  // 사용자 추천 단백질
    public double userFat;  // 사용자 추천 지방
    public double userCalcium;  // 사용자 추천 칼슘
    public double userIron; // 사용자 추천 철
    public double userNatrium;  // 사용자 추천 나트륨
    public double userVitaminA; // 사용자 추천 비타민A
    public double userVitaminB; // 사용자 추천 비타민B
    public double userVitaminC; // 사용자 추천 비타민C


    public UserData(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)

    }

    public UserData(String userName, String email, Long userAge, Double userHeight,
                    Double userWeight, Double stdWeight, Double userCalorie, Boolean userGender,
                    Double userPhysical, double userCarbo, double userProtein,
                    double userFat, double userCalcium, double userIron, double userNatrium,
                    double userVitaminA, double userVitaminB, double userVitaminC){
        this.userName = userName;
        this.email = email;
        this.userAge = userAge;
        this.userHeight = userHeight;
        this.userWeight = userWeight;
        this.stdWeight = stdWeight;
        this.userCalorie = userCalorie;
        this.userGender = userGender;
        this.userPhysical = userPhysical;
        //this.userKcal = userKcal;
        this.userCarbo = userCarbo;
        this.userProtein = userProtein;
        this.userFat = userFat;
        this.userCalcium = userCalcium;
        this.userIron = userIron;
        this.userNatrium = userNatrium;
        this.userVitaminA = userVitaminA;
        this.userVitaminB = userVitaminB;
        this.userVitaminC = userVitaminC;

    }

}