package com.example.dummy.campusnavforblind;

public class Notification {

    private int id;
    private String title;
    private String shortdesc;

    //constructor
    public Notification(String title, String shortdesc) {

        this.title = title;
        this.shortdesc = shortdesc;

    }

    //return title
    public String getTitle() {
        return title;
    }

    // return description
    public String getShortdesc() {
        return shortdesc;
    }
}
