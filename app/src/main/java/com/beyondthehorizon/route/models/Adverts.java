package com.beyondthehorizon.route.models;

import android.net.Uri;

public class Adverts {
    String title;
    String description;
    Uri icon_url;

    public Adverts(Uri url, String name, String explanation) {
        this.title = name;
        this.description = explanation;
        this.icon_url = url;
    }

    public Uri getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(Uri icon_url) {
        this.icon_url = icon_url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}