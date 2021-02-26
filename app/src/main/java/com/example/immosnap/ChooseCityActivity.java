package com.example.immosnap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.immosnap.data.User;

public class ChooseCityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);

        Intent intent = getIntent();
        User newUser = (User)intent.getSerializableExtra("newUser");
    }

    public void showResults(View view){
        Intent intent = new Intent(this, ListImmoActivity.class);
        startActivity(intent);
    }
}
