package com.example.hwarang.threemealsdev.main;

public class UserData {

    public String firebaseKey; // Firebase Realtime Database 에 등록된 키 값
    public String userName;     // 사용자 이름
    public String email;        // user email
    public String userPhoto;    // user profile picture
    public Long userAge;      // 사용자 나이
    public Double userHeight;   // 사용자 키
    public Double userWeight;   // 사용자 몸무게
    public Double stdWeight;    // 사용자 표준 몸무게
    public Double userCalorie;  // 사용자 권장 섭취 칼로리
    public Boolean userGender;  // 사용자 성별
    public Double userPhysical;   // 사용자 활동량
    public Double[] userNutrient; // 사용자 권장 섭취 영양소


    public UserData(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)

    }

    public UserData(String userName, String email, Long userAge, Double userHeight,
                    Double userWeight, Double stdWeight, Double userCalorie, Boolean userGender, Double userPhysical){
        this.userName = userName;
        this.email = email;
        this.userAge = userAge;
        this.userHeight = userHeight;
        this.userWeight = userWeight;
        this.stdWeight = stdWeight;
        this.userCalorie = userCalorie;
        this.userGender = userGender;
        this.userPhysical = userPhysical;
    }
    //TODO 나중에 영양소 추가해서 생성자 바꿔줘야함

}