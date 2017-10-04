package com.bsn.mediassist.data;

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


    public User() {
    }

    public User(String age, String doctorName, String doctorNumber, String gender, String isAthlete, String isBloodPressure, String name, String relativeName1, String relativeName2, String relativeNumber1, String relativeNumber2) {
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
    }


    @Override
    public String toString() {

        String user =
                "Name: " + name + "\n" +
                        "age: " + age + "\n" +
                        "Gender: " + gender + "\n" +
                        "Athlete: " + isAthlete + "\n" + "Gender: " + gender + "\n" +
                        "BloodPressure: " + isBloodPressure + "\n" +

                        "DoctorName: " + doctorName + "\n" +
                        "DoctorNumber: " + doctorNumber + "\n" +

                        "Relative1: " + relativeName1 + "\n" +
                        "Relative #1: " + relativeNumber1 + "\n" +
                        "Relative2: " + relativeName2 + "\n" +

                        "Relative #2: " + relativeNumber2 + "\n";


        return user;


    }
}
