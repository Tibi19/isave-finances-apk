package com.tam.isave.model.info;

public class AppInformation {

    String title;
    String description;

    public AppInformation(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }

    public String getDescription() { return description; }
}
