package com.example.immosnap;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.immosnap.data.ImmoObject;

public class ReadRequestActivity extends AppCompatActivity {

    TextView txtSenderName;
    TextView txtToRent;
    TextView txtLocation;
    TextView txtPrice;
    TextView txtRooms;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_request);
        mapping();

        Intent intent = getIntent();
        String senderName = intent.getStringExtra("senderName");
        ImmoObject immoObject = (ImmoObject) intent.getSerializableExtra("immoObject");

        txtSenderName.setText(txtSenderName.getText().toString() + " " + senderName);
        if (immoObject.isToRent()){
            txtToRent.setText(R.string.rent);
        }
        else{
            txtToRent.setText(R.string.sale);
        }
        txtLocation.setText(txtLocation.getText().toString() + " " + immoObject.getAddress());
        txtPrice.setText(txtPrice.getText().toString() + " " + immoObject.getPrice() + "€");
        txtRooms.setText(txtRooms.getText().toString() + " " + immoObject.getRooms());
    }

    public void mapping(){
        txtSenderName = (TextView) findViewById(R.id.textViewSenderName);
        txtToRent = (TextView) findViewById(R.id.textViewReqToRent);
        txtLocation = (TextView) findViewById(R.id.textViewReqLocation);
        txtPrice = (TextView) findViewById(R.id.textViewReqPrice);
        txtRooms = (TextView) findViewById(R.id.textViewReqRoom);
    }
}