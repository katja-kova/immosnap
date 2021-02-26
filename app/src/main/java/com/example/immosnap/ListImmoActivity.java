package com.example.immosnap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.example.immosnap.data.CustomAdapter;
import com.example.immosnap.data.ImmoManager;
import com.example.immosnap.data.ImmoObject;
import com.example.immosnap.data.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ListImmoActivity extends AppCompatActivity {

    final int MY_PERMISSIONS_STORAGE = 100;
    private User user;
    private ArrayList<ImmoObject> immos;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_immo);

        Intent intent = getIntent();
        this.user = (User) intent.getSerializableExtra("loggedUser");

        String[] permissions =
                {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions,
                MY_PERMISSIONS_STORAGE);

        load(this.getApplicationContext());

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onRestart() {
        super.onRestart();
        load(this.getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            load(this.getApplicationContext());
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (!user.getRenter()){
            getMenuInflater().inflate(R.menu.menu_list_immo, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.menu_list_immo_user, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuNewOffer:
                Intent newOfferIntent = new Intent(ListImmoActivity.this, NewOfferActivity.class);
                newOfferIntent.putExtra("immoList", immos);
                startActivity(newOfferIntent);
                break;
            case R.id.menuFavorite:
                Intent favoriteIntent = new Intent(ListImmoActivity.this, ListFavoriteActivity.class);
                favoriteIntent.putExtra("favImmos", user.getSelectedImmos());
                favoriteIntent.putExtra("loggedUser", user);
                startActivity(favoriteIntent);
                break;
            case R.id.menuMessageUser:
                Intent messageUserIntent = new Intent(ListImmoActivity.this, ListMessageActivity.class);
                messageUserIntent.putExtra("loggedUser", user);
                startActivity(messageUserIntent);
                break;
            case R.id.menuMessage:
                Intent messageIntent = new Intent(ListImmoActivity.this, ListMessageActivity.class);
                messageIntent.putExtra("loggedUser", user);
                startActivity(messageIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void load(Context c) {

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            ImmoObject[] immoObjects = objectMapper.readValue(loadJSONFromStorage(c), ImmoObject[].class);
            this.immos = new ArrayList<>(Arrays.asList(immoObjects));

            takeUserFromJSON(c);

            RecyclerView immoList = findViewById(R.id.ImmoListView);
            immoList.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            immoList.setLayoutManager(linearLayoutManager);
            CustomAdapter customAdapter = new CustomAdapter(this, this.immos, this.user);
            immoList.setAdapter(customAdapter);

        }
        catch (Exception e) {
            Toast.makeText(c, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //Datei in Assets ist read-only, kann mann nicht bearbeiten
    public String loadJSONFromAsset(Context c) {
        String json = null;
        try {

            InputStream is = c.getAssets().open("immoobject.json");
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

    //copy json-Datei von Assets, um zu bearbeiten
    public void copyFromAssets(Context context){
        String jsonData = loadJSONFromAsset(context);
        try {
            FileOutputStream outputStream = openFileOutput("immoobject.json", MODE_PRIVATE);
            outputStream.write(jsonData.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // load and read json-Data
    public String loadJSONFromStorage(Context c) {
        String json = null;
        try {
            InputStream is = openFileInput("immoobject.json");
            //Toast.makeText(this, getFileStreamPath("immoobject.json").getPath() , Toast.LENGTH_LONG).show();
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

    public String loadUserJSONFromStorage(Context c) {
        String json = null;
        try {
            InputStream is = openFileInput("immouser.json");
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

    public void takeUserFromJSON(Context context){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            User[] userArray = objectMapper.readValue(loadUserJSONFromStorage(context), User[].class);
            ArrayList<User> userList = new ArrayList<>(Arrays.asList(userArray));
            for (User u : userList){
                if (u.equals(this.user)){
                    this.user = u;
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
