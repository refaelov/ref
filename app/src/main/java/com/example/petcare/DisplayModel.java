package com.example.petcare;

import java.util.Date;

public class DisplayModel {

    String petName,petAge ,petImgUrl,petType;
    Date bornDate;
    VaccineRecord vaccineRecord;

    DisplayModel(){

    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public DisplayModel(String petName, Date bornDate, String petImgUrl, String petType) {
        this.petName = petName;
        this.bornDate=bornDate;
        this.petType=petType;
        this.petImgUrl = petImgUrl;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public Date getBornDate() {
        return bornDate;
    }

    public void setBordDate(Date bordDate) {
        this.bornDate = bordDate;
    }

    public String getPetImgUrl() {
        return petImgUrl;
    }

    public void setPetImgUrl(String petImgUrl) {
        this.petImgUrl = petImgUrl;
    }

    public String getPetAge() {
        return petAge;
    }

    public void setPetAge(String petAge) {
        this.petAge = petAge;
    }

    public void setBornDate(Date bornDate) {
        this.bornDate = bornDate;
    }

    public VaccineRecord getVaccineRecord() {
        return vaccineRecord;
    }

    public void setVaccineRecord(VaccineRecord vaccineRecord) {
        this.vaccineRecord = vaccineRecord;
    }
}
