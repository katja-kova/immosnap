package com.example.immosnap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.immosnap.data.ImmoMessage;
import com.example.immosnap.data.MessageAdapter;
import com.example.immosnap.data.User;
import com.example.immosnap.data.UserAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListMessageActivity extends AppCompatActivity {

    private ArrayList<ImmoMessage> messageList;
    User loggedUser;

    Button btnAgentContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_message);

        btnAgentContact = findViewById(R.id.buttonAgentContact);

        final Intent intent = getIntent();
        loggedUser = (User) intent.getSerializableExtra("loggedUser");
        messageList = loggedUser.getMessageList();
        if (!loggedUser.getRenter()){
            btnAgentContact.setVisibility(View.INVISIBLE);
        }
        creatList();

        btnAgentContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent = new Intent(v.getContext(), ListUserActivity.class);
                messageIntent.putExtra("sender", loggedUser);
                startActivity(messageIntent);
                finish();
            }
        });
    }

    public void creatList(){
        RecyclerView messageListView = findViewById(R.id.recyclerViewMessage);
        messageListView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        messageListView.setLayoutManager(linearLayoutManager);
        MessageAdapter messageAdapter = new MessageAdapter(this, messageList, loggedUser);
        messageListView.setAdapter(messageAdapter);
    }
}