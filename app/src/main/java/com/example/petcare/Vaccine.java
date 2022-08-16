package com.example.petcare;

import java.util.Date;
import java.util.UUID;

public class Vaccine {

    private String uid;
    private Date date;
    private String description;

    public Vaccine(Date date, String description) {
        this.uid = UUID.randomUUID().toString();
        this.date = date;
        this.description = description;
    }

    public Vaccine() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Vaccine{" +
                "uid='" + uid + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }
}
