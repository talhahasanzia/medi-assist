package com.bsn.mediassist.login;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {


    public String name;
    public String username;
    public String email;
    public String gender;
    public String age;
    public String bloodPressure;
    public String athlete;
    public String doctorName;
    public String doctorPhone;

    public String relativeName1;
    public String relativePhone1;
    public String relativeName2;
    public String relativePhone2;


    public User(String name, String username, String email, String gender, String age, String bloodPressure, String athlete, String doctorName, String doctorPhone, String relativeName1, String relativePhone1, String relativeName2, String relativePhone2) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.bloodPressure = bloodPressure;
        this.athlete = athlete;
        this.doctorName = doctorName;
        this.doctorPhone = doctorPhone;
        this.relativeName1 = relativeName1;
        this.relativePhone1 = relativePhone1;
        this.relativeName2 = relativeName2;
        this.relativePhone2 = relativePhone2;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    

}