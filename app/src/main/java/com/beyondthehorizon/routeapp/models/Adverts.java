package com.beyondthehorizon.routeapp.models;
public class Adverts {
    String title;
    String description;

    public Adverts(String name, String explanation) {
        this.title = name;
        this.description = explanation;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}