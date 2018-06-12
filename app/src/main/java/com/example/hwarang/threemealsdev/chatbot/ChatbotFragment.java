package com.example.hwarang.threemealsdev.chatbot;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hwarang.threemealsdev.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
import ai.api.model.ResponseMessage;
import ai.api.model.Result;
import butterknife.ButterKnife;

import static ai.api.services.SpeaktoitRecognitionServiceImpl.TAG;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatbotFragment extends Fragment implements AIListener {
    public DietModel dietmodel = new DietModel();
    public ChatModel chatmodel = new ChatModel();
    public infoModel infoModel = new infoModel();
    List<String> food_list = new ArrayList<String>();
    AIService aiService;    //dialogflow 자연어음성처리
    AIDataService aiDataService;    //dialogflow 채팅처리
    private EditText message;   //메세지 입력칸
    private Button send_button; //전송버튼
    private ImageButton listen_button;  //마이크버튼
    private RecyclerView recyclerView;  //채팅창
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private SharedPreferences dateorder;
    private SharedPreferences welcomeflag;
    private SharedPreferences datetime;

    private long unixtime = System.currentTimeMillis();
    private Date date = new Date(unixtime);
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //a =am/pm, EEE=day ,,, 메세지 시간

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
        if (permission != PackageManager.PERMISSION_GRANTED) { //녹음기능 활성화 X면
            Log.i(TAG, "Permission to record denied");
            ActivityCompat.requestPermissions(this.requireActivity(),new String[]{android.Manifest.permission.RECORD_AUDIO},101);
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

        welcomeflag = getActivity().getSharedPreferences("welcomeflag", MODE_PRIVATE);
        datetime = getActivity().getSharedPreferences("datetime", MODE_PRIVATE);

        if (welcomeflag.getInt("welcomeflag",0)==0) { //처음실행시 welcome 실행
            FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").setValue(null);
            AIRequest aiRequest = new AIRequest();
            aiRequest.setQuery("welcome");
            try {
                final AIResponse aiResponse = aiDataService.request(aiRequest); // 내 메세지에 대한 반응
                onResult(aiResponse);   //결과 보여줌
            } catch (AIServiceException e) {
                e.printStackTrace();
            }
        } else {
            SharedPreferences.Editor editor_flag = welcomeflag.edit();
            int date_int = Integer.parseInt(simpleDateFormat.format(date).substring(11, 13));
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            chatmodel.time = simpleDateFormat.format(date);
            chatmodel.user = false;
            chatmodel.message = "안녕하세요! 1개씩 식단입력을 해주시거나 지난 날짜의 식단을 물어보세요.";
            int k = welcomeflag.getInt("welcomeflag",0);
            if (5 <= date_int && date_int < 11) {   //아침
                editor_flag.putInt("welcomeflag", 1);
            } else if (11 <= date_int && date_int < 16) {   //점심
                editor_flag.putInt("welcomeflag", 2);
            } else {
                editor_flag.putInt("welcomeflag", 3);
            }
            editor_flag.commit();

            if (k!=welcomeflag.getInt("welcomeflag", 0)) {
                FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").push().setValue(chatmodel);
            }

        }
        chatRoom(); //채팅내용 채팅창에 출력
        ButterKnife.bind(this, view);

        return view;
    }

    //record audio 권한 관련 저장
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],int[] grantResults){
        switch (requestCode){
            case 101:
                if(grantResults.length ==0||grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                }
                else{}
                return;
        }
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


    @Override
    public void onResult(AIResponse response) { // 반응에 대한 결과

        Result result = response.getResult();

        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        dietmodel.food = result.getStringParameter("foodname");
        dietmodel.End = result.getStringParameter("End");
        dietmodel.query = result.getResolvedQuery();
        dietmodel.time = simpleDateFormat.format(date);

        //현재시 계산을 통한 시간대
        int date_int = Integer.parseInt(simpleDateFormat.format(date).substring(11, 13));    //현재시 저장
        dateorder = getActivity().getSharedPreferences("dateorder", MODE_PRIVATE);
        datetime = getActivity().getSharedPreferences("datetime", MODE_PRIVATE);
        SharedPreferences.Editor editor = dateorder.edit();
        SharedPreferences.Editor editordate = datetime.edit();

        if(!result.getResolvedQuery().equals("welcome")) {
            if (dateorder.getInt("dateorder", 0) == 0) {
                if (result.getStringParameter("date-time").equals("")) {
                    editordate.putString("datetime", simpleDateFormat.format(date).substring(0, 10));
                    if (4 <= date_int && date_int < 11) {   //아침
                        editor.putInt("dateorder", 1);
                    } else if (11 <= date_int && date_int < 16) {   //점심
                        editor.putInt("dateorder", 2);
                    } else
                        editor.putInt("dateorder", 3);  //저녁
                } else {  // 이미 지난 시간의 식단 입력시 그 식단에 입력
                    int date_int2 = Integer.parseInt(result.getStringParameter("date-time").substring(11, 13));
                    editordate.putString("datetime", result.getStringParameter("date-time").replace("2019", "2018").substring(0, 10));
                    if (4 <= date_int2 && date_int2 < 11) {
                        editor.putInt("dateorder", 1);
                    } else if (11 <= date_int2 && date_int2 < 16) {
                        editor.putInt("dateorder", 2);
                    } else {
                        editor.putInt("dateorder", 3);
                        chatmodel.message = result.getFulfillment().getSpeech();
                    }
                }
            }
        }
        editor.commit();
        editordate.commit();
        dietmodel.date_order = dateorder.getInt("dateorder", 0);

        if(result.getStringParameter("howtouse").equals("true")){
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            chatmodel.time = simpleDateFormat.format(date);
            chatmodel.user = true;
            chatmodel.message = result.getResolvedQuery();
            FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").push().setValue(chatmodel);

            chatmodel.time = simpleDateFormat.format(date);
            chatmodel.user = false;
            
            int messageCount = result.getFulfillment().getMessages().size(); // 반응메세지list
            if (messageCount > 1) {
                for (int i = 0; i < messageCount; i++) {
                    ResponseMessage.ResponseSpeech responseMessage = (ResponseMessage.ResponseSpeech) result.getFulfillment().getMessages().get(i);
                    chatmodel.message = responseMessage.speech.toString().substring(1,responseMessage.speech.toString().length()-1);
                    FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").push().setValue(chatmodel);
                }
            }


            chatmodel.time = simpleDateFormat.format(date);
            chatmodel.message = "안녕하세요! 1개씩 식단입력을 해주시거나 지난 날짜의 식단을 물어보세요.";
            FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").push().setValue(chatmodel);

            SharedPreferences.Editor editor_flag = welcomeflag.edit();
            if (5 <= date_int && date_int < 11) {   //아침
                editor_flag.putInt("welcomeflag", 1);
            } else if (11 <= date_int && date_int < 16) {   //점심
                editor_flag.putInt("welcomeflag", 2);
            } else {
                editor_flag.putInt("welcomeflag", 3);
            }
            editor_flag.commit();
        }

        else if (!result.getStringParameter("check").equals("true")) {
            if (result.getFulfillment().getSpeech().equals("retry") || dietmodel.End.equals("true") || dietmodel.food.equals("welcome")) {
            }  // 이상한 말이 아니고 끝이면 식단에 입력 안함
            else {
                ValueEventListener nutirentListener = new ValueEventListener() { //음식 info 불러와서 넣기
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
                        if (dietmodel.foodAmount != 0) {
                            food_list.add(dietmodel.food);
                            FirebaseDatabase.getInstance().getReference().child(uid).child("Diet").child(datetime.getString("datetime", ""))
                                    .push().setValue(dietmodel);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                };
                FirebaseDatabase.getInstance().getReference().child("info").child(dietmodel.food).addListenerForSingleValueEvent(nutirentListener);
            }

            chatmodel.user = true;
            chatmodel.message = dietmodel.query;
            chatmodel.time = simpleDateFormat.format(date);
            if (!chatmodel.message.equals("welcome")) {
                FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").push().setValue(chatmodel); // 내 메세지를 chatDB에넣음
            }
            chatmodel.user = false;
            if (result.getFulfillment().getSpeech().equals("retry")) {
                chatmodel.message = "무슨 말씀인지 이해하지 못했습니다. 다시 말씀해주세요";
            } else if (dietmodel.End.equals("true")) {
                editor.putInt("dateorder", 0);
                editor.commit();
                chatmodel.message = datetime.getString("datetime", "")+"에 "+food_list.toString().substring(1, food_list.toString().length() - 1) + " 입력되었습니다.";
                food_list.clear();
            } else {
                if (result.getFulfillment().getSpeech().equals(""))
                    chatmodel.message = "무슨 말씀인지 이해하지 못했습니다. 다시 말씀해주세요";
                else
                    chatmodel.message = result.getFulfillment().getSpeech();
            }
            FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").push().setValue(chatmodel);  // 매세지에 대한 반응을 chatDB에 넣음
        }

        //todo 확인
        else if (result.getStringParameter("check").equals("true")) {
            datetime = getActivity().getSharedPreferences("datetime", MODE_PRIVATE);
            SharedPreferences.Editor editor2 = datetime.edit();
            editor2.putString("datetime", result.getStringParameter("date-time").replace("2019", "2018").substring(0, 10));
            editor2.commit();

            chatmodel.user = true;
            chatmodel.message = dietmodel.query;
            chatmodel.time = simpleDateFormat.format(date);
            if (!chatmodel.message.equals("welcome"))
                FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").push().setValue(chatmodel); // 내 메세지를 chatDB에넣음

            // 날짜 전체 식단
            if (result.getStringParameter("date-time").length() <= 25) {

                FirebaseDatabase.getInstance().getReference().child(uid).child("Diet").child(datetime.getString("datetime", "")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        food_list.clear();
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            if (item.getValue(DietModel.class).foodAmount != 0) {
                                food_list.add(item.getValue(DietModel.class).food);
                            }
                        }
                        chatmodel.user = false;
                        if (!food_list.toString().equals("[]")) {
                            chatmodel.message = datetime.getString("datetime", "") + "에 " + food_list.toString().substring(1, food_list.toString().length() - 1) + " 드셨습니다.";
                        } else {
                            chatmodel.message = "드신게 없습니다.";
                        }
                        chatmodel.time = simpleDateFormat.format(date);
                        FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").push().setValue(chatmodel);
                        food_list.clear();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            //todo 날짜 dateorder 식단
            else {
                if (Integer.parseInt(result.getStringParameter("date-time").substring(11, 13)) == 8) {
                    FirebaseDatabase.getInstance().getReference().child(uid).child("Diet").child(datetime.getString("datetime", "")).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            food_list.clear();
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                if (item.getValue(DietModel.class).foodAmount != 0 && item.getValue(DietModel.class).date_order == 1) {
                                    food_list.add(item.getValue(DietModel.class).food);
                                }
                            }
                            chatmodel.user = false;
                            if (!food_list.toString().equals("[]")) {
                                chatmodel.message = datetime.getString("datetime", "") + " 아침에 " + food_list.toString().substring(1, food_list.toString().length() - 1) + " 드셨습니다.";
                            } else {
                                chatmodel.message = "드신게 없습니다.";
                            }
                            chatmodel.time = simpleDateFormat.format(date);
                            FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").push().setValue(chatmodel);
                            food_list.clear();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else if (Integer.parseInt(result.getStringParameter("date-time").substring(11, 13)) == 12) {
                    FirebaseDatabase.getInstance().getReference().child(uid).child("Diet").child(datetime.getString("datetime", "")).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            food_list.clear();
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                if (item.getValue(DietModel.class).foodAmount != 0 && item.getValue(DietModel.class).date_order == 2) {
                                    food_list.add(item.getValue(DietModel.class).food);
                                }
                            }
                            chatmodel.user = false;
                            if (!food_list.toString().equals("[]")) {
                                chatmodel.message = datetime.getString("datetime", "") + " 점심에 " + food_list.toString().substring(1, food_list.toString().length() - 1) + " 드셨습니다.";
                            } else {
                                chatmodel.message = "드신게 없습니다.";
                            }
                            chatmodel.time = simpleDateFormat.format(date);
                            FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").push().setValue(chatmodel);
                            food_list.clear();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    FirebaseDatabase.getInstance().getReference().child(uid).child("Diet").child(datetime.getString("datetime", "")).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            food_list.clear();
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                if (item.getValue(DietModel.class).foodAmount != 0 && item.getValue(DietModel.class).date_order == 3) {
                                    food_list.add(item.getValue(DietModel.class).food);
                                }
                            }

                            chatmodel.user = false;
                            if (!food_list.toString().equals("[]")) {
                                chatmodel.message = datetime.getString("datetime", "") + " 저녁에 " + food_list.toString().substring(1, food_list.toString().length() - 1) + " 드셨습니다.";
                            } else {
                                chatmodel.message = "드신게 없습니다.";
                            }
                            chatmodel.time = simpleDateFormat.format(date);
                            FirebaseDatabase.getInstance().getReference().child(uid).child("Chat").push().setValue(chatmodel);
                            food_list.clear();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        }
        chatRoom(); // 채팅창 최신화해서 나타내기
    }

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

    // item_message -> fragment_chatbot의 recyclerview의 채팅창으로 만듬
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
            MessageViewHolder messageViewHolder = (MessageViewHolder)holder;


            // 내 메세지
            if(messages.get(position).user) {
                messageViewHolder.textView_message.setText(messages.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.chatbot3);
                messageViewHolder.linearLayout_chatbot.setVisibility(View.INVISIBLE);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
            }
            // 챗봇 메세지
            else{
                messageViewHolder.textView_message.setText(messages.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.chatbot3);
                messageViewHolder.linearLayout_chatbot.setVisibility(View.VISIBLE);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
            }
        }
        @Override
        public int getItemCount() {
            return messages.size();
        }
        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public LinearLayout linearLayout_chatbot;
            public LinearLayout linearLayout_main;

            public MessageViewHolder(View itemView) {
                super(itemView);
                textView_message = itemView.findViewById(R.id.messageItem_textview);
                linearLayout_chatbot = itemView.findViewById(R.id.item_message_LinearLayout_chatbot);
                linearLayout_main = itemView.findViewById(R.id.item_message_LinearLayout_main);
            }
        }
    }
}
