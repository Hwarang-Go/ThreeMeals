package com.example.hwarang.threemealsdev.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwarang.threemealsdev.R;
import com.example.hwarang.threemealsdev.login.LoginActivity;
import com.example.hwarang.threemealsdev.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

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

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /*public void onClickButton(View view){
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
    }*/
}
