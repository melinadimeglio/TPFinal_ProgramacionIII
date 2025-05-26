package com.example.demo.enums;

public enum UserPreferences {

    CULTURAL("cultural"),
    HISTORIC("historic"),
    RELIGION("religion"),
    NATURAL("natural"),
    BEACHES("beaches"),
    SPORT("sport"),
    FOODS("foods"),
    ADULT("adult"),
    SHOPS("shops"),
    AMUSEMENTS("amusements"),
    ARCHITECTURE("architecture"),
    INDUSTRIAL_FACILITIES("industrial_facilities"),
    VIEW_POINTS("view_points"),
    WAYS("ways"),
    OTHER("other");

    private final String kindApi;

    UserPreferences(String kindApi) {
        this.kindApi = kindApi;
    }

    public String getKindApi() {
        return kindApi;
    }
}
