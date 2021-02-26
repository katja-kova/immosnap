package com.example.immosnap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.immosnap.data.CustomAdapter;
import com.example.immosnap.data.ImmoObject;
import com.example.immosnap.data.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class SignUpActivity extends AppCompatActivity {

    final int MY_PERMISSIONS_STORAGE = 100;
    public ArrayList<User> users;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        String[] permissions =
                {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions,
                MY_PERMISSIONS_STORAGE);

        load(this.getApplicationContext());
        this.createAppUser();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onRestart() {
        super.onRestart();
        load(this.getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void load(Context c) {

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            User[] immoUsers = objectMapper.readValue(loadJSONFromStorage(c), User[].class);
            this.users = new ArrayList<>(Arrays.asList(immoUsers));

        }
        catch (Exception e) {
            Toast.makeText(c, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void createAppUser(){

        final ArrayList<User> tmpUsers = new ArrayList<>();

        Intent intent = getIntent();
        final boolean renter = (boolean)intent.getSerializableExtra("renter");

        for(User u: this.users){
            if(renter){
                if(u.getRenter()) {
                    tmpUsers.add(u);
                }
            } else {
                if(!u.getRenter()){
                    tmpUsers.add(u);
                }
            }
        }

        final EditText username = (EditText)findViewById(R.id.UserName);
        final EditText email = (EditText)findViewById(R.id.UserEmail);
        final EditText password = (EditText)findViewById(R.id.UserPassword);

        Button signUpButton = (Button) findViewById(R.id.buttonSignUp);

        signUpButton.setOnClickListener(new View.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                String tmpUsername = username.getText().toString();
                String tmpEmail = email.getText().toString();
                String tmpPassword = password.getText().toString();

                if (tmpUsername.length() == 0 || tmpEmail.length() == 0 || tmpPassword.length() == 0) {
                    notificationDialog("Please provide your credentials");
                } else {
                    boolean exists = false;

                    for (User user : tmpUsers) {
                        if (user.getUsername().equals(tmpUsername)) {
                            exists = true;
                            break;
                        }
                    }

                    if (exists) {
                        notificationDialog("Sorry, this username is already taken");
                    } else {
                        User newUser = new User();
                        newUser.setUsername(tmpUsername);
                        newUser.setEmail(tmpEmail);
                        newUser.setPassword(tmpPassword);
                        newUser.setRenter(renter);

                        users.add(newUser);
                        updateJSONFile(users);

                        Intent intent = new Intent(v.getContext(), ListImmoActivity.class);
                        intent.putExtra("loggedUser", newUser);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public void moveToSignIn(View view){

        Intent intent = getIntent();
        boolean renter = (boolean)intent.getSerializableExtra("renter");

        intent = new Intent(this, SignInActivity.class);
        intent.putExtra("renter", renter);
        startActivity(intent);
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
            copyFromAssets(c);
            //ex.printStackTrace();
            return null;
        }
        return json;
    }

    //copy json-Datei von Assets, um zzu bearbeiten
    public void copyFromAssets(Context context){
        String jsonData = loadJSONFromAsset(context);
        try {
            FileOutputStream outputStream = openFileOutput("immouser.json", MODE_PRIVATE);
            outputStream.write(jsonData.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Datei in Assets ist read-only, kann mann nicht bearbeiten
    public String loadJSONFromAsset(Context c) {
        String json = null;
        try {

            InputStream is = c.getAssets().open("immouser.json");
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notificationDialog(String msg) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "ImmoUser Notification";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("immosnap")
                .setContentTitle("Error")
                .setContentText(msg)
                .setContentInfo("Information");
        notificationManager.notify(1, notificationBuilder.build());
    }

    public void updateJSONFile(ArrayList<User> list){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            OutputStream outputStream = openFileOutput("immouser.json", MODE_PRIVATE);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputStream, list);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
