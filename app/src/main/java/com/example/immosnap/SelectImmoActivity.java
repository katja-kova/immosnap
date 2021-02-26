package com.example.immosnap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.immosnap.data.ImmoMessage;
import com.example.immosnap.data.ImmoObject;
import com.example.immosnap.data.User;
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

public class SelectImmoActivity extends AppCompatActivity {

    private int position;
    private User loggedUser;
    private ImmoObject immoObject;
    private ImmoMessage immoMessage;
    private ArrayList<User> contactUsers;
    final static int REQUEST_CODE = 1234;

    private ImageView imgObjectImage;
    private TextView txtObjectTitle;
    private TextView txtObjectPrice;
    private TextView txtObjectRoom;
    private Button btnContact;
    private ImageView imgFavorite;
    private TextView txtObjectAddress;
    private TextView txtObjectCommission;
    private TextView txtObjectDescription;
    private TextView txtCounter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_immo);

        mapping();
        loadContactUsers(this.getApplicationContext());
        createView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data !=null){
            User receiver = (User) data.getSerializableExtra("receiver");
            if (receiver.getRenter()){
                receiver.addMessageList(this.immoMessage);
                updateUserJSONFile(receiver, this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("SetTextI18n")
    private void createView(){

        //btnContact.setVisibility(View.INVISIBLE);
        //imgFavorite.setVisibility(View.INVISIBLE);
        this.txtObjectCommission.setVisibility(View.INVISIBLE);
        this.txtCounter.setVisibility(View.INVISIBLE);

        final Intent intent = getIntent();
        this.immoObject = (ImmoObject) intent.getSerializableExtra("immoObject");
        this.loggedUser = (User) intent.getSerializableExtra("loggedUser");
        this.position = intent.getIntExtra("position", 0);


        if (this.immoObject != null && this.loggedUser != null) {
            Drawable res;
            if (this.immoObject.getImg().substring(0, 4).equals("rent")){
                // object von Default-JSON-Datei
                String img = "@drawable/" + this.immoObject.getImg();
                int imageResource = this.getResources().getIdentifier(img, null, this.getPackageName());
                res = this.getResources().getDrawable(imageResource);
            }
            else {
                // new Object
                res = Drawable.createFromPath(this.immoObject.getImg());
            }

            this.imgObjectImage.setImageDrawable(res);
            this.txtObjectTitle.setText(this.immoObject.getTitle());
            this.txtObjectPrice.setText(this.txtObjectPrice.getText() + ": " + this.immoObject.getPrice() + " €");
            this.txtObjectRoom.setText(this.txtObjectRoom.getText() + ": " + this.immoObject.getRooms());
            this.txtObjectAddress.setText(this.txtObjectAddress.getText() + ": " + this.immoObject.getAddress());
            this.txtObjectDescription.setText(this.txtObjectDescription.getText() + ":\n" + this.immoObject.getDescription());

            if (this.loggedUser.getRenter()) {

                createViewRenter();

            } else {

                createViewAgent();

            }
        }
    }

    private void createViewRenter(){

        //btnContact.setVisibility(View.VISIBLE);
        //imgFavorite.setVisibility(View.VISIBLE);
        this.txtCounter.setVisibility(View.INVISIBLE);
        this.btnContact.setText("Viewing requests");

        this.btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                immoMessage = new ImmoMessage(loggedUser.getUsername(), immoObject);
                for (User u : contactUsers){
                    if (!u.getRenter()){
                        boolean requested = false;
                        for (ImmoMessage imsg : u.getViewingRequestList()){
                            if (imsg.equals(immoMessage)){
                                requested = true;
                            }
                        }
                        if (!requested){
                            u.addViewingRequestList(immoMessage);
                            updateUserJSONFile(u, v.getContext());
                            Toast.makeText(v.getContext(), "Send request", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(v.getContext(), "Requested", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        for(ImmoObject favObject: loggedUser.getSelectedImmos()){
            if(immoObject.equals(favObject)) {
                this.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
            }
        }

        this.imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean found=false;
                for(ImmoObject favObject: loggedUser.getSelectedImmos()){
                    if(immoObject.equals(favObject)){
                        found=true;
                    }
                }

                if(!found){
                    imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                    loggedUser.addSelectedImmos(immoObject);
                    updateUserJSONFile(loggedUser, v.getContext());
                }else {
                    imgFavorite.setImageResource(R.drawable.ic_outline_favorite_border_24);
                    loggedUser.removeSelectedImmos(immoObject);
                    updateUserJSONFile(loggedUser, v.getContext());
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void createViewAgent(){

        this.txtObjectCommission.setVisibility(View.VISIBLE);
        this.txtObjectCommission.setText(this.txtObjectCommission.getText() + ": " + this.immoObject.getCommission() + "%");
        this.imgFavorite.setImageResource(R.drawable.ic_round_visibility_24);

        //Besichtiungswünsche zählen:
        this.txtCounter.setVisibility(View.VISIBLE);
        int viewingRequestsCounter = 0;
        for (ImmoMessage imsg : this.loggedUser.getViewingRequestList()){
            if (imsg.getSelectedImmoObject().equals(immoObject)){
                viewingRequestsCounter++;
            }
        }
        this.txtCounter.setText(String.valueOf(viewingRequestsCounter));

        this.btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                immoMessage = new ImmoMessage(loggedUser.getUsername(), immoObject);
                Intent intent1 = new Intent(v.getContext(), ListUserActivity.class);
                intent1.putExtra("sender", loggedUser);
                startActivityForResult(intent1, REQUEST_CODE);
            }
        });
    }

    private void loadContactUsers(Context c){
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            User[] immoUsers = objectMapper.readValue(loadUserJSONFromStorage(c), User[].class);
            this.contactUsers = new ArrayList<>(Arrays.asList(immoUsers));

        }
        catch (Exception e) {
            Toast.makeText(c, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void mapping(){
        imgObjectImage = (ImageView) findViewById(R.id.imageViewObjectImg);
        txtObjectTitle = (TextView) findViewById(R.id.textViewObjectTitle);
        txtObjectPrice = (TextView) findViewById(R.id.textViewObjectPrice);
        txtObjectRoom = (TextView) findViewById(R.id.textViewObjectRoom);
        btnContact = (Button) findViewById(R.id.buttonContact);
        imgFavorite = (ImageView) findViewById(R.id.imageViewFavorite);
        txtObjectAddress = (TextView) findViewById(R.id.textViewObjectAddress);
        txtObjectCommission = (TextView) findViewById(R.id.textViewObjectCommission);
        txtObjectDescription = (TextView) findViewById(R.id.textViewObjectDescription);
        txtCounter = (TextView) findViewById(R.id.textViewCounter);
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
}
