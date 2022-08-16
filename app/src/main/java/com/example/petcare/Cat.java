package com.example.petcare;

import java.util.ArrayList;
import java.util.Date;

public class Cat extends Pet {

    private ArrayList<String> vaccines;

    public Cat(String petType, String petName, Date bornDate,String imgUrl) {
        super(petType,petName,bornDate,imgUrl);
        this.vaccines=new ArrayList<String>();
    }
    public Cat(){}

    public ArrayList<String> getVaccines() {
        return vaccines;
    }

    public void setVaccines(String vaccines) {
        this.vaccines.add(vaccines);
    }

}
