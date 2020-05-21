package com.kase.wallyapp.model;

public class FirebaseUsers {

    String userName;
    String phoneNumber;
    String dob;

    public FirebaseUsers(){

    }

    public FirebaseUsers(String name, String phone, String date){
        userName = name;
        phoneNumber = phone;
        dob = date;
    }
}
