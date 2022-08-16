package com.example.petcare;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Dog extends Pet {

    private VaccineRecord vaccineRecord ;

    public Dog(String petType, String petName, Date bornDate, String imgUrl) {
        super(petType,petName,bornDate,imgUrl);
        this.vaccineRecord=new VaccineRecord();
    }



    public VaccineRecord getVaccineRecord() {
        return vaccineRecord;
    }

    public void setVaccineRecord(VaccineRecord vaccineRecord) {
        this.vaccineRecord = vaccineRecord;
    }
}
