package com.example.petcare;

import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Date;

public class VaccineRecord {

    private ArrayList<Vaccine>arrayList;


    public VaccineRecord() {
        //makes a empty arrayList for the vaccines
        this.arrayList = new ArrayList<>();
    }

    public ArrayList getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList arrayList) {
        this.arrayList = arrayList;
    }
    public void addVaccine(Vaccine vaccine)
    {
        arrayList.add(vaccine);
    }

    @Override
    public String toString() {
        return "VaccineRecord{" +
                "arrayList=" + arrayList +
                '}';
    }
}
