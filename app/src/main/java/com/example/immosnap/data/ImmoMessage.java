package com.example.immosnap.data;

import java.io.Serializable;

public class ImmoMessage implements Serializable {

    private String immoUserName;
    private ImmoObject selectedImmoObject;

    public ImmoMessage(){}

    public ImmoMessage(String immoUserName, ImmoObject selectedImmoObject) {
        this.immoUserName = immoUserName;
        this.selectedImmoObject = selectedImmoObject;
    }

    public String getImmoUserName() {
        return immoUserName;
    }
    public void setImmoUserName(String immoUserName) {
        this.immoUserName = immoUserName;
    }

    public ImmoObject getSelectedImmoObject() {
        return selectedImmoObject;
    }
    public void setSelectedImmoObject(ImmoObject selectedImmoObject) { this.selectedImmoObject = selectedImmoObject; }

    public boolean equals(ImmoMessage immoMessage){
        boolean result = false;
        if(this.immoUserName.equals(immoMessage.getImmoUserName())
                && this.selectedImmoObject.equals(immoMessage.getSelectedImmoObject())
        ){
            result = true;
        }
        return result;
    }
}
