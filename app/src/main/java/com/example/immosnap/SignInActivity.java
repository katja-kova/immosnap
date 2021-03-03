package com.example.immosnap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

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

import com.example.immosnap.data.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {

    final int MY_PERMISSIONS_STORAGE = 100;
    public ArrayList<User> users;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        String[] permissions =
                {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions,
                MY_PERMISSIONS_STORAGE);

        this.load(this.getApplicationContext());
        this.login();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onRestart() {
        super.onRestart();
        load(this.getApplicationContext());
    }

    public void login() {

        final ArrayList<User> tmpUsers = this.users;

        final EditText username = (EditText)findViewById(R.id.UserEmail);
        final EditText password = (EditText)findViewById(R.id.UserPassword);

        Button signInButton = (Button) findViewById(R.id.buttonSignIn);

        signInButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                String tmpUsername = username.getText().toString();
                String tmpPassword = password.getText().toString();

                boolean auth = false;

                User foundUser = new User();

                for(User user: tmpUsers){
                    if(user.getUsername().equals(tmpUsername)){
                        if(user.getPassword().equals(tmpPassword)){
                            auth = true;
                            foundUser = user;
                            break;
                        } else {
                            auth = false;
                            break;
                        }
                    }
                }

                if(!auth){
                    notificationDialog();
                } else {
                    Intent intent =  new Intent(v.getContext(), ListImmoActivity.class);
                    intent.putExtra("loggedUser", foundUser);
                    startActivity(intent);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void load(Context c) {

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            Intent intent = getIntent();
            boolean renter = (boolean)intent.getSerializableExtra("renter");

            User[] immoUsers = objectMapper.readValue(loadJSONFromStorage(c), User[].class);
            this.users = new ArrayList<>(Arrays.asList(immoUsers));

            for(User u: this.users){
                if(renter){
                    if(!u.getRenter()) {
                        this.users.remove(u);
                    }
                } else {
                    if(u.getRenter()){
                        this.users.remove(u);
                    }
                }
            }
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
            copyFromAssets(c);
            //ex.printStackTrace();
            return null;
        }
        return json;
    }

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
    private void notificationDialog() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "Immo Notification";
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
                .setContentText("Wrong credentials")
                .setContentInfo("Error")
                .setColor(Color.RED);

        notificationManager.notify(1, notificationBuilder.build());

    }
}
