package com.example.user.sqlite;

/**
 * Created by user on 2015/7/30.
 */
public class Contact {
    //private variables
    String _id;
    String _name;

    String _subject;
    String _expertise;

    // Empty constructor
    public Contact(){

    }
    // constructor
    public Contact(String id, String name, String subject,String expertise){
        this._id = id;
        this._name = name;
        this._subject=subject;
        this._expertise=expertise;
    }

    // constructor
    public Contact(String name){
        this._name = name;

    }
    // getting ID
    public String getID(){
        return this._id;
    }

    // setting id
    public void setID(String id){
        this._id = id;
    }

    // getting name
    public String getName(){
        return this._name;
    }

    // setting name
    public void setName(String name){
        this._name = name;
    }

    // getting subject
    public String getSubject(){
        return this._subject;
    }

    // setting SUBJECT
    public void setSubject(String subject){
        this._subject = subject;
    }

    // getting subject
    public String getExpertise(){
        return this._expertise;
    }

    // setting phone number
    public void setExpertise(String expertise){
        this._expertise = expertise;
    }


}
