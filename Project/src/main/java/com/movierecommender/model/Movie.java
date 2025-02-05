package com.movierecommender.model;

public class Movie {
    private String title;
    private double rating;
    private int year;

    // Constructor
    public Movie(String title, double rating, int year) {
        this.title = title;
        this.rating = rating;
        this.year = year;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public double getRating() {
        return rating;
    }

    public int getYear() {
        return year;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
