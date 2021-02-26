package com.example.immosnap.data;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class User implements Serializable {

    private String username;
    private String email;
    private String password;
    private boolean renter;
    private ArrayList<ImmoMessage> messageList = new ArrayList<>();
    private ArrayList<ImmoObject> selectedImmos = new ArrayList<>();
    private ArrayList<ImmoMessage> viewingRequestList = new ArrayList<>();

    public User(){
        super();
    }

    public User(String username, String email, String password, boolean renter, ArrayList<ImmoMessage> messageList, ArrayList<ImmoObject> selectedImmos){
        this.username = username;
        this.email = email;
        this.password = password;
        this.renter = renter;
        this.messageList = messageList;
        this.selectedImmos = selectedImmos;
    }

    public String getUsername(){
        return this.username;
    }
    public void setUsername(String name){
        this.username = name;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean getRenter(){
        return this.renter;
    }
    public void setRenter(boolean renter){
        this.renter = renter;
    }

    public ArrayList<ImmoMessage> getMessageList() {
        return messageList;
    }
    public void setMessageList(ArrayList<ImmoMessage> messageList) {this.messageList = messageList; }

    public ArrayList<ImmoObject> getSelectedImmos() { return selectedImmos; }
    public void setSelectedImmos(ArrayList<ImmoObject> selectedImmos) { this.selectedImmos = selectedImmos; }
    public void addSelectedImmos(ImmoObject immoObject) { this.selectedImmos.add(immoObject); }
    public void removeSelectedImmos(ImmoObject immoObject) {
        for (ImmoObject object : this.selectedImmos){
            if (object.equals(immoObject)){
                this.selectedImmos.remove(object);
            }
        }
    }

    public void addMessageList(ImmoMessage immoMessage) {
        this.messageList.add(0, immoMessage);
    }

    public ArrayList<ImmoMessage> getViewingRequestList() {
        return viewingRequestList;
    }
    public void addViewingRequestList(ImmoMessage immoMessage) {
        this.viewingRequestList.add(immoMessage);
    }

    public boolean equals(User user){
        boolean result = false;
        if(this.username.equals(user.getUsername())
                && this.email.equals(user.getEmail())
                && this.password.equals(user.getPassword())
                && this.renter == user.getRenter()
        ){
            result = true;
        }
        return result;
    }
}
