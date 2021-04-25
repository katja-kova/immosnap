package com.example.immosnap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.os.ConfigurationCompat;

import com.example.immosnap.data.User;

public class MainActivity extends AppCompatActivity {

    final int MY_PERMISSIONS_STORAGE = 100;
    private boolean renter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);
        Button changeLang = findViewById(R.id.switchLanguage);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLanguage();
            }
        });

        String[] permissions =
                {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions,
                MY_PERMISSIONS_STORAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){}

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void switchLanguage() {

        Locale current = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0);
        if(current.getLanguage()=="de"){
            setLocale("en");
            recreate();
        } else{
            setLocale("de");
            recreate();
        }

    }

    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("Language", lang);
        editor.apply();
    }

    public void loadLocale(){
        
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("Language", "");
        setLocale(language);
        
    }

    public void createAgentUser(View view) {

        this.renter = false;

        this.moveToSignUp();
    }

    public void createRenterUser(View view) {

        this.renter = true;

        this.moveToSignUp();
    }

    private void moveToSignUp() {

        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra("renter", renter);
        startActivity(intent);

    }

}
