package com.example.hwarang.threemealsdev.home;


import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hwarang.threemealsdev.R;
import com.example.hwarang.threemealsdev.login.LoginActivity;
import com.example.hwarang.threemealsdev.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private final static String TAG = "HomeFragment";

    /*private Button btn;*/

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

        /*btn = (Button)view.findViewById(R.id.btn_noti_test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(v.getContext(), 0, intent, 0);

                Notification.Builder builder = new Notification.Builder(v.getContext());
                builder.setContentIntent(pendingIntent)
                        .setContentText("test text")
                        .setContentTitle("test title")
                        .setTicker("test ticker")
                        .setAutoCancel(true);
                NotificationManager notificationManager = (NotificationManager) v.getContext().getSystemService(v.getContext().NOTIFICATION_SERVICE);

                notificationManager.notify(112, builder.build());
            }
        });*/


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



}
