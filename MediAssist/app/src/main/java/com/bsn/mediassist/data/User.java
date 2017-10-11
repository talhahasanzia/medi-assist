package com.bsn.mediassist.data;

import android.os.Bundle;

/**
 * Created by me on 04-Oct-17.
 */

public class User {

    public String age;

    public String doctorName;

    public String doctorNumber;

    public String gender;

    public String isAthlete;

    public String isBloodPressure;

    public String name;

    public String relativeName1;

    public String relativeName2;

    public String relativeNumber1;

    public String relativeNumber2;

    public String monitoringData;


    public User() {
    }

    public User(String age, String doctorName, String doctorNumber, String gender, String isAthlete, String isBloodPressure, String name, String relativeName1, String relativeName2, String relativeNumber1, String relativeNumber2, String monitoringData) {
        this.age = age;
        this.doctorName = doctorName;
        this.doctorNumber = doctorNumber;
        this.gender = gender;
        this.isAthlete = isAthlete;
        this.isBloodPressure = isBloodPressure;
        this.name = name;
        this.relativeName1 = relativeName1;
        this.relativeName2 = relativeName2;
        this.relativeNumber1 = relativeNumber1;
        this.relativeNumber2 = relativeNumber2;
        this.monitoringData=monitoringData;
    }


    @Override
    public String toString() {

        String user =
                "Name: " + name + "\n" +
                        "age: " + age + "\n" +
                        "Gender: " + gender + "\n" +
                        "Athlete: " + isAthlete + "\n" +
                        "BloodPressure: " + isBloodPressure + "\n" +

                        "DoctorName: " + doctorName + "\n" +
                        "DoctorNumber: " + doctorNumber + "\n" +

                        "Relative1: " + relativeName1 + "\n" +
                        "Relative #1: " + relativeNumber1 + "\n" +
                        "Relative2: " + relativeName2 + "\n" +

                        "Relative #2: " + relativeNumber2 + "\n";


        return user;


    }


    public Bundle toBundle() {

        Bundle bundle = new Bundle();


        bundle.putString("age", age);
        bundle.putString("doctorName", doctorName);
        bundle.putString("doctorNumber", doctorNumber);
        bundle.putString("gender", gender);
        bundle.putString("isAthlete", isAthlete);
        bundle.putString("isBloodPressure", isBloodPressure);
        bundle.putString("name", name);
        bundle.putString("relative1Name", relativeName1);
        bundle.putString("relative1Number", relativeNumber1);
        bundle.putString("relative2Name", relativeName2);
        bundle.putString("relative2Number", relativeNumber2);
        bundle.putString("monitoringData", monitoringData);


        return bundle;

    }

    public static User fromBundle(Bundle bundle) {

        return new User(bundle.getString("age"),
                bundle.getString("doctorName"),
                bundle.getString("doctorNumber"),


                bundle.getString("gender"),
                bundle.getString("isAthlete"),
                bundle.getString("isBloodPressure"),
                bundle.getString("name"),
                bundle.getString("relative1Name"),
                bundle.getString("relative2Name"),
                bundle.getString("relative1Number"),

                bundle.getString("relative2Number"),
                bundle.getString("monitoringData")

                );


    }
}
