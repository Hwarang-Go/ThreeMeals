package com.example.hwarang.threemealsdev.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwarang.threemealsdev.R;
import com.example.hwarang.threemealsdev.chatbot.DietModel;
import com.example.hwarang.threemealsdev.login.LoginActivity;
import com.example.hwarang.threemealsdev.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    ArrayList<WordItemData> inputData = new ArrayList<WordItemData>();
    public HomeTLAdapter adapter;
    public FirebaseDatabase mDatabase;
    public DatabaseReference mReference;
    public SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
    public long mNow;
    public Date mDate;

    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    private final static String TAG = "HomeFragment";

    public HomeFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance(){
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        recyclerView = (RecyclerView)view.findViewById(R.id.home_recycler);

        setData();
        setRecyclerView();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    void setRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HomeTLAdapter(getActivity(),inputData);
        recyclerView.setAdapter(adapter);

    }

    public void setData(){



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//현재 유저
        String userid = user.getUid();//유저 아이디 획득
        String sDate = getTime();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference().child(userid).child("Diet");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inputData.clear();
                for( DataSnapshot snapshot : dataSnapshot.getChildren() ){//날짜
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){//각 끼니?
                        DietModel dm = snapshot1.getValue(DietModel.class);
                        WordItemData item = new WordItemData(dm.time,dm.carbo,dm.protein,dm.fat,dm.foodname);
                        inputData.add(item);
                    }

            }
            setRecyclerView();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

