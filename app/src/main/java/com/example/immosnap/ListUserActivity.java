package com.example.immosnap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.immosnap.data.CustomAdapter;
import com.example.immosnap.data.ImmoMessage;
import com.example.immosnap.data.ImmoObject;
import com.example.immosnap.data.User;
import com.example.immosnap.data.UserAdapter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ListUserActivity extends AppCompatActivity {

    private ArrayList<User> usersList;
    User loggedUser;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        load(this.getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void load(Context c) {


        try {

            Intent intent = getIntent();
            loggedUser = (User) intent.getSerializableExtra("sender");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            User[] users = objectMapper.readValue(loadJSONFromStorage(c), User[].class);
            usersList = new ArrayList<User>(Arrays.asList(users));

            if(!loggedUser.getRenter()){
                for (int i = 0; i < usersList.size(); i++){
                    if (!usersList.get(i).getRenter()){
                        usersList.remove(i);
                    }
                }
            }
            else {
                for (int i = 0; i < usersList.size(); i++){
                    if (usersList.get(i).getRenter()){
                        usersList.remove(i);
                    }
                }
            }

            RecyclerView userListView = findViewById(R.id.userListview);
            userListView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            userListView.setLayoutManager(linearLayoutManager);
            UserAdapter userAdapter = new UserAdapter(this, usersList, loggedUser);
            userListView.setAdapter(userAdapter);

        }
        catch (Exception e) {
            Toast.makeText(c, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // load and read json-Data
    public String loadJSONFromStorage(Context c) {
        String json = null;
        try {
            InputStream is = openFileInput("immouser.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            // file in Storage not exists
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}