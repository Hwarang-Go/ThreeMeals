package com.example.hwarang.threemealsdev.chatbot;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hwarang.threemealsdev.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ai.api.android.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import butterknife.ButterKnife;

import static ai.api.services.SpeaktoitRecognitionServiceImpl.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatbotFragment extends Fragment implements AIListener {
    private EditText message;   //메세지 입력칸
    private Button send_button; //전송버튼
    private ImageButton listen_button;  //마이크버튼
    private RecyclerView recyclerView;  //채팅창
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public DietModel dietmodel = new DietModel();
    public ChatModel chatmodel = new ChatModel();
    public infoModel infoModel = new infoModel();

    AIService aiService;    //dialogflow 자연어음성처리
    AIDataService aiDataService;    //dialogflow 채팅처리
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

        //thread 관련 오류 안나게함
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        int permission = ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.RECORD_AUDIO);
        if(FirebaseAuth.getInstance().getCurrentUser()==null) { //처음실행시
            if (permission != PackageManager.PERMISSION_GRANTED) { //녹음기능 활성화 X면
                Log.i(TAG, "Permission to record denied");
                makeRequest();  // 녹음기능 활성화 요청
            }
        }

        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);
        message = (EditText) view.findViewById(R.id.message_text);
        listen_button = (ImageButton) view.findViewById(R.id.listen_button);
        send_button = (Button) view.findViewById(R.id.send_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.message_recyclerview);

        //dialogflow 내꺼 tokenkey 한국어
        final AIConfiguration config = new AIConfiguration("483796ebcf564547a9ae3df7e6ee007c",
                AIConfiguration.SupportedLanguages.Korean,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this.getContext(), config);
        aiDataService = new AIDataService(this.getContext(), config);
        aiService.setListener(this);

        listen_button.setOnClickListener(new View.OnClickListener() {   //마이크 누르면 startlistening
            @Override
            public void onClick(View v) {
                        aiService.startListening();
            }
        });
        send_button.setOnClickListener(new View.OnClickListener() {     // 전송 누르면 채팅처리
            @Override
            public void onClick(View v) {
                AIRequest aiRequest = new AIRequest();
                aiRequest.setQuery(message.getText().toString());   // 내 메세지를 요청
                message.setText("");
                        try {
                            final AIResponse aiResponse = aiDataService.request(aiRequest); // 내 메세지에 대한 반응
                            onResult(aiResponse);   //결과 보여줌
                        } catch (AIServiceException e) {
                            e.printStackTrace();
                        }
            }
        });

        chatRoom(); //채팅내용 채팅창에 출력
        ButterKnife.bind(this, view);

        return view;
    }

    //녹음기능 요청
    protected void makeRequest(){
        ActivityCompat.requestPermissions(this.requireActivity(),
                new String[]{android.Manifest.permission.RECORD_AUDIO},101);
    }
    /*
    public void onRequestPermissionsResult(int requestCode, String permissions[],int[] grantResults){
        switch (requestCode){
            case 101:
                if(grantResults.length ==0||grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                }
                else{}
                return;
        }
    }*/

    @Override
    public void onResult(AIResponse response) { // 반응에 대한 결과

        Result result = response.getResult();

        long unixtime = System.currentTimeMillis();
        Date date = new Date(unixtime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //a =am/pm, EEE=day ,,, 메세지 시간
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("yyyy-MM-dd");    // 식단날짜 아직 쓸지 결정X
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        simpleDateFormat_date.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            dietmodel.food = result.getStringParameter("foodname");
            dietmodel.End = result.getStringParameter("End");
            dietmodel.query = result.getResolvedQuery();
            dietmodel.time = simpleDateFormat.format(date);

            if(result.getFulfillment().getSpeech().equals("retry")||dietmodel.End.equals("true")) {}  // 이상한 말이 아니고 끝이라고하지않으면 식단에 입력
            else {
                    output_info();
            }
            chatmodel.user = true;
            chatmodel.message = dietmodel.query;
            chatmodel.time = simpleDateFormat.format(date);
            FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").push().setValue(chatmodel); // 내 메세지를 chatDB에넣음
            chatmodel.user = false;
            chatmodel.message = result.getFulfillment().getSpeech();
            FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").push().setValue(chatmodel);  // 매세지에 대한 반응을 chatDB에 넣음

            chatRoom(); // 채팅창최신화
    }

    void output_info(){
        ValueEventListener nutirentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI;
                infoModel = dataSnapshot.getValue(infoModel.class);
                dietmodel.foodAmount = infoModel.foodAmount;
                dietmodel.kcal = infoModel.kcal;
                dietmodel.carbo = infoModel.carbo;
                dietmodel.protein = infoModel.protein;
                dietmodel.fat = infoModel.fat;
                dietmodel.calcium = infoModel.calcium;
                dietmodel.iron = infoModel.iron;
                dietmodel.natrium = infoModel.natrium;
                dietmodel.vitaminA = infoModel.vitaminA;
                dietmodel.vitaminB = infoModel.vitaminB;
                dietmodel.vitaminC = infoModel.vitaminC;


                FirebaseDatabase.getInstance().getReference().child(uid).child("Diet").push().setValue(dietmodel);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        FirebaseDatabase.getInstance().getReference().child("info").child(dietmodel.food).addValueEventListener(nutirentListener);
    }
/*
    void input_info(String name,double foodAmount, double kcal, double car, double pro, double fat, double calcium, double iron, double natrium, double vitaminA, double vitaminB, double vitaminC)
    {
        infoModel info = new infoModel();
        info.foodname = name;
        info.foodAmount = foodAmount;
        info.kcal = kcal;
        info.carbo = car/10;
        info.protein = pro/10;
        info.fat = fat/10;
        info.calcium = calcium/10;
        info.iron = iron/10;
        info.natrium = natrium/10;
        info.vitaminA = vitaminA/10;
        info.vitaminB = vitaminB/100;
        info.vitaminC = vitaminC/10;
        FirebaseDatabase.getInstance().getReference().child("info").child(name).setValue(info);
    }
*/
    void chatRoom(){
        //내 uid로 된 chat이 있으면 다 읽어와서 recycyclerview로출력
        FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recyclerView.setLayoutManager(new LinearLayoutManager(ChatbotFragment.super.getContext()));
                recyclerView.setAdapter(new RecyclerViewAdapter());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    //aiservice 음성처리 관련
    @Override
    public void onError(AIError error) {
       // t.setText(error.toString());
    }
    @Override   //ai음성 오디오 레벨설졍
    public void onAudioLevel(float level) {
    }
    @Override
    public void onListeningStarted() {
    }
    @Override
    public void onListeningCanceled() {
    }
    @Override
    public void onListeningFinished() {
    }
    /////////////////////////////////////////////////////

    // item_message -> fragment_chatbot의 recyclerview의 채팅창으로 만듬
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        List<ChatModel> messages;
        public RecyclerViewAdapter(){
            messages = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    messages.clear();
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        messages.add(item.getValue(ChatModel.class));
                    }
                    notifyDataSetChanged(); // 갱신
                    recyclerView.scrollToPosition(messages.size()-1); // 맨밑으로 스크롤 해줌
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
            return new MessageViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((MessageViewHolder)holder).textView_message.setText(messages.get(position).message);
        }
        @Override
        public int getItemCount() {
            return messages.size();
        }
        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;

            public MessageViewHolder(View itemView) {
                super(itemView);
                textView_message = itemView.findViewById(R.id.messageItem_textview);
            }
        }
    }
}
