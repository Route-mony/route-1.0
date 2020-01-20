package com.beyondthehorizon.routeapp.Models;

public class Movies {
    String name;
    String explanation;

    public Movies(String name, String explanation) {
        this.name = name;
        this.explanation = explanation;
    }

    public String getName() {
        return name;
    }

    public String getExplanation() {
        return explanation;
    }
}