package com.example.campusconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.campusconnect.model.ChatroomModel;
import com.example.campusconnect.model.UserModel;
import com.example.campusconnect.utils.AndroidUtil;
import com.example.campusconnect.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get Usermodel
        otherUser= AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId= FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUserId());

        messageInput=findViewById(R.id.chat_message_input);
        sendMessageBtn=findViewById(R.id.message_send_btn);
        backBtn=findViewById(R.id.back_btn);
        otherUsername=findViewById(R.id.other_username);
        recyclerView=findViewById(R.id.chat_recycler_view);

        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());

        getOrCreateChatroomModel();
    }

    void getOrCreateChatroomModel(){
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                chatroomModel=task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel==null){
                    //first time chat
                    chatroomModel =new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }
}