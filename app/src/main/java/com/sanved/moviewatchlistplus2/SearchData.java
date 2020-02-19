package com.sanved.moviewatchlistplus2;

/**
 * Created by Sanved on 15-08-2017.
 */

public class SearchData {

    // Constructor
    String link, name, year, imdb;

    public SearchData(String link, String name, String year, String imdb){
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
