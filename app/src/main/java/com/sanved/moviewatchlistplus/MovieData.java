package com.sanved.moviewatchlistplus;

/**
 * Created by Sanved on 06-08-2017.
 */

public class MovieData {

    String link, name, year, imdb;

    public MovieData(String link, String name, String year, String imdb){
        this.link = link;
        this.name = name;
        this.year = year;
        this.imdb = imdb;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getImdb() {
        return imdb;
    }
}
