package com.example.petcare;

import java.util.Date;

public class Pet {
    private String petName;
    private String petType;
    private Date bornDate;
    private String imgUrl;

    public Pet(String petType,String petName,Date bornDate,String imgUrl)
    {
        this.petName=petName;
        this.petType=petType;
        this.bornDate=bornDate;
        this.imgUrl="image"+imgUrl;
    }
    public Pet(){}

    public String getPetName() {
        return petName;
    }
    public Date getBornDate() {
        return bornDate;
    }
    public void setBornDate(Date dateToSet) {
        this.bornDate=dateToSet;
    }
    public void setPetName(String petName) {
        this.petName = petName;
    }
    public String getPetType() {
        return petType;
    }
    public void setPetType(String petType) {
        this.petType = petType;
    }
    public String getPetImgUrl() {
        return imgUrl;
    }
    public void setPetImgUrl(String petImgUrl) {
        this.imgUrl = petImgUrl;
    }




}
