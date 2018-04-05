package com.example.hwarang.threemealsdev.chatbot;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwarang.threemealsdev.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatbotFragment extends Fragment {


    public ChatbotFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance(){
        ChatbotFragment chatbotFragment = new ChatbotFragment();
        return chatbotFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
