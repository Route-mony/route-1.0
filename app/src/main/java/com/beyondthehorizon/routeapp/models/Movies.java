package com.beyondthehorizon.routeapp.models;

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