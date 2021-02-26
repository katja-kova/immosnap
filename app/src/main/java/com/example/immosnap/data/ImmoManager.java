package com.example.immosnap.data;

import java.util.ArrayList;

public class ImmoManager {

    private ArrayList<ImmoObject> immoObjects;

    public ImmoManager(ArrayList<ImmoObject> immoObjects){
        this.immoObjects = immoObjects;
    }

    public ArrayList<ImmoObject> getImmoObjects() {
        return immoObjects;
    }
}
