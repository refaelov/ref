package com.example.petcare;

import java.util.Date;

public class Parrot extends Pet {

    public Parrot(String petType, String petName, Date bornDate,String imgUrl) {
        super(petType,petName,bornDate,imgUrl);
    }
    public Parrot(){super();}


    public String toString()
    {
        return super.getPetName();
    }


}
