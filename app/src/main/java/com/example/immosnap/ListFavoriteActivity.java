package com.example.immosnap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.immosnap.data.CustomAdapter;
import com.example.immosnap.data.ImmoObject;
import com.example.immosnap.data.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ListFavoriteActivity extends AppCompatActivity {

    ArrayList<User> users;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_favorite);

        Intent intent = getIntent();
        this.user = (User) intent.getSerializableExtra("loggedUser");

        this.loadUsers(this.getApplicationContext());
        this.createList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        this.loadUsers(this.getApplicationContext());
        this.createList();
    }

    private void createList() {

        for(User u: this.users){
            if(u.getUsername().equals(this.user.getUsername())){
                this.user = u;
            }
        }

        RecyclerView favoriteList = findViewById(R.id.recyclerViewFavorite);
        favoriteList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        favoriteList.setLayoutManager(linearLayoutManager);
        CustomAdapter customAdapter = new CustomAdapter(this, user.getSelectedImmos(), user);
        favoriteList.setAdapter(customAdapter);
    }
    private void loadUsers(Context c){
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            User[] immoUsers = objectMapper.readValue(loadUserJSONFromStorage(c), User[].class);
            this.users = new ArrayList<>(Arrays.asList(immoUsers));

        }
        catch (Exception e) {
            Toast.makeText(c, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public String loadUserJSONFromStorage(Context c) {
        String json = null;
        try {
            InputStream is = openFileInput("immouser.json");
            //Toast.makeText(this, getFilesDir() + "immoobject.json", Toast.LENGTH_LONG).show();
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}