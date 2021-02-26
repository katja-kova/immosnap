package com.example.immosnap.data;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class ImmoObject implements Serializable {

    private String img;
    private String title;
    private int price;
    private String address;
    private boolean toRent;
    private int rooms;
    private String description;
    private int commission;

    public ImmoObject(){
        super();
    }

    public ImmoObject(String img, String title, int price, String address, boolean toRent, int rooms, String description, int commission){
        this.img = img;
        this.title = title;
        this.price = price;
        this.address = address;
        this.toRent = toRent;
        this.rooms = rooms;
        this.description = description;
        this.commission = commission;
        //this.favorite = false;
    }

    public int getCommission() {
        return commission;
    }

    public void setCommission(int commission) {
        this.commission = commission;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isToRent() {
        return toRent;
    }

    public void setToRent(boolean toRent) {
        this.toRent = toRent;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(ImmoObject immoObject){
        boolean result = false;
        if(this.img.equals(immoObject.getImg())
                && this.title.equals(immoObject.getTitle())
                && this.price == immoObject.getPrice()
                && this.address.equals(immoObject.getAddress())
                && this.toRent == immoObject.isToRent()
                && this.rooms == immoObject.getRooms()
                && this.description.equals(immoObject.getDescription())
        ){
            result = true;
        }
        return result;
    }
}
