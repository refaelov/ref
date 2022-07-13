package com.example.petcare;

import java.util.ArrayList;
import java.util.Date;

public class Dog extends Pet {

    private ArrayList<String> vaccines;

    public Dog(String petType,String petName,Date bornDate) {
        super(petType,petName,bornDate);
        this.vaccines=new ArrayList<String>();
    }

    public ArrayList<String> getVaccines() {
        return vaccines;
    }

    public void setVaccines(String vaccines) {
        this.vaccines.add(vaccines);
    }



}
