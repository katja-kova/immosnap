package com.example.immosnap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.immosnap.data.ImmoMessage;
import com.example.immosnap.data.ImmoObject;
import com.example.immosnap.data.User;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.String.valueOf;

public class RequestImmoActivity extends AppCompatActivity {

    String senderName;
    User receiver;
    TextView txtReceiverName;
    RadioButton rdbSale;
    RadioButton rdbRent;
    EditText edtLocation;
    EditText edtPrice;
    Spinner spnRoom;
    Button btnSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_immo);

        mapping();
        Intent intent = getIntent();
        senderName = intent.getStringExtra("sender");
        receiver = (User) intent.getSerializableExtra("receiver");
        takeUserFromJSON(this);

        txtReceiverName.setText(txtReceiverName.getText().toString() + " " + receiver.getUsername());

        final ArrayList<String> roomsList = new ArrayList<>();
        for (int i = 1; i <= 6 ; i++){
            roomsList.add(String.valueOf(i));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomsList);
        spnRoom.setAdapter(arrayAdapter);
        final String[] roomNum = new String[1];

        spnRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                roomNum[0] = roomsList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean toRent = false;
                String location;
                int price;
                int rooms = Integer.parseInt(roomNum[0]);

                if (rdbRent.isChecked()){
                    toRent = true;
                }
                location = edtLocation.getText().toString();
                price = Integer.parseInt(edtPrice.getText().toString());

                ImmoObject immoObject = new ImmoObject("", "", price, location, toRent, rooms, "", 0);
                ImmoMessage immoMessage = new ImmoMessage(senderName, immoObject);
                receiver.addMessageList(immoMessage);
                updateUserJSONFile(receiver, v.getContext());
                Toast.makeText(v.getContext(), "Send Message to Agent", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void mapping(){
        txtReceiverName = (TextView) findViewById(R.id.textViewReceiverName);
        rdbSale = (RadioButton) findViewById(R.id.radioButtonRequestSale);
        rdbRent = (RadioButton) findViewById(R.id.radioButtonRequestRent);
        edtLocation = (EditText) findViewById(R.id.editTextlocation);
        edtPrice = (EditText) findViewById(R.id.editTextNumberRequestPrice);
        spnRoom = (Spinner) findViewById(R.id.spinnerRoom);
        btnSendMessage = (Button) findViewById(R.id.buttonSendMessage);
    }

    public void updateUserJSONFile(User user, Context c){
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            User[] userArray = objectMapper.readValue(loadUserJSONFromStorage(c), User[].class);
            ArrayList<User> userList = new ArrayList<User>(Arrays.asList(userArray));
            for (int i = 0; i < userList.size(); i++){
                if (user.equals(userList.get(i))){
                    userList.set(i, user);
                }
            }
            OutputStream outputStream = openFileOutput("immouser.json", MODE_PRIVATE);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputStream, userList);
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
                if (u.equals(this.receiver)){
                    this.receiver = u;
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}